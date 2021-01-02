package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxBoard;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Analyzes a Xox game and generates a collection of valid actions for a player.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class XoxActionGenerator implements ActionGenerator {

    /**
     * @param game   as the game instance for which the
     * @param player as the player object defining the participant for why tha action bundle shall be created.
     * @return
     */
    public Action[] generateActions(XoxGame game, PlayerReadOnly player) throws LogicException {

        // Verify that the provided player is a game participant
        if (!isParticipant(game, player))
            throw new LogicException("Actions can no be generated for player " + player.getName() + ". Is not a participant of the game.");

        // If not the player's turn, return an empty set. (Check is performed by comparing the name of the current player)
        if (player.equals(game.getCurrentPlayerName()))
            return new Action[0];

        // Iterate over board and add an action for every unoccupied cell.
        return emptyCellsToActions(game.getBoard(), player);
    }

    /**
     * Verifies if a provided player is a valid participant (player) of a Xox game. The verification runs a case
     * sensitive string comparison of the player names.
     *
     * @param game   as the xox game.
     * @param player as the player object of the participant to test.
     * @return a boolean, indicating whether the provided name is a valid player name.
     */
    private static boolean isParticipant(XoxGame game, PlayerReadOnly player) {
        return game.getPlayerInfo(0).getName().equals(player.getName()) ||
                game.getPlayerInfo(1).getName().equals(player.getName());
    }

    /**
     * Iterates over all cells of a provided Xox-Board and creates an action object for every unoccupied cell.
     *
     * @param board as the 3x3 grid to be analyzed.
     * @return an array of possible lay actions.
     */
    private static Action[] emptyCellsToActions(XoxBoard board, PlayerReadOnly player) throws LogicException {
        Collection<XoxClaimFieldAction> actions = new LinkedList<>();

        // Iterate over board
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                // Add an action if the position is free
                if (board.isFree(xPos, yPos))
                    actions.add(new XoxClaimFieldAction(xPos, yPos, player));
            }
        }

        return actions.toArray(new Action[actions.size()]);
    }
}

