package eu.kartoffelquadrat.lobbyservice.xox.model;

import eu.kartoffelquadrat.lobbyservice.xox.controller.communcationbeans.PlayerInfo;
import jdk.nashorn.internal.parser.TokenStream;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents the state of a running game.
 */
public class XoxGame {

    // Read only access to the parameters of the two involved players.
    private final PlayerInfoReadOnly[] players = new PlayerInfoReadOnly[2];

    // Reference to current state of the board
    XoxBoard board;

    // Internal flag to indicate whether the game has already ended or still running.
    private boolean finished;

    // Internal index counter for the current player. Range: [0-1]
    private int currentPlayer;

    public XoxGame(PlayerInfo startPlayer, PlayerInfo secondPlayer) {
        players[0] = startPlayer;
        players[1] = secondPlayer;
        currentPlayer = 0;
        //Todo: Implement.
    }

    public boolean isFinished() {
        return finished;
    }

    public void markAsFinished() {
        finished = true;
    }

    public XoxBoard getBoard() {
        return board;
    }

    public PlayerInfoReadOnly getPlayerInfo(int index) {

        return players[index];
    }

    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public String getCurrentPlayerName() {
        return players[currentPlayer].getName();
    }

    public void setCurrentPlayer(int nextCurrentPlayer) {
        if (nextCurrentPlayer != 0 && nextCurrentPlayer != 1)
            throw new ModelAccessException("Current player can not be set to a value other than 0 or 1.");
        currentPlayer = nextCurrentPlayer;
    }
}
