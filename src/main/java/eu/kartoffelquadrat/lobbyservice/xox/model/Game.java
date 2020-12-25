package eu.kartoffelquadrat.lobbyservice.xox.model;

import eu.kartoffelquadrat.lobbyservice.xox.controller.beans.PlayerInfo;

/**
 * Represents the state of a running game.
 */
public class Game {

    // Read only access to the parameters of the two involved players.
    private final PlayerInfoReadOnly playerOne;
    private final PlayerInfoReadOnly playerTwo;

    // Reference to current state of the board
    Board board;

    public Game(PlayerInfo startPlayer, PlayerInfo secondPlayer) {
        this.playerOne = null;
        this.playerTwo = null;
        //Todo: Implement.
    }
}
