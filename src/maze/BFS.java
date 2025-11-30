package maze;

import java.util.*;

public class BFS {

    public static List<Cell> search(Maze maze, MazePanel panel) {

        // reset any previous flags (defensive)
        maze.clearAlgorithmData();

        Queue<Cell> q = new LinkedList<>();
        Map<Cell, Cell> parent = new HashMap<>();

        Cell start = maze.start;
        Cell goal = maze.goal;
        if (start == null || goal == null) return null;

        q.add(start);
        start.visited = true;
        start.frontier = true;
        panel.repaintPause();

        while (!q.isEmpty()) {
            Cell cur = q.poll();

            if (cur == goal) {
                // reconstruct path (goal→start animation is done by caller or here)
                return buildPath(parent, start, goal, panel);
            }

            for (Cell nxt : neighbors(maze, cur)) {
                if (!nxt.visited && !nxt.wall) {
                    nxt.visited = true;
                    nxt.frontier = true;
                    nxt.parent = cur;
                    parent.put(nxt, cur);

                    panel.repaintPause();
                    q.add(nxt);
                }
            }
        }
        return null;
    }

    private static List<Cell> buildPath(Map<Cell, Cell> parent,
                                        Cell start, Cell goal,
                                        MazePanel panel) {

        List<Cell> path = new ArrayList<>();
        Cell cur = goal;

        // walk back using parent pointers and animate goal->start
        while (cur != null && cur != start) {
            cur.path = true;
            panel.repaintPause();
            path.add(cur);
            cur = parent.get(cur);
        }
        // include start
        start.path = true;
        panel.repaintPause();
        path.add(start);

        // path currently is goal->start; reverse to return start->goal if you want
        Collections.reverse(path);
        return path;
    }

    private static List<Cell> neighbors(Maze maze, Cell c) {
        List<Cell> list = new ArrayList<>();
        int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : DIRS) {
            int nr = c.row + d[0];
            int nc = c.col + d[1];
            if (nr >= 0 && nc >= 0 && nr < maze.rows && nc < maze.cols)
                list.add(maze.grid[nr][nc]);
        }
        return list;
    }
}
