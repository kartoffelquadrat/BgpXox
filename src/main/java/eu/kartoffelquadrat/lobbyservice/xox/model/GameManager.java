package eu.kartoffelquadrat.lobbyservice.xox.model;

import eu.kartoffelquadrat.lobbyservice.xox.controller.beans.PlayerInfo;

/**
 * General game manager entity that serves as entry point for access to the model. Adherent classes can be injected into
 * the contoller. On the long run there will by multiple implementations, depending on whether an in-RAM model
 * management or Database mangament (which allows for multiple instances of the service to be deployed simultaneously)
 * is desired.
 */
public interface GameManager {

    /**
     * Retrieves a specific game, by Id.
     *
     * @param gameId
     * @return
     */
    Game getGameById(long gameId);

    /**
     * Adds a new blank game to the manager
     *
     * @param gameId       id provided by the lobby-service
     * @param startPlayer  parameters for the player who begins
     * @param secondPlayer parameters for the second player
     * @return
     */
    Game addGame(long gameId, PlayerInfo startPlayer, PlayerInfo secondPlayer);

    /**
     * Removes an indexed game. The action is rejected adn a IllegalModelAccessException is thrown, if the game is not
     * yet finished, but the corresponding flag is set to false.
     *
     * @param GameId as the game to by removed
     * @param evenIfUnfinished as a safety flag to prevent unitended deletion of running games
     * @throws IllegalModelAccessException in case the game is still runnung by the previous parameter is set to false
     */
    void removeGame(long GameId, boolean evenIfUnfinished);
}