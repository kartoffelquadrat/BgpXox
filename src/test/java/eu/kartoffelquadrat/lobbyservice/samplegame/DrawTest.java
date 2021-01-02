package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxActionInterpreter;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxClaimFieldAction;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxEndingAnalyzer;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

/**
 * This tests simulates a XOX game that ends in a draw. The playing is simulated using generated actions.
 *
 *         // ToDo: Add a test where a non-included action is passed.
 *
 * @author Maximilian Schiedermeier
 */
public class DrawTest extends XoxTestUtils {

    @Test
    public void testDraw() throws ModelAccessException, LogicException {

        // Prepare the game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        XoxGame game = addDummyGame(xoxGameGameManager, 12345);

        PlayerReadOnly x = game.getPlayerByName("X");
        PlayerReadOnly o = game.getPlayerByName("O");

        // Draw pattern, X begins
        //  X O X   1 8 9
        //  X X O   6 5 7
        //  O X O   4 3 2
        XoxActionGenerator actionGenerator = new XoxActionGenerator();


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
        XoxClaimFieldAction action2 = findActionForPosition(yActions, 2, 2);
        actionInterpreter.interpretAndApplyAction(action2, game);

        // 3)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 7);
        XoxClaimFieldAction action3 = findActionForPosition(xActions, 1, 2);
        actionInterpreter.interpretAndApplyAction(action3, game);

        // 4)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 6);
        XoxClaimFieldAction action4 = findActionForPosition(yActions, 0, 2);
        actionInterpreter.interpretAndApplyAction(action4, game);

        // 5)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 5);
        XoxClaimFieldAction action5 = findActionForPosition(xActions, 1, 1);
        actionInterpreter.interpretAndApplyAction(action5, game);

        // 6)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 4);
        XoxClaimFieldAction action6 = findActionForPosition(yActions, 0, 1);
        actionInterpreter.interpretAndApplyAction(action6, game);

        // 7)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 3);
        XoxClaimFieldAction action7 = findActionForPosition(xActions, 2, 1);
        actionInterpreter.interpretAndApplyAction(action7, game);

        // 8)
        yActions = actionGenerator.generateActions(game, o);
        assert (yActions.length == 2);
        XoxClaimFieldAction action8 = findActionForPosition(yActions, 1, 0);
        actionInterpreter.interpretAndApplyAction(action8, game);

        // At this point the game should not be flagged finished by an end analyzer
        assert(!game.isFinished());
        XoxEndingAnalyzer.analyzeAndUpdate(game);
        assert(!game.isFinished());

        // 9)
        xActions = actionGenerator.generateActions(game, x);
        assert (xActions.length == 1);
        XoxClaimFieldAction action9 = findActionForPosition(xActions, 2, 0);
        actionInterpreter.interpretAndApplyAction(action9, game);

        // At this point the game should be a draw.
        XoxEndingAnalyzer.analyzeAndUpdate(game);
        assert(game.isFinished());

        // Verify there is no winner
        // ToDo: Implement a mechanism to determine and communicate the winner.

    }

    /**
     * Helper method to extract the desired action of an actions bundle. The action is identified by gird position.
     */
    private XoxClaimFieldAction findActionForPosition(XoxClaimFieldAction[] actions, int xPos, int yPos) {

        for(XoxClaimFieldAction action : actions)
        {
            if(action.getX() == xPos && action.getY() == yPos)
                return action;
        }
        throw new RuntimeException("Requested action is not contained in provided action bundle.");
    }
}