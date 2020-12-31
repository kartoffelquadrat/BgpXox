package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.GameServerParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util class that handles the registration / un-registration of a game-service based on the parameters provided in
 * application.properties. Configurable annotation allows injection of primitives form application.properties as class
 * fields at runtime.
 *
 * @Author Maximilian Schiedermeier, 2021.
 */
@Component
public class Registrator {

    private String lobbyServiceLocation;
    private String gameServiceName;

    @Value("${oauth2.name}")
    private String serviceOauthName;

    @Value("${oauth2.password}")
    private String serviceOauthPassword;

    private GameServerParameters registrationParameters;

    @Autowired
    Registrator(@Value("${gameservice.name}")
                        String gameServiceName, @Value("${lobbyservice.location}")
                        String lobbyServiceLocation) {
        this.gameServiceName = gameServiceName;
        this.lobbyServiceLocation = lobbyServiceLocation;
        registrationParameters = new GameServerParameters(gameServiceName, lobbyServiceLocation + gameServiceName, 2, 2, "true");
        System.out.println("LS-location: " + lobbyServiceLocation);
    }

    /**
     * Retrieves an OAuth2 token that allows registration / unregistration of Xox as a new GameService at the LS.
     * Credentials of an LS admin account are required.
     *
     * @return a string encoded OAuth token. Special characters are not yet URL enoded.
     * @throws UnirestException in case of a communication error with the LS
     */
    private String getToken() throws UnirestException {

        String bodyString = "grant_type=password&username=" + serviceOauthName + "&password=" + serviceOauthPassword;
        HttpResponse<String> response = Unirest
                .post(lobbyServiceLocation + "/oauth/token")
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

        // Get a valid access token, to authenticate for the registration.
        String accessToken = getToken();

        // Build and send an authenticated registration request to the LS API.
        String bodyJson = new Gson().toJson(registrationParameters);
        HttpResponse<String> response = Unirest
                .put(lobbyServiceLocation + "/api/gameservices/Xox")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(bodyJson)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            System.out.println("LobbyService rejected registration of Xox. Server replied:\n" + response.getStatus() + " - " + response.getBody());
    }

    /**
     * Revoke a previous registration at the LS game registry. This is typically performed prior to shutdown. Admin
     * credentials are required to authorize this operation.
     */
    public void unregisterAtLobbyService() throws UnirestException {

        // Get a valid access token, to authenticate for the un-registration.
        String accessToken = getToken();

        // Build and send an authenticated un-registration request to the LS API.
        HttpResponse<String> response = Unirest
                .delete(lobbyServiceLocation + "/api/gameservices/Xox")
                .header("Authorization", "Bearer " + accessToken)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            System.out.println("LobbyService rejected unregistration of Xox. Server replied:\n" + response.getStatus() + " - " + response.getBody());
    }
}
