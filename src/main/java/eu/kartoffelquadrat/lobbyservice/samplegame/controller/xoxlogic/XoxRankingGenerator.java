package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.controller.LogicException;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.RankingGenerator;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.Game;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;
import eu.kartoffelquadrat.lobbyservice.samplegame.controller.communcationbeans.Ranking;
import eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel.XoxGame;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;

@Component
public class XoxRankingGenerator implements RankingGenerator {

    @Override
    public Ranking computeRanking(Game game) throws LogicException {

        // can only be applied on Xox games
        if(game.getClass() != XoxGame.class)
            throw new LogicException("Xox Ranking generator can only operate on Xox games.");
        XoxGame xoxGame = (XoxGame) game;

        // Will only provide a ranking with non-0 scores, if the game has already ended.
        if(!((XoxGame) game).isFinished())
            return new Ranking(game.getPlayers(), new int[]{0,0}, false);

        // Verify there actually is a player, if not:
        if(isDraw(xoxGame))
            return new Ranking(game.getPlayers(), new int[]{0,0}, true);

        // Winner (player with 3 in a row) gets 1 point, looser 0.
        char winnerChar = xoxGame.getBoard().getThreeInALineCharIfExists();
        PlayerReadOnly[] rankedPlayers = game.getPlayers();

        // If the non-creator won, return a ranking that is the inverse of the games player listing.
        if(winnerChar != 'x')
            ArrayUtils.reverse(rankedPlayers);
        return new Ranking(rankedPlayers, new int[]{1, 0}, true);
    }

    /**
     * Analyze if the provided game resulted in a draw (no winner)
     * @return
     */
    private boolean isDraw(XoxGame game)
    {
        if(!game.isFinished())
            return false;

        return !game.getBoard().isThreeInALine();
    }

    private PlayerReadOnly getWinner(XoxGame game)
    {
        // Todo Implement;
        return null;
    }
}
