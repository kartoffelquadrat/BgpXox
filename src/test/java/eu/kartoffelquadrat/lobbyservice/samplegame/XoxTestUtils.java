package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

public class XoxTestUtils {

    public XoxGame addDummyGame(GameManager<XoxGame> gameGameManager, long gameId) throws ModelAccessException {

        // Add test game to gameManager
        long fakeId = 12345;
        Player[] players = new Player[2];
        players[0] = new Player("X", "#CAFFEE");
        players[1] = new Player("O", "#1C373A");
        gameGameManager.addGame(gameId, players);
        return gameGameManager.getGameById(gameId);

    }
}
