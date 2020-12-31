package eu.kartoffelquadrat.lobbyservice.samplegame.model.xoxmodel;


import eu.kartoffelquadrat.lobbyservice.samplegame.model.Board;

/**
 * Xox specific implementation of the board interface. Encodes 3x3 matrix with individually maintained state model per
 * cell.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public class XoxBoard implements Board {

    // States of cells are encoded by Characters:
    // ' ': empty. 'x': occupied by x, 'o': occupied by o, 'X' winning cell of x, 'O' winning cell of o.
    private final char[][] cells;

    protected XoxBoard() {
        cells = new char[3][3];
        initCells();
    }

    /**
     * Iterates of a board and returns true if all cells are populated.
     *
     * @return
     */
    public boolean isFull() {

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y] == ' ')
                    return false;
            }
        }
        return true;
    }

    /**
     * Insepcts the cells and returns true if three equal symbols lie on row, column or main diagonal.
     *
     * @return
     */
    public boolean isThreeInALine() {

        // check for three in a row
        for (int y = 0; y < cells[0].length; y++) {
            if (cells[0][y] == cells[1][y] && cells[1][y] == cells[2][y])
                return true;
        }

        // check for three in a column
        for (int x = 0; x < cells.length; x++) {
            if (cells[x][0] == cells[x][1] && cells[x][1] == cells[x][2])
                return true;
        }

        // check diagonals
        if (cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2])
            return true;
        if (cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0])
            return true;

        return false;
    }

    public boolean isFree(int xPos, int yPos) {

        return cells[yPos][xPos] == ' ';
    }

    /**
     * Iterates over the board and initializes all cells with the whitespace character.
     */
    private void initCells() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y] = ' ';
            }
        }
    }
}
