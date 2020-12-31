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
    PlayerReadOnly getPlayerByName(String name);

    /**
     * Retrieves the board of a generic game.
     *
     * @return
     */
    Board getBoard();
}
