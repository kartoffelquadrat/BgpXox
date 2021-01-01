package eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-RAM implementation of the principal game manager, that is to say state is restrained to a single service entity
 * and not backend by a dedicated database. Indexes games by ID (provided by the LobbyService) This class is
 * instantiated and maintained as Singleton bean by the Spring Bean manager (which allows autowiring in the controller,
 * where needed.)
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class XoxLocalGameManager implements GameManager {

    // organizes games by session-ID (provided by lobby-service)
    private final Map<Long, XoxGame> games = new LinkedHashMap<>();

    @Override
    public XoxGame getGameById(long gameId) throws ModelAccessException{

        if (!isExistentGameId(gameId))
            throw new ModelAccessException("GameId can not be resolved to a registered game.");
        return games.get(gameId);
    }

    @Override
    public boolean isExistentGameId(long gameId) {
        return games.containsKey(gameId);
    }

    @Override
    public XoxGame addGame(long gameId, Player[] players) throws ModelAccessException {

        // Refuse creation if gameid collides with existing game.
        if (games.containsKey(gameId))
            throw new ModelAccessException("Game can not be created, the requested ID " + gameId + "is already in use.");

        // Create a new game and add it, then return the game entity.
        if (players.length != 2)
            throw new ModelAccessException("Game can not be created, exactly two players are required to play Xox.");
        games.put(gameId, new XoxGame(players[0], players[1]));
        return getGameById(gameId);
    }

    @Override
    public void removeGame(long gameId, boolean evenIfUnfinished) throws ModelAccessException  {

        // Throw exception of the gameid is not valid
        if (!games.containsKey(gameId))
            throw new ModelAccessException("Game can not be removed. The provided id " + gameId + " is not in use.");

        // Throw expection of the game is still running and no override flag was provided
        if (!games.get(gameId).isFinished() && !evenIfUnfinished)
            throw new ModelAccessException("Game can be removed. It is still running.");

        // Looks ok, remove the game form the map.
        games.remove(gameId);
    }
}
