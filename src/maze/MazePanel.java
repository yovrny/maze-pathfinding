package maze;

import javax.swing.*;
import java.awt.*;

/**
 * MazePanel: draws maze grid, start/goal, frontier and final path.
 * Also supports a colored heatmap overlay (blue -> red) via debugHeatmap.
 */
public class MazePanel extends JPanel {
    public Maze maze;

    // heatmap visualization
    public boolean showHeatmap = false;
    public double[][] debugHeatmap = null; // normalized values [0..1]
    public float heatmapAlpha = 0.45f;

    private final int cellSize = 14;

    public MazePanel(Maze maze) {
        this.maze = maze;
        setBackground(Color.DARK_GRAY);
    }

    public void repaintPause() {
        repaint();
        try { Thread.sleep(12); } catch (InterruptedException ignored) {}
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(maze.cols * cellSize, maze.rows * cellSize);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        if (maze == null) return;

        for (int r = 0; r < maze.rows; r++) {
            for (int c = 0; c < maze.cols; c++) {
                Cell cell = maze.grid[r][c];
                int x = c * cellSize;
                int y = r * cellSize;

                // base: wall / floor
                if (cell.wall) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x, y, cellSize, cellSize);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // frontier (algorithm visited)
                if (cell.frontier) {
                    g.setColor(new Color(100, 160, 240)); // light blue
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // final path
                if (cell.path) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // heatmap overlay (blue -> red)
                if (showHeatmap && debugHeatmap != null) {
                    double v = debugHeatmap[r][c];
                    v = Math.max(0.0, Math.min(1.0, v));
                    // blue (cold) -> red (hot)
                    Color heat = new Color((float)(1.0 - v), 0f, (float)v, heatmapAlpha);
                    g.setColor(heat);
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // start/goal on top
                if (maze.start == cell) {
                    g.setColor(new Color(30, 200, 30));
                    g.fillRect(x, y, cellSize, cellSize);
                } else if (maze.goal == cell) {
                    g.setColor(new Color(200, 30, 30));
                    g.fillRect(x, y, cellSize, cellSize);
                }

                // thin grid line
                g.setColor(new Color(100, 100, 100, 60));
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
}
