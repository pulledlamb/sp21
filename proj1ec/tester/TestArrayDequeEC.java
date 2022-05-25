package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    private static final int M = 5;
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> L = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> B = new ArrayDequeSolution<>();
        ArrayDequeSolution<String> msg = new ArrayDequeSolution<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                if (msg.size() > M) {
                    msg.removeFirst();
                }
                msg.addLast("addLast(" + randVal + ")\n");
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                //System.out.println("size: " + size);
                if (msg.size() > M) {
                    msg.removeFirst();
                }
                msg.addLast("size()\n");

                assertEquals(msg.toString(), L.size(), B.size());
            } else if (L.size() > 0) {
                // getLast
                if (msg.size() > M) {
                    msg.removeFirst();
                }
                msg.addLast("removeLast()\n");

                assertEquals(msg.toString(), L.removeLast(), B.removeLast());
            }
        }
    }

}
