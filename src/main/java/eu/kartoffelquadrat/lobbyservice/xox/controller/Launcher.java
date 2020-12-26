/**
 * Launch this program with: "mvn spring-boot:run"
 * <p>
 * Access to the UI is granted by the LobbyService WebUI through redirect.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
package eu.kartoffelquadrat.lobbyservice.xox.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kartoffelquadrat.lobbyservice.xox.controller.communcationbeans.GameServerParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

/**
 * This class powers up Spring and ensures the annotated controllers are detected.
 */
@SpringBootApplication
public class Launcher {

    public static final String GAME_SERVICE_NAME = "Xox";

    private static final String AUTH_NAME = "xox";
    private static final String AUTH_PASSWORD = "laaPhie*aiN0";

    // ToDo: Read credentials from properties / docker-param
    // ToDo: Pass LS location as parameter.
    // ToDo: Configure own location as parameter.
    private static final String LS_LOCATION = "http://127.0.0.1:4242";
    private static final GameServerParameters REGISTRATION_PARAMETERS = new GameServerParameters(GAME_SERVICE_NAME, "http://127.0.0.1:4244/" + GAME_SERVICE_NAME, 2, 2, "true");

    public static void main(String[] args) {

        // Power up Xox API backend
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Launcher.class, args);

        // Register Xox at LS-GameRegistry
        try {
            registerAtLobbyService();
        } catch (UnirestException ue) {
            throw new RuntimeException("LobbyService not reachable at provided location: " + LS_LOCATION);
        }

        // Keep alive until "Return-Key" pressed
        System.out.println("Xox running and registered at the LobbyService. Hit enter to unregister and shutdown.");
        new Scanner(System.in).nextLine();

        // Unregister Xox at LS-GameRegistry
        System.out.println("Unregistering...");
        try {
            unregisterAtLobbyService();
        } catch (UnirestException ue) {
            throw new RuntimeException("LobbyService not reachable at provided location: " + LS_LOCATION);
        }

        // Shut down Xox backend API
        System.out.println("Shutting down API...");
        applicationContext.close();
        System.out.println("[Xox terminated]");
    }

    /**
     * Retrieves an OAuth2 token that allows registration / unregistration of Xox as a new GameService at the LS.
     * Credentials of an LS admin account are required.
     *
     * @return a string encoded OAuth token. Special characters are not yet URL enoded.
     * @throws UnirestException in case of a communication error with the LS
     */
    private static String getToken() throws UnirestException {

        String bodyString = "grant_type=password&username=" + AUTH_NAME + "&password=" + AUTH_PASSWORD;
        HttpResponse<String> response = Unirest
                .post(LS_LOCATION + "/oauth/token")
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
    private static void registerAtLobbyService() throws UnirestException {

        // Get a valid access token, to authenticate for the registration.
        String accessToken = getToken();

        // Build and send an authenticated registration request to the LS API.
        String bodyJson = new Gson().toJson(REGISTRATION_PARAMETERS);
        HttpResponse<String> response = Unirest
                .put(LS_LOCATION + "/api/gameservices/Xox")
                .header("Authorization", "Bearer "+ accessToken)
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
    private static void unregisterAtLobbyService() throws UnirestException {

        // Get a valid access token, to authenticate for the un-registration.
        String accessToken = getToken();

        // Build and send an authenticated un-registration request to the LS API.
        HttpResponse<String> response = Unirest
                .delete(LS_LOCATION + "/api/gameservices/Xox")
                .header("Authorization", "Bearer "+ accessToken)
                .asString();

        // Verify the registration was accepted
        if (response.getStatus() != 200)
            System.out.println("LobbyService rejected unregistration of Xox. Server replied:\n" + response.getStatus() + " - " + response.getBody());
    }
}

