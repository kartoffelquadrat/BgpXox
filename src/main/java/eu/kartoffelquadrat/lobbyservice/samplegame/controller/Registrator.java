package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.GameServerParameters;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.GameServiceRegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util class that handles the registration / un-registration of a game-service based on the parameters provided in
 * application.properties. Configurable annotation allows injection of primitives form application.properties as class
 * fields at runtime.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class Registrator {

    @Autowired
    private GameServiceRegistrationDetails lobbyServiceLocation;

    private String gameServiceLocation;

    private String gameServicePort;

    @Value("${oauth2.name}")
    private String serviceOauthName;

    @Value("${oauth2.password}")
    private String serviceOauthPassword;

    private GameServerParameters registrationParameters;

    @Value("${debug.skip.registration}")
    private boolean skipLobbyServiceCallbacks;

    @Autowired
    Registrator(@Value("${gameservice.name}")
                        String gameServiceName,
                @Value("${gameservice.location}")
                        String gameServiceLocation,
                @Value("${server.port}")
                        String gameServicePort) {
        this.gameServiceLocation = gameServiceLocation;
        this.gameServicePort = gameServicePort;
        registrationParameters = new GameServerParameters(gameServiceName, gameServiceLocation + ":" + gameServicePort + "/" + gameServiceName, 2, 2, "true");
    }

    /**
     * Retrieves an OAuth2 token that allows registration / unregistration of Xox as a new GameService at the LS.
     * Credentials of an LS admin account are required.
     *
     * @return a string encoded OAuth token. Special characters are not yet URL enoded.
     * @throws UnirestException in case of a communication error with the LS
     */
    private String getToken() throws UnirestException {

        if (skipLobbyServiceCallbacks) {
            System.out.println("***WARNING*** Token retrieval skipped.");
            return "DUMMY";
        }

        String bodyString = "grant_type=password&username=" + serviceOauthName + "&password=" + serviceOauthPassword;
        HttpResponse<String> response = Unirest
                .post(lobbyServiceLocation.getAssociatedLobbyLocation() + "/oauth/token")
                // Authorization parameter is the base64 encoded string: "bgp-client-name:bgp-client-pw".
                // Can remain unchanged for future games.
                .header("Authorization", "Basic YmdwLWNsaWVudC1uYW1lOmJncC1jbGllbnQtcHc=")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(bodyString)
                .asString();
        if (response.getStatus() != 200)
            throw new RuntimeException("LS rejected Xox credentials. Make sure the \"xox\" user exists!");

        // Extract token of response JSON, escape potential special characters
        JsonObject responseJson = new JsonParser().parse(response.getBody()).getAsJsonObject();
        String token = responseJson.get("access_token").toString().replaceAll("\"", "");
        return token;
    }

    /**
     * Registers Xox as a Game-service at the LS game registry. Admin credentials are required to authorize this
     * operation.
     *
     * @throws UnirestException in case the communication with the LobbyService failed or the LobbyService rejected a
     *                          registration of Xox.
     */
    public void registerAtLobbyService() throws UnirestException {

        if (skipLobbyServiceCallbacks) {
            System.out.println("***WARNING*** Registration skipped.");
            return;
        }

        // Get a valid access token, to authenticate for the registration.
        String accessToken = getToken();

        // Build and send an authenticated registration request to the LS API.
        String bodyJson = new Gson().toJson(registrationParameters);
        HttpResponse<String> response = Unirest
                .put(lobbyServiceLocation.getAssociatedLobbyLocation() + "/api/gameservices/Xox")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(bodyJson)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            throw new RuntimeException("LobbyService rejected registration of Xox. Server replied:\n" + response.getStatus() + " - " + response.getBody());

        throw new RuntimeException("Everything ok: "+response);
    }

    /**
     * Revoke a previous registration at the LS game registry. This is typically performed prior to shutdown. Admin
     * credentials are required to authorize this operation.
     */
    public void unregisterAtLobbyService() throws UnirestException {

        if (skipLobbyServiceCallbacks) {
            System.out.println("***WARNING*** Unregistration skipped.");
            return;
        }

        // Get a valid access token, to authenticate for the un-registration.
        String accessToken = getToken();

        // Build and send an authenticated un-registration request to the LS API.
        HttpResponse<String> response = Unirest
                .delete(lobbyServiceLocation.getAssociatedLobbyLocation() + "/api/gameservices/Xox")
                .header("Authorization", "Bearer " + accessToken)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            System.out.println("LobbyService rejected unregistration of Xox. Server replied:\n" + response.getStatus() + " - " + response.getBody());
    }

    // ToDo: Add method to notify LS about an ended game.
    public void notifyGameOver(long gameId) throws UnirestException {
        if (skipLobbyServiceCallbacks) {
            System.out.println("***WARNING*** Notify game-over skipped.");
            return;
        }

        // Get a valid access token, to authenticate for the un-registration.
        String accessToken = getToken();

        // Build and send an authenticated game-over request to the LS API.
        HttpResponse<String> response = Unirest
                .delete(lobbyServiceLocation.getAssociatedLobbyLocation() + "/api/sessions/"+gameId)
                .header("Authorization", "Bearer " + accessToken)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            System.out.println("LobbyService rejected Game-Over notification of Xox session. Server replied:\n" + response.getStatus() + " - " + response.getBody());

    }
}
