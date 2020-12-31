package eu.kartoffelquadrat.lobbyservice.samplegame.model;

/**
 * Generic game interface with method common to all board game implementations.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface Game {

    /**
     * Retrieves the board of a generic game.
     * @return
     */
    public Board getBoard();
}
