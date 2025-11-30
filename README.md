# Maze Solver & Generator (Java)

A Java-based maze explorer featuring multiple search algorithms, a DFS maze generator, and a custom hybrid greedy + heatmap pathfinding strategy designed to balance speed and accuracy.

This project began as an exploration of how different algorithms handle unknown environments, inspired partly by real-world disaster robotics and curiosity sparked from a Veritasium video on navigating mazes. Over time, it grew into a fun project I worked on over the week of Thanksgiving break.

---

## Features

### Maze Generation
- **DFS Maze Generation** — creates perfect mazes with randomized depth-first search.

### Pathfinding Algorithms
- **DFS Solver** — fast, but not shortest-path.
- **BFS Solver** — guaranteed shortest path, but slow due to full-state exploration.
- **Greedy Heatmap (Custom Algorithm)**  
  A hybrid approach combining:
  - Greedy movement toward the goal
  - Local heatmap-based scoring
  - Visiting-score penalties
  - Dead-zone detection (partial wall-following escape)

Designed to find *good* paths quickly in unknown spaces while avoiding BFS’s full search.

---

## 🎯 Goal of the Project
- Compare classical algorithms (BFS/DFS) with custom heuristics
- Explore maze generation + maze solving as a combined system
- Create a visual, interactive tool for algorithm experimentation
- Reason about real-world constraints:  
  *“If a small robot must find a path in a collapsed building, accuracy matters, but speed matters more.”*

---

## 🖼 Demo 
Maze Generation:
![MazeGengif](https://github.com/user-attachments/assets/38d54463-7c73-4218-8152-bade3214b59a)
DFS: 
![DFSgif](https://github.com/user-attachments/assets/8118c0d4-5a3f-4b4c-9ee5-22e50cdc73c0)
BFS:
![BFSgif](https://github.com/user-attachments/assets/24827a44-7dca-4b55-8b02-ca1dd119d464)
Custom Algorithm:
![Greedywithfallbackgif](https://github.com/user-attachments/assets/0235e3c9-8276-4a7b-bed1-b1abc1884d11)


