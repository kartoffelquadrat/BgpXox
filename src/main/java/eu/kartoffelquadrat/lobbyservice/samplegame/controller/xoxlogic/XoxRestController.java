package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kartoffelquadrat.asyncrestlib.BroadcastContentManager;
import eu.kartoffelquadrat.asyncrestlib.ResponseGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.*;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.LauncherInfo;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.GameServiceRegistrationDetails;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Ranking;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Board;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.LinkedHashMap;
import java.util.Map;


/***
 * Rest controller for all API endpoints of the Xox game.
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
    private final ActionInterpreter actionInterpreter;
    // This controller holds an additional BroadCastManager per game.
    // Calling touch on the manager allows an unblocking of clients that long poll the board resource.
    private final Map<Long, BroadcastContentManager<Board>> broadcastContentManagers;
    private final long longPollTimeout;
    @Autowired
    RankingGenerator rankingGenerator;
    @Autowired
    GameServiceRegistrationDetails lobbyServiceLocation;
    @Value("${debug.skip.registration}")
    private boolean skipTokenValidation;
    @Autowired
    private Registrator registrator;

    private final Logger logger;


    public XoxRestController(
            @Autowired ActionGenerator actionGenerator, GameManager<XoxGame> gameManager, TokenResolver tokenResolver, ActionInterpreter actionInterpreter,
            @Value("${gameservice.name}") String gameServiceName, @Value("${long.poll.timeout}") long longPollTimeout) {
        this.actionGenerator = actionGenerator;
        this.actionInterpreter = actionInterpreter;
        this.gameManager = gameManager;
        this.gameServiceName = gameServiceName;
        this.tokenResolver = tokenResolver;

        broadcastContentManagers = new LinkedHashMap<>();
        this.longPollTimeout = longPollTimeout;

        logger = LoggerFactory.getLogger(XoxRestController.class);
    }

    /**
     * Debug endpoint. Can be accessed e.g. at: http://127.0.0.1:4244/Xox/online
     */
    @GetMapping("/online")
    public String getOnlineFlag() {
        logger.info("Online echo contacted.");
        return "Xox is happily running.";
    }

    @Override
    @PutMapping(value = "/api/games/{gameId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchGame(@PathVariable long gameId, @RequestBody LauncherInfo launcherInfo) {

        try {
            if (launcherInfo == null || launcherInfo.getGameServer() == null)
                throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
            if (!launcherInfo.getGameServer().equals(gameServiceName))
                throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
            if (gameManager.isExistentGameId(gameId))
                throw new LogicException("Game can not be launched. Id is already in use.");

            // Looks good, lets create the game on model side, create a BCM for the board.
            gameManager.addGame(gameId, launcherInfo.getPlayers().toArray(new Player[launcherInfo.getPlayers().size()]));
            broadcastContentManagers.put(gameId, new BroadcastContentManager<>(gameManager.getGameById(gameId).getBoard()));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (LogicException | ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/api/games/{gameId}")
    public ResponseEntity deleteGame(@PathVariable long gameId) {

        try {
            // Verify the provided game id is valid
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Game can not be removed. No game associated to provided gameId");

            // Looks good, remove the game on model side, return an Http-OK.
            gameManager.removeGame(gameId, true);
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @GetMapping(value = "/api/games/{gameId}/board", produces = "application/json; charset=utf-8")
    public DeferredResult<ResponseEntity<String>> getBoard(@PathVariable long gameId, @RequestParam(required = false) String hash) {

        try {
            // Verify the requested game exists.
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve board for game " + gameId + ". Not a valid game id.");

            // No hash provided at all -> return a synced update. We achieve this by setting a hash that clearly differs from any valid hash.
            if (hash == null)
                hash = "-";

            // Looks good. Wait for next timeout or unblocking of corresponding BCM
            return ResponseGenerator.getHashBasedUpdate(longPollTimeout, broadcastContentManagers.get(gameId), hash);

        } catch (ModelAccessException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
            return result;
        }
    }

    @Override
    @GetMapping(value = "/api/games/{gameId}/players", produces = "application/json; charset=utf-8")
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
    @GetMapping(value = "/api/games/{gameId}/players/{player}/actions", produces = "application/json; charset=utf-8")
    public ResponseEntity getActions(@PathVariable long gameId, @PathVariable String player, @RequestParam(name = "access_token") String accessToken) {

        try {
            // Verify the requested game exists
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve players for game " + gameId + ". Not a valid game id.");

            if (skipTokenValidation)
                System.out.println("***WARNING*** Token verification skipped.");
            else {
                // Verify the provided token is a player token
                if (tokenResolver.isPlayerToken(accessToken))
                    throw new LogicException("Received token is an admin token but a player token is required.");

                // Verify the provided token belongs to the specified player
                if (tokenResolver.isMatchingPlayer(player, accessToken))
                    throw new LogicException("Received token does not match player of to accessed resource.");
            }

            // Looks good, build the actions array, serialize it and send it back in a 200 (OK) Http response.
            XoxGame xoxGame = gameManager.getGameById(gameId);
            PlayerReadOnly playerObject = xoxGame.getPlayerByName(player);
            String serializedActions = new Gson().toJson(actionGenerator.generateActions(xoxGame, playerObject));
            return ResponseEntity.status(HttpStatus.OK).body(serializedActions);
        } catch (ModelAccessException | LogicException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @PostMapping(value = "/api/games/{gameId}/players/{player}/actions/{actionMD5}")
    public ResponseEntity<String> selectAction(@PathVariable long gameId, @PathVariable String player, @PathVariable String actionMD5, @RequestParam(name = "access_token") String accessToken) {

        try {
            // Verify the requested game exists
            if (!gameManager.isExistentGameId(gameId))
                throw new ModelAccessException("Can not retrieve players for game " + gameId + ". Not a valid game id.");

            if (skipTokenValidation)
                System.out.println("***WARNING*** Token verification skipped.");
            else {
                // Verify the provided token is a player token
                if (tokenResolver.isPlayerToken(accessToken))
                    throw new LogicException("Received token is an admin token but a player token is required.");

                // Verify the provided token belongs to the specified player
                if (tokenResolver.isMatchingPlayer(player, accessToken))
                    throw new LogicException("Received token does not match player of to accessed resource.");
            }

            // Verify the provided player is a participant of the game
            if (!isPlayer(gameId, player))
                throw new LogicException("Action bundle can not be created. The provided player is not a participant of the referenced game.");

            // Verify the selected action was actually offered
            XoxGame xoxGame = gameManager.getGameById(gameId);
            PlayerReadOnly playerObject = xoxGame.getPlayerByName(player);
            Map<String, Action> actions = actionGenerator.generateActions(xoxGame, playerObject);
            if (!actions.containsKey(actionMD5))
                throw new LogicException("Received MD5 does not match any previously offered action.");

            // Looks good - perform the action by passing it to the XoxActionInterpreter
            Action selectedAction = actions.get(actionMD5);
            actionInterpreter.interpretAndApplyAction(selectedAction, xoxGame);

            // Touch the board, to update all subscribed clients
            broadcastContentManagers.get(gameId).touch();

            // if the game was ended by the action, notify lobbyservice, close board subscriptions.
            if (xoxGame.isFinished())
                registrator.notifyGameOver(gameId);

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (ModelAccessException | LogicException | UnirestException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/api/games/{gameId}")
    public ResponseEntity<String> getRanking(@PathVariable long gameId) {

        try {
            Ranking ranking = rankingGenerator.computeRanking(gameManager.getGameById(gameId));
            return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(ranking));
        } catch (ModelAccessException | LogicException e) {

            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for static information on the registration status.
     */
    @GetMapping("/")
    public String getXoxRegistrationDetails() {
        return new Gson().toJson(lobbyServiceLocation);
    }

    /**
     * Helper method that iterates over player registered for a game and compares to a provided player name.
     *
     * @param gameId     as the id of the game to iterate over.
     * @param playerName as the name to search for
     * @return true if the player is associated by the game, false otherwise.
     */
    private boolean isPlayer(long gameId, String playerName) throws ModelAccessException {
        for (PlayerReadOnly player : gameManager.getGameById(gameId).getPlayers()) {
            if (player.getName().equals(playerName))
                return true;
        }
        return false;
    }
}
