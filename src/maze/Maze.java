package maze;

import java.util.Random;

public class Maze {

    public int rows, cols;
    public Cell[][] grid;

    public Cell start;
    public Cell goal;

    public Maze(int rows, int cols) {
        // ensure odd dimensions for nice carving 
        this.rows = (rows % 2 == 0) ? rows + 1 : rows;
        this.cols = (cols % 2 == 0) ? cols + 1 : cols;

        grid = new Cell[this.rows][this.cols];

        // initialize grid — default wall = true
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                grid[r][c] = new Cell(r, c, true);
            }
        }

        // set temporary start/goal (will be adjusted after generation)
        start = grid[1][1];
        goal = grid[this.rows - 2][this.cols - 2];
    }

    //Generate (animated) — calls AnimatedMazeGenerator.generate(this). Call from a background thread so Swing can repaint.
   
    public void generate() {
        AnimatedMazeGenerator.generate(this);
        // after generation ensure start/goal are open and placed
        start = grid[1][1];
        start.wall = false;
        // carve doorway to border 
        if (rows > 2) grid[0][1].wall = false;

        // place a goal somewhere in bottom-right quadrant that's open
        Random rng = new Random();
        int r, c;
        do {
            r = rows/2 + rng.nextInt(Math.max(1, rows/2 - 1));
            c = cols/2 + rng.nextInt(Math.max(1, cols/2 - 1));
        } while (grid[r][c].wall);
        goal = grid[r][c];
    }

    public void clearAlgorithmData() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                cell.frontier = false;
                cell.path = false;
                cell.visited = false;
                cell.parent = null;
            }
        }
    }
}
