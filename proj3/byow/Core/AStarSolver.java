package byow.Core;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AStarSolver {
    public ArrayHeapMinPQ<Position> fringe;

    public SolverOutcome result;
    public LinkedList<Position> solution;
    public int numStates = 0;
    public double timeSpent, solutionWeight;
    public HashMap<Position, Position> edgeTo;
    public HashMap<Position, Double> distTo;

    public AStarSolver(AStarGraph input, Position start,
                       Position end, double timeout) {

        Stopwatch sw = new Stopwatch();

        fringe = new ArrayHeapMinPQ<>();
        distTo = new HashMap<>(); edgeTo = new HashMap<>();
        solution = new LinkedList<>();

        fringe.add(start, 0);

        numStates += 1;

        while (!fringe.isEmpty() || fringe.getSmallest().equals(end)
                ||sw.elapsedTime() > timeout) {
            Position source = fringe.removeSmallest();
            numStates += 1;

            List<WeightedEdge<Position>> neighborEdge = input.neighbors(source);

            for (WeightedEdge<Position> e : neighborEdge) {
                relax(e, input, end, start);
            }
            if (source.equals(end)) {

                solutionWeight = distTo.get(source);
                result = SolverOutcome.SOLVED;

                // add Position to solution
                Position temp = end;
                solution.addFirst(temp);
                while (!temp.equals(start)) {
                    solution.addFirst(edgeTo.get(temp));
                    temp = edgeTo.get(temp);
                }
                timeSpent = sw.elapsedTime();
                return;
            }
            if (sw.elapsedTime() > timeout) {
                result = SolverOutcome.TIMEOUT;
                solutionWeight = 0;
                solution.clear();
                return;
            }
        }
        solution.clear();
        solutionWeight = 0;
        result = SolverOutcome.UNSOLVABLE;
        timeSpent = sw.elapsedTime();
    }

    private void relax(WeightedEdge<Position> e, AStarGraph input, Position goal, Position start) {
        Position p = e.from(), q = e.to();
        double w = e.weight();

        if (p.equals(start)) {
            distTo.put(p, 0.0);
            distTo.put(q, w);
        }
        if (!distTo.containsKey(p)) {
            distTo.put(p, distTo.get(p));
        }
        if (!distTo.containsKey(q)) {
            distTo.put(q, distTo.get(p) + w);
        }
        if (distTo.get(p) + w <= distTo.get(q)) {
            distTo.put(q, distTo.get(p) + w);
            if (fringe.contains(q)) {
                fringe.changePriority(q, distTo.get(q) + input.estimatedDistanceToGoal(q, goal));
                edgeTo.put(q, p);
            } else {
                fringe.add(q, distTo.get(q) + input.estimatedDistanceToGoal(q, goal));
                edgeTo.put(q, p);
                numStates += 1;
            }
        }
    }

    public SolverOutcome outcome() {
        return result;
    }

    public LinkedList<Position> getSolution() {
        return solution;
    }

    public double getSolutionWeight() {
        return solutionWeight;
    }

    public int getNumStates() {
        return numStates;
    }

    public double getTimeSpent() {
        return timeSpent;
    }
}
