package maze;

import javax.swing.*;
import java.awt.*;

public class MazeApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // create data
            Maze maze = new Maze(41, 41);

            // create UI
            MazePanel panel = new MazePanel(maze);

            JFrame frame = new JFrame("Maze Solver");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // top controls
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton generateBtn = new JButton("Generate Maze");
            JComboBox<SearchType> algoBox = new JComboBox<>(SearchType.values());
            JButton runBtn = new JButton("Run");
            JButton clearBtn = new JButton("Clear Path");
            JSlider speedSlider = new JSlider(2, 60, 12); // ms delay control
            JCheckBox heatmapToggle = new JCheckBox("Show Heatmap");
            heatmapToggle.addActionListener(e -> {
                panel.showHeatmap = heatmapToggle.isSelected();
                panel.repaint();
            });
            top.add(heatmapToggle);



            top.add(generateBtn);
            top.add(new JLabel("Algorithm:"));
            top.add(algoBox);
            top.add(runBtn);
            top.add(clearBtn);
            top.add(new JLabel("Speed:"));
            top.add(speedSlider);

            frame.add(top, BorderLayout.NORTH);
            frame.add(new JScrollPane(panel), BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // wire actions
            // generate in background
            generateBtn.addActionListener(e -> {
                new Thread(() -> {
                    AnimatedMazeGenerator.panel = panel;
                    AnimatedMazeGenerator.carveDelay = Math.max(1, 60 - speedSlider.getValue());
                    maze.generate();
                }).start();
            });

            // run selected algorithm in background
            runBtn.addActionListener(e -> {
                SearchType sel = (SearchType) algoBox.getSelectedItem();
                AnimatedMazeGenerator.panel = panel;
                // adjust delays based on speed slider
                int delay = Math.max(1, 60 - speedSlider.getValue());
                AnimatedMazeGenerator.carveDelay = delay;
                panel.repaint(); // ensure current state visible

                if (sel == SearchType.BFS) AnimatedMazeGenerator.runBFS(maze, panel);
                else if (sel == SearchType.DFS) AnimatedMazeGenerator.runDFS(maze, panel);
                else if (sel == SearchType.EXPERIMENTAL_OPTIMISTIC) AnimatedMazeGenerator.runOptimistic(maze, panel);
                else if (sel == SearchType.GREEDY_HEATMAP) AnimatedMazeGenerator.runGreedyHeatmap(maze, panel);


            });

            // clear path states (re-run search without regen)
            clearBtn.addActionListener(e -> {
                maze.clearAlgorithmData();
                panel.repaint();
            });

            // update speed live
            speedSlider.addChangeListener(ev -> {
                int delay = Math.max(1, 60 - speedSlider.getValue());
                AnimatedMazeGenerator.carveDelay = delay;
            });

            // initial generation
            new Thread(() -> {
                AnimatedMazeGenerator.panel = panel;
                AnimatedMazeGenerator.carveDelay = Math.max(1, 60 - speedSlider.getValue());
                maze.generate();
            }).start();
        });
    }
}
