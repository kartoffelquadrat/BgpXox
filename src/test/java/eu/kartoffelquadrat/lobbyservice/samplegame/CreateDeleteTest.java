package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

/**
 * @author Maximilian Schiedermeier
 */
public class CreateDeleteTest extends XoxTestUtils {

    @Test(expected = ModelAccessException.class)
    public void testNoDuplicateGames() throws ModelAccessException {

        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        try {
            addDummyGame(xoxGameGameManager, 12345);
        } catch (ModelAccessException e) {
            throw new RuntimeException("Test game creation failed");
        }

        // using the same game twice must result in a model exception
        addDummyGame(xoxGameGameManager, 12345);
    }

    @Test
    public void testGameCreateDelete() {

        // Create a gameManager, add an empty game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        try {
            addDummyGame(xoxGameGameManager, 12345);
            xoxGameGameManager.removeGame(12345, true);
        } catch (ModelAccessException e) {
            throw new RuntimeException("Test game creation failed");
        }
    }

    @Test(expected = ModelAccessException.class)
    public void testRemoveInexistent() throws ModelAccessException {

        // Create a gameManager, add an empty game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();
        xoxGameGameManager.removeGame(42l, true);
    }
}