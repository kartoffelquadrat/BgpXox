package eu.kartoffelquadrat.lobbyservice.samplegame;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic.XoxClaimFieldAction;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

import java.util.Map;

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

    /**
     * Helper method to extract the desired action of an actions bundle. The action is identified by gird position.
     */
    public XoxClaimFieldAction findActionForPosition(Map<String, Action> actions, int xPos, int yPos) {

        for(Action action : actions.values())
        {
            if(action.getClass() != XoxClaimFieldAction.class)
                throw new RuntimeException("Action map can not be interpreted - not Xox actions");
            XoxClaimFieldAction xoxAction = (XoxClaimFieldAction) action;

            if(xoxAction.getX() == xPos && xoxAction.getY() == yPos)
                return xoxAction;
        }
        throw new RuntimeException("Requested action is not contained in provided action bundle.");
    }
}
