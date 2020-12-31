/**
 * REST API handlers of Xox.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import com.google.gson.Gson;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.GameRestController;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.LauncherInfo;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.GameManager;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private GameManager<XoxGame> gameManager;

    private ActionGenerator actionGenerator;

    private String gameServiceName;

    public XoxRestController(
            @Autowired GameManager gameManager,
            @Autowired ActionGenerator actionGenerator,
            @Value("${gameservice.name}") String gameServiceName) {
        this.gameManager = gameManager;
        this.gameServiceName = gameServiceName;
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
    public void launchGame(@PathVariable long gameId, LauncherInfo launcherInfo, String token) {
        if (launcherInfo == null || launcherInfo.getGameServer() == null)
            throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
        if (!launcherInfo.getGameServer().equals(gameServiceName))
            throw new LogicException("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
        if (gameManager.isExistentGameId(gameId))
            throw new LogicException("Game can not be launched. Id is already in use.");
        else {
            gameManager.addGame(gameId, launcherInfo.getPlayers().toArray(new Player[launcherInfo.getPlayers().size()]));
        }
    }

    @Override
    @DeleteMapping("/api/games/{gameId}")
    public void deleteGame(@PathVariable long gameId, String token) {

        // Verify the provided game id is valid
        if (!gameManager.isExistentGameId(gameId))
            throw new ModelAccessException("Game can not be removed. No game associated to provided gameId");

        // ToDo: Verify how this canges if a creator token is used. Inspect LS sources if termination of running games is allowed for creators.
        gameManager.removeGame(gameId, true);
    }

    @Override
    public String getBoard(@PathVariable long gameId, @RequestParam(required = false) String hash) {

        // ToDo: implement long polling

        if(!gameManager.isExistentGameId(gameId))
            throw new ModelAccessException("Can not retrieve board for game "+gameId+". Not a valid game id.");

        return new Gson().toJson(gameManager.getGameById(gameId));
    }

    @Override
    public String getPlayers(@PathVariable long gameId) {

        if(!gameManager.isExistentGameId(gameId))
            throw new ModelAccessException("Can not retrieve players for game "+gameId+". Not a valid game id.");

        return new Gson().toJson(gameManager.getGameById(gameId).getBoard());
    }

    @Override
    public String getActions(@PathVariable long gameId, String player, String token) {

        if(!gameManager.isExistentGameId(gameId))
            throw new ModelAccessException("Can not retrieve players for game "+gameId+". Not a valid game id.");

        // ToDo: verify token belongs to player

        // ToDo: verify player is participant

        // ToDo: verify if its the players turn

        // ToDo: resolve player to player object... or convert down call stack below...

        XoxGame xoxGame = gameManager.getGameById(gameId);
        PlayerReadOnly playerObject = xoxGame.getPlayerByName(player);
        return new Gson().toJson(actionGenerator.generateActions(xoxGame, playerObject));
    }
}
