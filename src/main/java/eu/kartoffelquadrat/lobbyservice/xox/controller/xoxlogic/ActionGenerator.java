package eu.kartoffelquadrat.lobbyservice.xox.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.xox.model.XoxBoard;
import eu.kartoffelquadrat.lobbyservice.xox.model.XoxGame;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Analyzes a Xox game and generates a collection of valid actions for a player.
 */
public class ActionGenerator {

    /**
     * @param game
     * @param playerName
     * @return
     */
    public static Collection<XoxAction> generateActions(XoxGame game, String playerName) {

        // Verify that the provided player is a game participant
        if (!isParticipant(game, playerName))
            throw new LogicException("Actions can no be generated for player " + playerName + ". Is not a participant of the game.");

        // If not the player's turn, return an empty set. (Check is performed by comparing the name of the current player)
        if (playerName.equals(game.getCurrentPlayerName()))
            return new LinkedList<>();

        // Iterate over board and add an action for every unoccupied cell.
        return emptyCellsToActions(game.getBoard());
    }

    /**
     * Verifies if a provided player is a valid participant (player) of a Xox game. The verification runs a case
     * sensitive string comparison of the player names.
     *
     * @param game       as the xox game.
     * @param playerName as the name of the player to test.
     * @return a boolean, indicating whether the provided name is a valid player name.
     */
    private static boolean isParticipant(XoxGame game, String playerName) {
        return game.getPlayerInfo(0).getName().equals(playerName) ||
                game.getPlayerInfo(1).getName().equals(playerName);
    }

    /**
     * Iterates over all cells of a provided Xox-Board and creates an action object for every unoccupied cell.
     *
     * @param board as the 3x3 grid to be analyzed.
     * @return
     */
    private static Collection<XoxAction> emptyCellsToActions(XoxBoard board) {
        Collection<XoxAction> actions = new LinkedList<>();

        // Iterate over board
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                // Add an action if the position is free
                if (board.isFree(xPos, yPos))
                    actions.add(new XoxAction(xPos, yPos));
            }
        }

        return actions;
    }
}

