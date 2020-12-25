package eu.kartoffelquadrat.lobbyservice.xox.model;

import eu.kartoffelquadrat.lobbyservice.xox.controller.beans.PlayerInfo;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-RAM implementation of the principal game manager, that is to say state is restrained to a single service entity
 * and not backend by a dedicated database. Indexes games by ID (provided by the LobbyService) This class is
 * instantiated and maintained as Singleton bean by the Spring Bean manager (which allows autowiring in the controller,
 * where needed.)
 */
@Component
public class LocalGameManager implements GameManager {

    // organizes games by session-ID (provided by lobby-service)
    private final Map<Long, Game> games = new LinkedHashMap<>();

    @Override
    public Game getGameById(long gameId) {
        return games.get(gameId);
    }

    @Override
    public Game addGame(long gameId, PlayerInfo startPlayer, PlayerInfo secondPlayer) {
        if (games.containsKey(gameId))
            throw new IllegalModelAccessException("Game can not be created, the requested ID " + gameId + "is already in use.");
        games.put(gameId, new Game(startPlayer, secondPlayer));

        return null;
        // Todo: implement
    }

    @Override
    public void removeGame(long GameId, boolean evenIfUnfinished) {
        // todo: implement
    }
}
