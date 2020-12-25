package eu.kartoffelquadrat.lobbyservice.xox.model;

/**
 * Read only interface for properties of a player, as provided by the LobbyService.
 */

public interface PlayerInfoReadOnly {

    /**
     * Getter for the name of a player.
     *
     * @return
     */
    String getName();

    /**
     * Getter for the preferred colour of a player.
     *
     * @return
     */
    String getPreferredColour();
}
