package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.RankingGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Ranking;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;

public class XoxRankingGenerator implements RankingGenerator {


    @Override
    public Ranking computeRanking(Game game) throws LogicException {

        // can only be applied on Xox games
        if(game.getClass() != XoxGame.class)
            throw new LogicException("Xox Ranking generator can only operate on Xox games.");
        XoxGame xoxGame = (XoxGame) game;

        // Will only provide a ranking with non-0 scores, if the game has already ended.
        if(!((XoxGame) game).isFinished())
            return new Ranking(game.getPlayers(), new int[]{0,0});

        // Verify there actually is a player, if not:
            // return new Ranking(game.getPlayers(), new int[]{0,0});

        // Winner (player with 3 in a row) gets 1 point, looser 0.

    }

    /**
     * Analyz
     * @return
     */
    private boolean isDraw(XoxGame game)
    {
        // Todo Implement;
        return false;
    }

    private PlayerReadOnly getWinner(XoxGame game)
    {
        // Todo Implement;
        return null;
    }
}
