package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import com.google.gson.Gson;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.GameRestController;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.TokenResolver;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.LauncherInfo;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/***
 * Rest controller for API endpoints of the Xox game. Apart from rtequest authorization, no business logic is contained
 * in this class.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@RestController
public class XoxRestController implements GameRestController {

    // Injected util beans and own service name.
    private final TokenResolver tokenResolver;
    private final GameManager<XoxGame> gameManager;
    private final ActionGenerator actionGenerator;
    private final String gameServiceName;

    public XoxRestController(
            @Autowired ActionGenerator actionGenerator, GameManager<XoxGame> gameManager, TokenResolver tokenResolver,
            @Value("${gameservice.name}") String gameServiceName) {
        this.actionGenerator = actionGenerator;
        this.gameManager = gameManager;
        this.gameServiceName = gameServiceName;
        this.tokenResolver = tokenResolver;
    }

    /**
     * Debug endpoint. Can be accessed e.g. at: http://127.0.0.1:4244/Xox/online
     */
    @GetMapping("/online")
    public String getOnlineFlag() {
        return "Xox is happily running.";
    }

    @Override
    @PutMapping(value = "/api/games/{gameId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchGame(@PathVariable long gameId, LauncherInfo launcherInfo, String accessToken) {

        try {
            if (launcherInfo == null || launcherInfo.getGameServer() == null)
                throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
            if (!launcherInfo.getGameServer().equals(gameServiceName))
                throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
            if (gameManager.isExistentGameId(gameId))
                throw new LogicException("Game can not be launched. Id is already in use.");

            // Looks good, lets create the game on model side, return an Http-OK.
            gameManager.addGame(gameId, launcherInfo.getPlayers().toArray(new Player[launcherInfo.getPlayers().size()]));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (LogicException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/api/games/{gameId}")
    public ResponseEntity deleteGame(@PathVariable long gameId, String accessToken) {

        try {
            // Verify the provided game id is valid
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Game can not be removed. No game associated to provided gameId");

            // ToDo: Verify how this changes if a creator token is used. Inspect LS sources if termination of running games is allowed for creators.

            // Looks good, remove the game on model side, return an Http-OK.
            gameManager.removeGame(gameId, true);
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity getBoard(@PathVariable long gameId, @RequestParam(required = false) String hash) {

        // ToDo: Enable long polling for this resource. ToDo: close BCM (dedicated ARL method) on game finish.
        try {
            // Verify the requested game exists.
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve board for game " + gameId + ". Not a valid game id.");

            // Looks good, Serialize the board and place it as body in a ResponseEntity (Http-OK).
            String serializedBoard = new Gson().toJson(gameManager.getGameById(gameId));
            return ResponseEntity.status(HttpStatus.OK).body(serializedBoard);

        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity getPlayers(@PathVariable long gameId) {

        try {
            // Verify the requested game exists.
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve players for game " + gameId + ". Not a valid game id.");

            // Looks good, Serialize the board and place it as body in a ResponseEntity (Http-OK).
            String serializedPlayers = new Gson().toJson(gameManager.getGameById(gameId).getPlayers());
            return ResponseEntity.status(HttpStatus.OK).body(serializedPlayers);
        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity getActions(@PathVariable long gameId, String playerName, String accessToken) {

        try {

            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve players for game " + gameId + ". Not a valid game id.");

            // ToDo: verify token belongs to player

            // ToDo: verify player is participant

            // ToDo: verify if its the players turn

            // ToDo: resolve player to player object... or convert down call stack below...

            // Looks good, build the actions array, serialize it and send it back in a 200 (OK) Http response.
            XoxGame xoxGame = gameManager.getGameById(gameId);
            PlayerReadOnly playerObject = xoxGame.getPlayerByName(playerName);
            String serializedActions = new Gson().toJson(actionGenerator.generateActions(xoxGame, playerObject));
            return ResponseEntity.status(HttpStatus.OK).body(serializedActions);

        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
