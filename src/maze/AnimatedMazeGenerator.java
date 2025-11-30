package maze;

import java.util.*;

public class AnimatedMazeGenerator {

    public static MazePanel panel;         // set by MazePanel constructor
    public static int carveDelay = 8;      // ms per carve step (adjust)

    /**
     * Animated randomized DFS maze generation.
     * This function updates maze.grid in place and calls panel.repaint() during carving.
     */
    public static void generate(Maze maze) {
        // safety: if panel not set yet, we'll still update grid but won't animate
        Cell[][] grid = maze.grid;
        int rows = maze.rows;
        int cols = maze.cols;

        // start with all walls
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                grid[r][c].wall = true;
                grid[r][c].visited = false;
                grid[r][c].frontier = false;
                grid[r][c].path = false;
                grid[r][c].parent = null;
            }

        Random rng = new Random();

        // pick starting odd coordinates (1,1) is fine
        int sr = 1;
        int sc = 1;
        if (rows > 3 && cols > 3) {
            sr = 1 + 2 * rng.nextInt(Math.max(1, (rows - 2) / 2));
            sc = 1 + 2 * rng.nextInt(Math.max(1, (cols - 2) / 2));
        }

        grid[sr][sc].wall = false;
        grid[sr][sc].visited = true;

        Stack<Cell> stack = new Stack<>();
        stack.push(grid[sr][sc]);

        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};

        while (!stack.isEmpty()) {
            Cell cur = stack.peek();

            // animate current iteration
            animateStep();

            // gather unvisited neighbors 2 steps away
            List<Cell> neighbors = new ArrayList<>();
            for (int[] d : dirs) {
                int nr = cur.row + d[0];
                int nc = cur.col + d[1];
                if (nr > 0 && nc > 0 && nr < rows - 1 && nc < cols - 1) {
                    if (grid[nr][nc].wall && !grid[nr][nc].visited) {
                        neighbors.add(grid[nr][nc]);
                    }
                }
            }

            if (neighbors.isEmpty()) {
                stack.pop();
                continue;
            }

            Cell next = neighbors.get(rng.nextInt(neighbors.size()));
            next.visited = true;

            // carve the wall in between
            int mr = (cur.row + next.row) / 2;
            int mc = (cur.col + next.col) / 2;
            grid[mr][mc].wall = false;
            animateStep();

            next.wall = false;
            animateStep();

            stack.push(next);
        }

        // add a few random openings to increase complexity
        int extra = rows * cols / 60;
        for (int i = 0; i < extra; i++) {
            int r = 1 + rng.nextInt(Math.max(1, rows - 2));
            int c = 1 + rng.nextInt(Math.max(1, cols - 2));
            grid[r][c].wall = false;
        }

        // ensure generator flags aren't left set
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                grid[r][c].visited = false;
            }

        // final repaint
        animateStep();
    }

    private static void animateStep() {
        if (panel != null) {
            panel.repaint();
            try { Thread.sleep(carveDelay); } catch (InterruptedException ignored) {}
        }
    }

   
    // Helper wrappers to run searches on background threads
 
    public static void runBFS(Maze maze, MazePanel p) {
        // attach panel so searches that use panel.repaintPause() can work
        panel = p;
        new Thread(() -> {
            maze.clearAlgorithmData();
            BFS.search(maze, p);
        }).start();
    }

    public static void runDFS(Maze maze, MazePanel p) {
        panel = p;
        new Thread(() -> {
            maze.clearAlgorithmData();
            DFS.search(maze, p);
        }).start();
    }

    public static void runOptimistic(Maze maze, MazePanel p) {
        panel = p;
        new Thread(() -> {
            maze.clearAlgorithmData();   // ensure fresh visualization
            Optimistic.search(maze, p);
        }).start();
    }   
    
    public static void runGreedyHeatmap(Maze maze, MazePanel p) {
        panel = p;
        new Thread(() -> {
            maze.clearAlgorithmData();
            GreedyBFSFallback.run(maze, p);
        }).start();
    }


}
