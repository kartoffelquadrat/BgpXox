package eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans;

import org.springframework.beans.factory.annotation.Value;

/**
 * Just a bean to encapsulate all data transferred to the LS upon registration of Xox as a new gameserver. This class
 * can be copied and reused as is in other game-implementations. Only the constructor parameters need to be adjusted.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public class GameServerParameters {

    // Name of the gameServer to be registered. Will we converted to lowercase internally.
    private String name;

    // Location of the server's base URL. If WebSupport is set to true, this URL must return the webclient of the
    // registered game. In any case the registered game must provide subresource, "games/{gameId}" with "PUT", for the
    // LS to launch the session on game-server side.
    private String location;

    // Upper and lower bounds for players allowed in sessions of this gameserver.
    private int maxSessionPlayers;
    private int minSessionPlayers;

    /* Indicates whether a generic BGP web clients can page forward to the server location on session launch (gameid as
     session parameter). Encodes a boolean, but must be a string, for non-existence is auto-interpreted as "false" for
     boolean values. If set, convention is that the generic JS client forwards on session launch to the provided server
     location, passing required params such as username, access_token, gameid as URL parameters.*/
    private String webSupport;

    // default CTR required for JSON deserialization.
    public GameServerParameters() {

    }

    /**
     * @param name
     * @param location
     * @param minSessionPlayers
     * @param maxSessionPlayers
     */
    public GameServerParameters(String name, String location, int minSessionPlayers, int maxSessionPlayers, String webSupport) {
        this.name = name;
        this.location = location;
        this.minSessionPlayers = minSessionPlayers;
        this.maxSessionPlayers = maxSessionPlayers;
        this.webSupport = webSupport;
    }


    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getMaxSessionPlayers() {
        return maxSessionPlayers;
    }

    public int getMinSessionPlayers() {
        return minSessionPlayers;
    }

    public boolean isWebSupport() {
        return Boolean.parseBoolean(webSupport);
    }

    /**
     * Helper method to determine wether a registered gameserver runs in phantom (p2p) mode. This method simply checks
     * whether no location was provided.
     *
     * @return
     */
    public boolean isPhantom() {
        return location.isEmpty();
    }
}
