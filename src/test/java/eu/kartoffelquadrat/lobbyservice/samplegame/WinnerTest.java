package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.EndingAnalyzer;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.*;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Ranking;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

/**
 * @author Maximilian Schiedermeier
 */
public class WinnerTest extends XoxTestUtils {

    @Test
    public void testWinX() throws ModelAccessException, LogicException {

        // Prepare the game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        XoxGame game = addDummyGame(xoxGameGameManager, 12345);

        PlayerReadOnly x = game.getPlayerByName("X");
        PlayerReadOnly o = game.getPlayerByName("O");

        // Draw pattern, X begins
        //  X O X   1 3 5
        //  X X O   2 4 -
        //  O X O   - - -
        XoxActionGenerator actionGenerator = new XoxActionGenerator();
        EndingAnalyzer xoxEndingAnalyzer = new XoxEndingAnalyzer();

        // 1)
        // X retrieves actions, decides for action on top left.
        XoxClaimFieldAction[] xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 9);
        XoxClaimFieldAction action1 = findActionForPosition(xActions, 0, 0);

        // Apply first action
        XoxActionInterpreter actionInterpreter = new XoxActionInterpreter(actionGenerator);
        actionInterpreter.interpretAndApplyAction(action1, game);

        // 2)
        // Y retrieves actions, decides for action bottom right, apply second action
        XoxClaimFieldAction[] yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 8);
        XoxClaimFieldAction action2 = findActionForPosition(yActions, 0, 1);
        actionInterpreter.interpretAndApplyAction(action2, game);

        // 3)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 7);
        XoxClaimFieldAction action3 = findActionForPosition(xActions, 1, 0);
        actionInterpreter.interpretAndApplyAction(action3, game);

        // 4)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 6);
        XoxClaimFieldAction action4 = findActionForPosition(yActions, 1, 1);
        actionInterpreter.interpretAndApplyAction(action4, game);

        // 5)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 5);
        XoxClaimFieldAction action5 = findActionForPosition(xActions, 2, 0);
        actionInterpreter.interpretAndApplyAction(action5, game);

        // At this point the game should be a won by X.
        xoxEndingAnalyzer.analyzeAndUpdate(game);
        assert (game.isFinished());

        // Verify there is no winner
        XoxRankingGenerator rankingGenerator = new XoxRankingGenerator();
        Ranking ranking = rankingGenerator.computeRanking(game);

        // In case of a draw, both players should hold 0 points.
        assert (ranking.getScoresDescending()[0] == 1);
        assert (ranking.getScoresDescending()[1] == 0);

        // Verify player x is ranked at top position, followed by player o
        assert (ranking.getPlayersDescending()[0].getName().equals("X"));
        assert (ranking.getPlayersDescending()[1].getName().equals("O"));

        // Verify no more moves are possible
        // 5)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 0);
    }
}