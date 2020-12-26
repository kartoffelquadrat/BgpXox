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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * This class powers up Spring and ensures the annotated controllers are detected.
 */
@SpringBootApplication
public class Launcher {

    public static final String GAME_SERVICE_NAME = "Xox";

    private static final String AUTH_NAME = "xox";
    private static final String AUTH_PASSWORD = "laaPhie*aiN0";

    // ToDo:L read credentials from properties / docker-param
    // ToDo: Pass LS location as parameter.
    // ToDo: Configure own location as parameter.
    private static final String LS_LOCATION = "http://127.0.0.1:4242"; // Unirest.config().defaultBaseUrl("http://homestar.com")
    private static final GameServerParameters REGISTRATION_PARAMETERS = new GameServerParameters("Xox", "http://127.0.0.1:4244/Xox", 2, 2, "true");

    public static void main(String[] args) {

        SpringApplication.run(Launcher.class, args);


        // For testing: Re-register on every return press.
        while (true) {
            System.out.println("------------------------------\nHit enter to register.");
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            System.out.println(line);
            System.out.println("Re-registering Xox at LS.");

            try {
                System.out.println("Trying to register...");
                registerAtLobbyService();
            } catch (UnirestException unirestException) {
                System.out.println("Registration failed. Cause: " + unirestException.getMessage());
            }
        }
    }

    /**
     * Retrieves an OAuth2 token that allows registration of Xox as a new GameService at the LS. Credentials of an LS
     * admin account are required.
     *
     * @return
     * @throws UnirestException
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
            throw new RuntimeException("LS rejected Xox credentials");
        JsonObject responseJson = new JsonParser().parse(response.getBody()).getAsJsonObject();
        String token = responseJson.get("access_token").toString().replaceAll("\"", "");
        token = token.replaceAll("\\+", "%2B");
//        token = token.replaceAll("\\=", "%3D");
        System.out.println("Retrieved access-token: "+token);
        return token;
    }

    private static void registerAtLobbyService() throws UnirestException {
        String accessToken= getToken();
        String bodyJson = new Gson().toJson(REGISTRATION_PARAMETERS);
        System.out.println(bodyJson);
        System.out.println(accessToken);
        HttpResponse<String> response = Unirest
              //.put("http://127.0.0.1:4242/api/gameservices/Xox")
                  .put(LS_LOCATION + "/api/gameservices/Xox")
                .queryString("access_token", accessToken)
//                .header("Authorization:Bearer", accessToken)
                .header("Content-Type", "application/json")
                .body(bodyJson)
                .asString();
        System.out.println(response.getBody());
        System.out.println("Server replied with: " + response.getStatus()+ " - "+response.getStatusText());


//        RestTemplate rest = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("{\n");
//        sb.append("    \"location\": \"http://127.0.0.1:4243/Xox\",\n");
//        sb.append("    \"maxSessionPlayers\": \"5\",\n");
//        sb.append("    \"minSessionPlayers\": \"3\",\n");
//        sb.append("    \"name\": \"Xox\",\n");
//        sb.append("    \"webSupport\": \"true\"\n");
//        sb.append("}");
//        String body = sb.toString();
//
//        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
//        ResponseEntity<String> responseEntity = rest.exchange("http://127.0.0.1:4242/api/gameservices/Xox?access_token=t7PlQHzSOfZzEwSasujfb%2BXjZFo=", HttpMethod.PUT, requestEntity, String.class);
//        HttpStatus httpStatus = responseEntity.getStatusCode();
//        int status = httpStatus.value();
//        String response = responseEntity.getBody();
//        System.out.println("Response status: " + status);
//        System.out.println(response);
    }
}

