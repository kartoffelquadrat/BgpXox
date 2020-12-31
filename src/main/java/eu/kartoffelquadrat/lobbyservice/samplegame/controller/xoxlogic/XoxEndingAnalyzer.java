package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

/**
 * Util class to determine whether a given xox board state implies that the game is ended.
 */
public class XoxEndingAnalyzer {

    /**
     * Verifies whether the game already fulfils any end criteria. Marks the game as finished, if at least criterion one
     * detected.
     *
     * @param game as the xox game object to be analysed and potentially flagged as finished.
     * @return boolean to indicate whether the game is already finished.
     */
    public static boolean analyzeAndUpdate(XoxGame game) {

        // check if board is full
        if (game.getBoard().isFull()) {
            game.markAsFinished();
            return true;
        }

        // check if one player has 3 in a line
        if (game.getBoard().isThreeInALine()) {
            game.markAsFinished();
            return true;
        }

        // No end criteria matched. Game is still running.
        return false;
    }
}
