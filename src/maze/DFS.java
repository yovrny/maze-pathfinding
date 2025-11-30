package maze;

import java.util.*;

public class DFS {

    public static List<Cell> search(Maze maze, MazePanel panel) {

        // reset
        maze.clearAlgorithmData();

        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> parent = new HashMap<>();

        Cell start = maze.start;
        Cell goal = maze.goal;
        if (start == null || goal == null) return null;

        stack.push(start);
        start.visited = true;
        start.frontier = true;
        panel.repaintPause();

        while (!stack.isEmpty()) {
            Cell cur = stack.pop();

            if (cur == goal) {
                return buildPath(parent, start, goal, panel);
            }

            for (Cell nxt : neighbors(maze, cur)) {
                if (!nxt.visited && !nxt.wall) {
                    nxt.visited = true;
                    nxt.frontier = true;
                    nxt.parent = cur;
                    parent.put(nxt, cur);

                    panel.repaintPause();
                    stack.push(nxt);
                }
            }
        }
        return null;
    }

    private static List<Cell> buildPath(Map<Cell, Cell> parent, Cell start, Cell goal, MazePanel panel) {
        List<Cell> path = new ArrayList<>();
        Cell cur = goal;
        while (cur != null && cur != start) {
            cur.path = true;
            panel.repaintPause();
            path.add(cur);
            cur = parent.get(cur);
        }
        start.path = true;
        panel.repaintPause();
        path.add(start);
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
