package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Ranking;
import org.springframework.stereotype.Component;

/**
 * Custom game specific classes implementing this interface can be applied on a game at any state, to compute the
 * current ranking (scores per player, descending order.)
 */
@Component
public interface RankingGenerator {

    /**
     *
     * @param game as the game instance to be evaluated.
     * @return a ranking object that tells the scores per player and the players in descending order.
     */
    Ranking computeRanking(Game game) throws LogicException;
}
