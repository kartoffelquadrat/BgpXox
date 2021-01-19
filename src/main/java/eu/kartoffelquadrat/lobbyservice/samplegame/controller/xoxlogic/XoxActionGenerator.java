package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import com.google.gson.Gson;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxBoard;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Analyzes a Xox game and generates a collection of valid actions for a player.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class XoxActionGenerator implements ActionGenerator {

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
    private static Map<String, Action> emptyCellsToActions(XoxBoard board, PlayerReadOnly player) throws LogicException {
        Map<String, Action> actionMap = new LinkedHashMap();

        // Iterate over board
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                // Add an action if the position is free
                if (board.isFree(xPos, yPos)) {
                    Action action = new XoxClaimFieldAction(xPos, yPos, player);
                    String actionMd5 = DigestUtils.md5Hex(new Gson().toJson(action)).toUpperCase();
                    actionMap.put(actionMd5, action);
                }
            }
        }
        return actionMap;
    }

    /**
     * @param game   as the game instance for which the
     * @param player as the player object defining the participant for why tha action bundle shall be created. Can be
     *               null, if en empty actions set must be generated for an observer who does not actively participate
     *               in the game.
     * @return
     */
    @Override
    public Map<String, Action> generateActions(Game game, PlayerReadOnly player) throws LogicException {

        // Verify and cast the game type
        if (game.getClass() != XoxGame.class)
            throw new LogicException("Xox Action Generator can only handle Xox games.");
        XoxGame xoxGame = (XoxGame) game;

        // Non participants (observers) always receive an empty action bundle.
        if (player == null || !isParticipant(xoxGame, player))
            return new LinkedHashMap<>();

        // If the game is already over, return an empty set
        if (xoxGame.isFinished())
            return new LinkedHashMap<>();

        // If not the player's turn, return an empty set. (Check is performed by comparing the name of the current player)
        if (!player.getName().toLowerCase().equals(xoxGame.getCurrentPlayerName().toLowerCase()))
            return new LinkedHashMap<>();

        // Iterate over board and add an action for every unoccupied cell.
        return emptyCellsToActions(xoxGame.getBoard(), player);
    }
}

