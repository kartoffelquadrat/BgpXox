package eu.kartoffelquadrat.lobbyservice.xox.controller.xoxlogic;

import eu.kartoffelquadrat.lobbyservice.xox.controller.xoxlogic.LogicException;

/**
 * Represents an action offered to a player at a given moment in the game. Every action encodes a position, specified
 * through x and y coordinates. The origin is top-left, index counting starts at 0.
 */
public class XoxAction {

    private final int x;
    private final int y;

    /**
     * Constructor for a Xox action encoding the action that represents marking a specific cell (specified by x/y
     * coordinates.)
     *
     * @param x as the column-index to be populated by this action
     * @param y as the row-index to be populated by this action
     */
    public XoxAction(int x, int y) {
        if (x < 0 || x > 2)
            throw new LogicException("Xox action can not be created. X position is out of bounds.");
        if (y < 0 || y > 2)
            throw new LogicException("Xox action can not be created. Y Position is out of bounds.");

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
