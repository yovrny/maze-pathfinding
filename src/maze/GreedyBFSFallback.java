package maze;

import java.util.*;

/**
 * Greedy + BFS Fallback Algorithm
 * 1. Greedy: Only move to neighbors with LOWER heatmap score
 * 2. Stuck: When no descending neighbor exists
 * 3. BFS Rescue: Search broadly (no restrictions) until finding better position
 * 4. Resume: Continue greedy from rescue point
 */

public class GreedyBFSFallback {

    private static final int SAFETY_LIMIT = 100000;

    public static void run(Maze maze, MazePanel panel) {
        if (maze == null || panel == null) return;
        maze.clearAlgorithmData();

        int rows = maze.rows;
        int cols = maze.cols;
        Cell start = maze.start;
        Cell goal = maze.goal;
        
        if (start == null || goal == null) return;

        // Build heatmap (normalized Manhattan distance to goal)
        double denom = rows + cols;
        double[][] heatmap = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heatmap[r][c] = (Math.abs(r - goal.row) + Math.abs(c - goal.col)) / denom;
            }
        }

        // Optional: expose heatmap for visualization
        panel.debugHeatmap = heatmap;

        // Track visited cells globally
        Set<Cell> globalVisited = new HashSet<>();
        Map<Cell, Cell> parent = new HashMap<>();

        Cell current = start;
        parent.put(start, null);
        globalVisited.add(start);
        current.frontier = true;
        panel.repaintPause();

        int steps = 0;

        // Main loop
        while (current != null && steps < SAFETY_LIMIT) {
            steps++;

            // Mark as visited
            current.visited = true;
            panel.repaintPause();

            // Check if goal reached
            if (current.equals(goal)) {
                reconstructPath(parent, start, goal, panel);
                return;
            }

            double currentScore = heatmap[current.row][current.col];
            List<Cell> neighbors = getNeighbors(maze, current);

            // Find UNVISITED neighbors with LOWER heatmap score
            Cell bestGreedy = null;
            double bestScore = currentScore;

            for (Cell neighbor : neighbors) {
                if (!globalVisited.contains(neighbor)) {
                    double neighborScore = heatmap[neighbor.row][neighbor.col];
                    if (neighborScore < bestScore) {
                        bestScore = neighborScore;
                        bestGreedy = neighbor;
                    }
                }
            }

            // CASE A: Greedy can descend
            if (bestGreedy != null) {
                parent.put(bestGreedy, current);
                globalVisited.add(bestGreedy);
                current = bestGreedy;
                current.frontier = true;
                panel.repaintPause();
            }
            // CASE B: Stuck - run BFS to find escape route
            else {
                Cell rescueTarget = bfsFallback(maze, current, currentScore, 
                                                heatmap, globalVisited, parent, panel);
                
                if (rescueTarget != null) {
                    current = rescueTarget;
                } else {
                    // Truly stuck - no path exists
                    System.out.println("Algorithm stuck - no valid moves from current position");
                    break;
                }
            }
        }

        System.out.println("No path found after " + steps + " steps");
    }

    /**
     * BFS Fallback: Unrestricted search to find escape
     * Explores ALL reachable unvisited cells until finding one with better score
     */
    private static Cell bfsFallback(Maze maze, Cell stuck, double stuckScore,
                                    double[][] heatmap, Set<Cell> globalVisited,
                                    Map<Cell, Cell> parent, MazePanel panel) {
        
        Queue<Cell> bfsQueue = new LinkedList<>();
        Set<Cell> bfsVisited = new HashSet<>();
        Map<Cell, Cell> bfsParent = new HashMap<>();
        
        bfsQueue.add(stuck);
        bfsVisited.add(stuck);
        bfsParent.put(stuck, null);

        while (!bfsQueue.isEmpty()) {
            Cell current = bfsQueue.poll();
            
            // Visualize BFS exploration
            if (!current.equals(stuck)) {
                current.frontier = true;
                panel.repaintPause();
            }

            double currentScore = heatmap[current.row][current.col];

            // Found a better position!
            if (currentScore < stuckScore && !globalVisited.contains(current)) {
                // Trace back through BFS to mark path
                Cell trace = current;
                while (trace != null && !trace.equals(stuck)) {
                    Cell bfsP = bfsParent.get(trace);
                    if (bfsP != null && !globalVisited.contains(trace)) {
                        parent.put(trace, bfsP);
                        globalVisited.add(trace);
                    }
                    trace = bfsP;
                }
                return current;
            }

            // Expand BFS
            List<Cell> neighbors = getNeighbors(maze, current);
            for (Cell neighbor : neighbors) {
                if (!bfsVisited.contains(neighbor)) {
                    bfsVisited.add(neighbor);
                    bfsParent.put(neighbor, current);
                    bfsQueue.add(neighbor);
                    neighbor.frontier = true;
                    panel.repaintPause();
                }
            }
        }

        return null; // No escape found
    }


    private static void reconstructPath(Map<Cell, Cell> parent, Cell start, 
                                       Cell goal, MazePanel panel) {
        List<Cell> path = new ArrayList<>();
        Cell current = goal;

        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);

        // Animate final path in yellow
        for (Cell cell : path) {
            cell.path = true;
            panel.repaintPause();
        }
    }

    // Get Valid neighbors
    
    private static List<Cell> getNeighbors(Maze maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.row;
        int c = cell.col;

        if (r > 0 && !maze.grid[r-1][c].wall) {
            neighbors.add(maze.grid[r-1][c]);
        }
        if (r < maze.rows - 1 && !maze.grid[r+1][c].wall) {
            neighbors.add(maze.grid[r+1][c]);
        }
        if (c > 0 && !maze.grid[r][c-1].wall) {
            neighbors.add(maze.grid[r][c-1]);
        }
        if (c < maze.cols - 1 && !maze.grid[r][c+1].wall) {
            neighbors.add(maze.grid[r][c+1]);
        }

        return neighbors;
    }
}