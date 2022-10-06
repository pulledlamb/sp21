package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;
import java.util.List;

public class Guard extends Creature {
    AStarGraph graph;
    Player prey;
    List<Position> projectedPath;

    public Guard(TETile[][] world, Position p, Player prey) {
        super(world, p);
        this.prey = prey;
        projectedPath = new ArrayList<>();
        buildGraph();
        drawGuard();
    }

    public void buildGraph() {
        graph = new AStarGraph();

        for (int i = 0; i < Engine.WIDTH; i++) {
            for (int j = 0; j < Engine.HEIGHT; j++) {
                if (!world[i][j].equals(Tileset.NOTHING)
                        && !world[i][j].equals(Tileset.WALL)) {
                    graph.add(new Position(i, j));
                }
            }
        }
    }

    public void hunt() {
        AStarSolver solver = new AStarSolver(graph,
                p, prey.getPosition(), 100);
        List<Position> shortestPath = solver.getSolution();

        int x = shortestPath.get(1).getX(), y = shortestPath.get(1).getY();
        Position near = new Position(x, y);

        if (p.getX() - x == -1) {
            System.out.println("left");
            moveGuard(1, 0);
        } else if (p.getX() - x == 1) {
            System.out.println("right");
            moveGuard(-1, 0);
        } else if (p.getY() - y == -1) {
            System.out.println("down");
            moveGuard(0, 1);
        } else {
            System.out.println("up");
            moveGuard(0, -1);
        }
        if (world[p.getX()][p.getY()].equals(Tileset.AVATAR)) {
            Engine.isGameOver = true;
        }
        drawGuard();
        StdDraw.pause(100);
    }

    public void projPath(boolean proj) {
        AStarSolver solver = new AStarSolver(graph,
                p, prey.getPosition(), 100);
        List<Position> shortestPath = solver.getSolution();
        projectedPath.addAll(shortestPath);
        if (proj) {
            drawProjectedPath(projectedPath, Tileset.FLOOR);
            drawProjectedPath(shortestPath, Tileset.REDFLOOR);
        } else {
            drawProjectedPath(projectedPath, Tileset.FLOOR);
        }
    }

    public void drawProjectedPath(List<Position> shortestPath, TETile t) {
        for (Position s : shortestPath) {
            if (world[s.getX()][s.getY()].equals(Tileset.FLOOR)
                    || world[s.getX()][s.getY()].equals(Tileset.REDFLOOR)) {
                world[s.getX()][s.getY()] = t;
            }
        }
    }

    public void moveGuard(int dx, int dy) {
        int x = p.getX(), y = p.getY();
        if (isWall(x + dx, y + dy)) {
            return;
        }
        world[x][y] = Tileset.FLOOR;
        p.update(x + dx, y + dy);
        world[p.getX()][p.getY()] = Tileset.WATER;
    }

    public void drawGuard() {
        world[p.getX()][p.getY()] = Tileset.WATER;
    }
}
