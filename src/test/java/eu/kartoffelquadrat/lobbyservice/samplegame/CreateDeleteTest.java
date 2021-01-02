package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxLocalGameManager;
import org.junit.Test;

/**
 * @author Maximilian Schiedermeier
 */
public class CreateDeleteTest {

    @Test
    public void testGameCreateDelete() {

        // Create a gameManager, add an empty game
        GameManager<XoxGame> xoxGameGameManager = new XoxLocalGameManager();

        // Add test game to gameManager
        long fakeId = 12345;
        Player[] players = new Player[2];
        players[0] = new Player("playerOne", "#CAFFEE");
        players[0] = new Player("playerTwo", "#1C373A");

        try {
            xoxGameGameManager.addGame(fakeId, players);
            xoxGameGameManager.removeGame(fakeId, true);
        } catch (ModelAccessException e) {
            throw new RuntimeException("Test failed.");
        }
    }
}