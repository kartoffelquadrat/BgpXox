package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

/**
 * This tests simulates a XOX game that ends in a draw. The playing is simulated using generated actions.
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
        //  X O X
        //  X X O
        //  O X O
        XoxActionGenerator actionGenerator = new XoxActionGenerator();


        // X retrieves actions, decides for action on top left.
        Action[] xActions = actionGenerator.generateActions(game, x);
        assert(xActions.length == 9);
    }
}