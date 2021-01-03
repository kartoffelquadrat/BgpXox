package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import org.springframework.stereotype.Component;

/**
 * Generic ending analyzer interface. Implement this interface to provide a tailored analyzer that determines when
 * entities of the game you implement have reached an end criteria.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Component
public interface EndingAnalyzer {
    boolean analyzeAndUpdate(Game game) throws LogicException;
}
