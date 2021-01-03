package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.Action;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.ActionInterpreter;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.ModelAccessException;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Business Logic class that applies a Xox Action on a provided Xox model instance. A Xox action encodes a players
 * request to lay on a given position. The ActionInterpreter verifies that the action is legal for the provided user. If
 * this is the case the provided Xox model instance is modified as requested.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public class XoxActionInterpreter implements ActionInterpreter {

    private final ActionGenerator actionGenerator;

    public XoxActionInterpreter(@Autowired ActionGenerator actionGenerator) {
        this.actionGenerator = actionGenerator;
    }

    @Override
    public void interpretAndApplyAction(Action action, Game game) throws LogicException, ModelAccessException {

        // Verify action and game input type
        if (action.getClass() != XoxClaimFieldAction.class)
            throw new LogicException("Xox Action Interpreter can only handle XoxClaimFieldActions");
        if (game.getClass() != XoxGame.class)
            throw new LogicException("Xox Action Interpreter can only handle XoxGames");
        XoxClaimFieldAction xoxClaimFieldAction = (XoxClaimFieldAction) action;
        XoxGame xoxGame = (XoxGame) game;

        // Verify the action is legit (must be included in list of actions provided by generator)
        if (!isValidAction(game, xoxClaimFieldAction))
            throw new LogicException("Provided action can not be applied on game - is not a valid action.");

        // Apply action on model
        xoxGame.getBoard().occupy(xoxClaimFieldAction.getX(), xoxClaimFieldAction.getY(), xoxGame.isFirstPlayer(xoxClaimFieldAction.getPlayer()));

        // Update current player
        xoxGame.setCurrentPlayer(1-xoxGame.getCurrentPlayerIndex());
    }

    /**
     * Helper method verify if a specific xox action is contained in an actions bundle. The action is identified by grid
     * position and player information.
     */
    private boolean isValidAction(Game game, XoxClaimFieldAction selectedAction) throws LogicException {

        // retrieve all valid actions for player
        Collection<Action> validActions = actionGenerator.generateActions(game, selectedAction.getPlayer()).values();

        // look up if provided action is contained
        for (Action currentAction : validActions) {
            if (currentAction.equals(selectedAction))
                return true;
        }
        return false;
    }
}
