package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Player;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

/**
 * Generic interface for Blockboard-style action generators that provide collections of selectable player options.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public interface ActionGenerator {

    Action[] generateActions(XoxGame game, PlayerReadOnly player);
}
