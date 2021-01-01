package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;

/**
 * Generic interface for Blackboard-style action interpreters that apply provided player actions on a game instance.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface ActionInterpreter {

    void interpretAndApplyAction(Action action, Game game);
}
