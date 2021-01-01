package eu.kartoffelquadrat.lobbyservice.samplegame.model;

/**
 * Generic game interface with method common to all board game implementations.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface Game {

    /**
     * Look up a player by name
     *
     * @param name as the name of the player to look up.
     * @return PlayerReadOnly as the matching player object, if associated. Throws a ModelAccessException if the
     * provided player is not associated to this game object.
     */
    PlayerReadOnly getPlayerByName(String name) throws ModelAccessException;

    /**
     * Retrieves the board of a generic game.
     *
     * @return the board of a game.
     */
    Board getBoard();

    /**
     * Retrieves the array (in order of registration) of players associated to a game object. Only returns a deep copy
     * of the original players array.
     *
     * @return a deep copy of the player array associated to a game.
     */
    PlayerReadOnly[] getPlayers();
}
