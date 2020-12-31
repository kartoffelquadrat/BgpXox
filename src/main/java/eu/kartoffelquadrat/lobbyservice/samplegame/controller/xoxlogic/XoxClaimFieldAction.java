package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.samplegame.model.PlayerReadOnly;

/**
 * Represents the only kind of blackboard action required in Xox. The Claim-Field action encodes a position, specified
 * through x and y coordinates and a player who claims the field. The origin is top-left, index counting starts at 0.
 */
public class XoxClaimFieldAction {

    private final int x;
    private final int y;
    private final PlayerReadOnly player;

    /**
     * Constructor for a Xox action encoding the action that represents marking a specific cell (specified by x/y
     * coordinates.)
     *
     * @param x as the column-index to be populated by this action
     * @param y as the row-index to be populated by this action
     */
    public XoxClaimFieldAction(int x, int y, PlayerReadOnly player) {
        if (x < 0 || x > 2)
            throw new LogicException("Xox action can not be created. X position is out of bounds.");
        if (y < 0 || y > 2)
            throw new LogicException("Xox action can not be created. Y Position is out of bounds.");

        this.x = x;
        this.y = y;
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlayerReadOnly getPlayer() {
        return player;
    }
}
