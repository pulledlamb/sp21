package deque;
import edu.princeton.cs.algs4.Stopwatch;

public class TimeArrayDeque {
    public static class TimeAList {
        private static void printTimingTable(ArrayDeque<Integer> nS, ArrayDeque<Double> times, ArrayDeque<Integer> opCounts) {
            System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
            System.out.printf("------------------------------------------------------------\n");
            for (int i = 0; i < nS.size(); i += 1) {
                int N = nS.get(i);
                double time = times.get(i);
                int opCount = opCounts.get(i);
                double timePerOp = time / opCount * 1e6;
                System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
            }
        }

        public static void main(String[] args) {
            timeAListConstruction();
        }

        public static void timeAListConstruction() {
            // TODO: YOUR CODE HERE
            ArrayDeque<Integer> nS = new ArrayDeque<>();
            // size of data structure
            nS.addLast(1000); nS.addLast(2000); nS.addLast(4000); nS.addLast(8000); nS.addLast(16000);
            nS.addLast(32000); nS.addLast(64000); nS.addLast(128000);

            ArrayDeque<Double> times = new ArrayDeque<>();
            ArrayDeque<Integer> opCount = new ArrayDeque<>();
            for (int i = 0; i < nS.size(); i += 1) {
                ArrayDeque<Integer> add = new ArrayDeque<>();
                opCount.addLast(nS.get(i));
                Stopwatch sw = new Stopwatch();
                for (int j = 0; j < nS.get(i); j += 1) {
                    add.addLast(j);
                }
                double timeInSeconds = sw.elapsedTime();
                times.addLast(timeInSeconds);
            }
            printTimingTable(nS, times, opCount);
        }
    }

}
