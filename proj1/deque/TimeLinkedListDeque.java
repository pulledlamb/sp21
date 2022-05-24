package deque;

import edu.princeton.cs.algs4.Stopwatch;

public class TimeLinkedListDeque {
    public static class TimeSLList {
        static final int m = 10000;
        private static void printTimingTable(LinkedListDeque<Integer> Ns, LinkedListDeque<Double> times, LinkedListDeque<Integer> opCounts) {
            System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
            System.out.printf("------------------------------------------------------------\n");
            for (int i = 0; i < Ns.size(); i += 1) {
                int N = Ns.get(i);
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

            LinkedListDeque<Integer> Ns = new LinkedListDeque<>();
            // size of data structure
            Ns.addLast(1000); Ns.addLast(2000); Ns.addLast(4000); Ns.addLast(8000); Ns.addLast(16000);
            Ns.addLast(32000); Ns.addLast(64000); Ns.addLast(128000);

            LinkedListDeque<Double> times = new LinkedListDeque<>();
            LinkedListDeque<Integer> opCount = new LinkedListDeque<>();
            for (int i = 0; i < Ns.size(); i += 1) {
                LinkedListDeque<Integer> add = new LinkedListDeque<>();
                opCount.addLast(m);
                for (int j = 0; j < Ns.get(i); j += 1) {
                    add.addLast(j);
                }
                Stopwatch sw = new Stopwatch();
                for (int k = 0; k < m; k += 1) {
                    add.removeLast();
                }
                double timeInSeconds = sw.elapsedTime();
                times.addLast(timeInSeconds);
            }



            printTimingTable(Ns, times, opCount);
        }

    }

}
