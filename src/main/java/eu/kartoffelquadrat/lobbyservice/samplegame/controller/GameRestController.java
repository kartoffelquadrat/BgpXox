package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.LauncherInfo;
import org.springframework.http.ResponseEntity;

/**
 * Generic interface for REST endpoint methods common to all board game implementations. All methods require the
 * specification of a specific game-entity, identified by the long-id provided by the lobby-service upon game creation.
 * Note that non-void return types are always Strings. This allows for a JSON / XML encoding of the result objects.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface GameRestController {

    /**
     * Creates a new game, identified by the provided unique long value.
     *
     * @param gameId       as the game key.
     * @param launcherInfo as additional parameters for the game to be created
     * @param accessToken  as the oauth2 token used to authorize this operation. Must be a user token.
     */
    ResponseEntity launchGame(long gameId, LauncherInfo launcherInfo, String accessToken);

    /**
     * Deletes a new game, identified by the provided unique long value, no matter if finished or still running.
     *
     * @param gameId      as the game key.
     * @param accessToken as the oauth2 token used to authorize this operation. Must be an admin token. ToDo: Verify
     *                    this!
     */
    ResponseEntity deleteGame(long gameId, String accessToken);

    /**
     * Getter for the game board. This end point should be refreshed regularly, to allow for asynchronous client
     * updates. The hash value can be set to the empty string to ignore long-polling.
     *
     * @param gameId as the game key.
     * @param hash   as the optional MD5 hash string of the most recent board state on client side.
     * @return
     */
    ResponseEntity getBoard(long gameId, String hash);

    /**
     * Getter for static player objects (names, preferred colours) of the participants of the game instance referenced
     * by the provided game-id.
     *
     * @param gameId as the game key.
     * @return
     */
    ResponseEntity getPlayers(long gameId);

    /**
     * Method to look up possible actions for a given player and session. This end point should be access protected, for
     * it potentially discloses information restricted to the player oin question. Note that there is no
     * generic/prepared method to handle user-selected actions. Following the REST approach, actions might directly
     * modify game/board-specific sub-resources which can not be assumed in a generic interface.
     *
     * @param gameId      as the key to resolve the referenced game-instance
     * @param player      as the player requesting a set of available actions
     * @param accessToken as the
     */
    ResponseEntity getActions(long gameId, String player, String accessToken);
}
