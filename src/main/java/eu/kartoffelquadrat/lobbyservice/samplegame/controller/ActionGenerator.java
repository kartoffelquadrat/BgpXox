package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

import java.util.Map;

/**
 * Generic interface for Blockboard-style action generators that provide collections of selectable player options.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface ActionGenerator {

    /**
     * Generates a map of actions, where each object is indexed by the MD5 hash of its json string representation.
     *
     * @param game
     * @param player
     * @return
     * @throws LogicException
     */
    Map<String, Action> generateActions(Game game, PlayerReadOnly player) throws LogicException;
}
