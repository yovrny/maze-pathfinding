package maze;

import java.util.*;

public class Optimistic {

    // Direction vectors
    private static final int[][] DIRS = {
            {0, 1},   // RIGHT
            {1, 0},   // DOWN
            {0, -1},  // LEFT
            {-1, 0}   // UP
    };

    // Wall-following direction (right-hand rule)
    private static int turnRight(int d) { return (d + 1) % 4; }
    private static int turnLeft(int d)  { return (d + 3) % 4; }

    public static void search(Maze maze, MazePanel panel) {

        Cell start = maze.start;
        Cell goal  = maze.goal;

        List<Cell> takenPath = new ArrayList<>();  // final path list

        Cell current = start;
        current.frontier = true;
        panel.repaintPause();

        while (current != goal) {

            // 1. Try GREEDY step
            Cell greedyNext = greedyStep(maze, current, goal);

            if (greedyNext != null && !greedyNext.wall) {
                // Take greedy step
                greedyNext.frontier = true;
                takenPath.add(greedyNext);
                current = greedyNext;
                panel.repaintPause();
                continue;
            }

            // 2. GREEDY blocked → wall-follow mode 
            int dir = directionTowardGoal(current, goal);   // approx movement direction

            // Enter wall-following loop
            current = wallFollow(maze, panel, current, dir, goal, takenPath);

            if (current == null) {
                System.out.println("No path found (Bug2 failed).");
                return;
            }
        }

        // final paint
        for (Cell c : takenPath) {
            c.path = true;
            panel.repaintPause();
        }

        goal.path = true;
        panel.repaintPause();
    }


    // GREEDY STEP
    private static Cell greedyStep(Maze maze, Cell c, Cell goal) {

        int bestDr = Integer.compare(goal.row, c.row);
        int bestDc = Integer.compare(goal.col, c.col);

        int nr = c.row + bestDr;
        int nc = c.col + bestDc;

        if (inBounds(maze, nr, nc)) {
            return maze.grid[nr][nc];
        }
        return null;
    }


    // Convert greedy motion to direction index
    private static int directionTowardGoal(Cell c, Cell g) {
        int dr = Integer.compare(g.row, c.row);
        int dc = Integer.compare(g.col, c.col);

        if (dr == 0 && dc == 1)  return 0; // RIGHT
        if (dr == 1 && dc == 0)  return 1; // DOWN
        if (dr == 0 && dc == -1) return 2; // LEFT
        if (dr == -1 && dc == 0) return 3; // UP

        // diagonal → pick dominant axis
        if (Math.abs(dr) >= Math.abs(dc))
            return (dr == 1 ? 1 : 3);
        else
            return (dc == 1 ? 0 : 2);
    }


    
    // WALL FOLLOWING 
 
    private static Cell wallFollow(Maze maze, MazePanel panel,
                                   Cell start, int dir,
                                   Cell goal, List<Cell> taken) {

        Cell current = start;

        // Mark start of wall-following
        // Try to turn right first (right-hand rule)
        dir = turnRight(dir);

        while (true) {

            // Check if greedy direction is now open → resume greedy mode
            Cell greedy = greedyStep(maze, current, goal);
            if (greedy != null && !greedy.wall) {
                return current;   // caller resumes greedy from here
            }

            // Try to move forward following wall
            int nr = current.row + DIRS[dir][0];
            int nc = current.col + DIRS[dir][1];

            if (inBounds(maze, nr, nc) && !maze.grid[nr][nc].wall) {
                // Move forward
                current = maze.grid[nr][nc];
                current.frontier = true;
                taken.add(current);
                panel.repaintPause();

                if (current == goal)
                    return current;

                // Try to turn right (stay hugging the wall)
                dir = turnRight(dir);
            } else {
                // Turn left until open
                dir = turnLeft(dir);
            }

            // Safety: if we return to the exact starting cell AND direction,
            // and never found an exit - no solution
            if (current == start)
                return null;
        }
    }


    private static boolean inBounds(Maze maze, int r, int c) {
        return r >= 0 && c >= 0 && r < maze.rows && c < maze.cols;
    }
}
