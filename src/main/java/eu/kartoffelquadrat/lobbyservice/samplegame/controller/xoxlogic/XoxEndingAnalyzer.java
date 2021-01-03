package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.EndingAnalyzer;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.springframework.stereotype.Component;

/**
 * Util class to determine whether a given xox board state implies that the game is ended.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class XoxEndingAnalyzer implements EndingAnalyzer {

    /**
     * Verifies whether the game already fulfils any end criteria. Marks the game as finished, if at least criterion one
     * detected.
     *
     * @param game as the xox game object to be analysed and potentially flagged as finished.
     * @return boolean to indicate whether the game is already finished.
     */
    @Override
    public boolean analyzeAndUpdate(Game game) throws LogicException {

        if(game.getClass() != XoxGame.class)
            throw new LogicException("Xox Ending Analyzer can only work on instances of Xox games.");
        XoxGame xoxGame = (XoxGame) game;

        // check if board is full
        if (xoxGame.getBoard().isFull()) {
            xoxGame.markAsFinished();
            return true;
        }

        // check if one player has 3 in a line
        if (xoxGame.getBoard().isThreeInALine()) {
            xoxGame.markAsFinished();
            return true;
        }

        // No end criteria matched. Game is still running.
        return false;
    }
}
