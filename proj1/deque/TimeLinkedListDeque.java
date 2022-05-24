package deque;

import edu.princeton.cs.algs4.Stopwatch;

public class TimeLinkedListDeque {
    public static class TimeSLList {
        static final int M = 10000;
        private static void printTimingTable(LinkedListDeque<Integer> nS, LinkedListDeque<Double> times, LinkedListDeque<Integer> opCounts) {
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
            timeGetLast();
        }

        public static void timeGetLast() {
            // TODO: YOUR CODE HERE

            LinkedListDeque<Integer> nS = new LinkedListDeque<>();
            // size of data structure
            nS.addLast(1000); nS.addLast(2000); nS.addLast(4000); nS.addLast(8000); nS.addLast(16000);
            nS.addLast(32000); nS.addLast(64000); nS.addLast(128000);

            LinkedListDeque<Double> times = new LinkedListDeque<>();
            LinkedListDeque<Integer> opCount = new LinkedListDeque<>();
            for (int i = 0; i < nS.size(); i += 1) {
                LinkedListDeque<Integer> add = new LinkedListDeque<>();
                opCount.addLast(M);
                for (int j = 0; j < nS.get(i); j += 1) {
                    add.addLast(j);
                }
                Stopwatch sw = new Stopwatch();
                for (int k = 0; k < M; k += 1) {
                    add.removeLast();
                }
                double timeInSeconds = sw.elapsedTime();
                times.addLast(timeInSeconds);
            }



            printTimingTable(nS, times, opCount);
        }

    }

}
