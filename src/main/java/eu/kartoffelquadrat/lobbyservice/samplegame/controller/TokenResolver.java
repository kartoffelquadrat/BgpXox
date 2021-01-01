package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.GameServerParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util class to resolve player tokens to names and roles. An instance of this calss can be injected anywhere user
 * authentication or role authentication is required (notably in access protected REST endpoints.)
 *
 * @Author Maximilian Schiedermeier, January 2021
 */
@Component
public class TokenResolver {

    private static final String TOKEN_ROLE_RESOURCE = "/oauth/role";
    private static final String TOKEN_NAME_RESOURCE = "/oauth/username";

    private String lobbyServiceLocation;

    private GameServerParameters registrationParameters;

    @Autowired
    TokenResolver(@Value("${lobbyservice.location}")
                          String lobbyServiceLocation) {
        this.lobbyServiceLocation = lobbyServiceLocation;
    }

    /**
     * Resolves an OAuth2 token to the associated user.
     *
     * @param accessToken as the string encoded OAuth2 token.
     * @return the name of the associated user.
     * @throws LogicException in case the lobby service replied with a non 200 (token not resolvable)
     * @throws UnirestException in case the lobby service is not reachable
     */
    public String resolveToPlayerName(String accessToken) throws LogicException, UnirestException {
        HttpResponse<String> response = Unirest
                .get(lobbyServiceLocation + TOKEN_NAME_RESOURCE)
                .header("Authorization", "Bearer " + accessToken)
                .asString();
        if (response.getStatus() != 200)
            throw new RuntimeException("Unable to resolve provided token to a username!");
        return response.getBody();
    }

    /**
     * Resolves an OAuth2 token to the associated role (admin / player).
     *
     * @param accessToken as the string encoded OAuth2 token.
     * @return the role of the associated user: "ROLE_ADMIN" / "ROLE_PLAYER"
     * @throws LogicException in case the lobby service replied with a non 200 (token not resolvable)
     * @throws UnirestException in case the lobby service is not reachable
     */
    public String resolveToRole(String accessToken) throws LogicException, UnirestException {
        HttpResponse<String> response = Unirest
                .get(lobbyServiceLocation + TOKEN_ROLE_RESOURCE)
                .header("Authorization", "Bearer " + accessToken)
                .asString();
        if (response.getStatus() != 200)
            throw new RuntimeException("Unable to resolve provided token to a role!");
        return response.getBody();
    }
}
