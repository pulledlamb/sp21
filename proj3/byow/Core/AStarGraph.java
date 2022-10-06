package byow.Core;

import java.util.ArrayList;
import java.util.LinkedList;

public class AStarGraph {
    ArrayList<Position> vertices;


    public AStarGraph() {
        vertices = new ArrayList<>();
    }

    public void add(Position v) {
        vertices.add(v);
    }

    public LinkedList<WeightedEdge<Position>> neighbors(Position v) {
        LinkedList<WeightedEdge<Position>> list = new LinkedList<>();

        int x = v.getX(), y = v.getY();

        if (checkValidPath(x, y, 0, 1))  {
            list.add(new WeightedEdge<>(v, new Position(x, y + 1), 1));
        }
        if (checkValidPath(x, y, 0, -1)) {
            list.add(new WeightedEdge<>(v, new Position(x, y - 1), 1));
        }
        if (checkValidPath(x, y, -1, 0)) {
            list.add(new WeightedEdge<>(v, new Position(x - 1, y), 1));
        }
        if (checkValidPath(x, y, 1, 0)) {
            list.add(new WeightedEdge<>(v, new Position(x + 1, y), 1));
        }

        return list;
    }

    private boolean checkValidPath(int x, int y, int dx, int dy) {
        return vertices.contains(new Position(x + dx, y + dy));
    }

    public double estimatedDistanceToGoal(Position source, Position goal) {
        double x = Math.pow(source.getX() - goal.getX(), 2),
                y = Math.pow(source.getY() - goal.getY(), 2);
        return x + y;
    }
}
