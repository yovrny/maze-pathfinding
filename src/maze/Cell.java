package maze;

public class Cell {
    public int row, col;
    public boolean wall;

    public boolean frontier;   // BFS/DFS explored
    public boolean path;       // final path coloring
    public boolean visited;    // used by searches / generator
    public Cell parent;        // BFS/DFS tree

    public Cell(int r, int c, boolean wall) {
        this.row = r;
        this.col = c;
        this.wall = wall;
        this.frontier = false;
        this.path = false;
        this.visited = false;
        this.parent = null;
    }
}
