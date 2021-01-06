package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.EndingAnalyzer;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Ranking;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.*;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

import java.util.Map;

/**
 * @author Maximilian Schiedermeier
 */
public class WinnerTestVertical extends XoxTestUtils {

    @Test
    public void testWinX() throws ModelAccessException, LogicException {

        // Prepare the game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        XoxGame game = addDummyGame(xoxGameGameManager, 12345);

        PlayerReadOnly x = game.getPlayerByName("X");
        PlayerReadOnly o = game.getPlayerByName("O");

        // Draw pattern, X begins
        //  O X -   2 1 -
        //  O X -   4 3 -
        //  - X -   - 5 -
        XoxActionGenerator actionGenerator = new XoxActionGenerator();
        XoxEndingAnalyzer endingAnalyzer = new XoxEndingAnalyzer();

        // 1)
        // X retrieves actions, decides for action on top left.
        Map<String, Action> xActions = actionGenerator.generateActions(game, x);
        assert (xActions.size() == 9);
        XoxClaimFieldAction action1 = findActionForPosition(xActions, 1, 0);

        // Apply first action
        XoxActionInterpreter actionInterpreter = new XoxActionInterpreter(actionGenerator, endingAnalyzer);
        actionInterpreter.interpretAndApplyAction(action1, game);

        // 2)
        // Y retrieves actions, decides for action bottom right, apply second action
        Map<String, Action> yActions = actionGenerator.generateActions(game, o);
        assert (yActions.size() == 8);
        XoxClaimFieldAction action2 = findActionForPosition(yActions, 0, 0);
        actionInterpreter.interpretAndApplyAction(action2, game);

        // 3)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.size() == 7);
        XoxClaimFieldAction action3 = findActionForPosition(xActions, 1, 1);
        actionInterpreter.interpretAndApplyAction(action3, game);

        // 4)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.size() == 6);
        XoxClaimFieldAction action4 = findActionForPosition(yActions, 0, 1);
        actionInterpreter.interpretAndApplyAction(action4, game);

        // 5)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.size() == 5);
        XoxClaimFieldAction action5 = findActionForPosition(xActions, 1, 2);
        actionInterpreter.interpretAndApplyAction(action5, game);

        // At this point the game should be a won by X.
//        xoxEndingAnalyzer.analyzeAndUpdate(game);
        assert (game.isFinished());

        // Verify there is no winner
        XoxRankingGenerator rankingGenerator = new XoxRankingGenerator();
        Ranking ranking = rankingGenerator.computeRanking(game);
        assert (ranking.isGameOver() == true);

        // In case of a draw, both players should hold 0 points.
        assert (ranking.getScoresDescending()[0] == 1);
        assert (ranking.getScoresDescending()[1] == 0);

        // Verify player x is ranked at top position, followed by player o
        assert (ranking.getPlayersDescending()[0].getName().equals("X"));
        assert (ranking.getPlayersDescending()[1].getName().equals("O"));

        // Verify no more moves are possible
        // 5)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.size() == 0);
    }
}