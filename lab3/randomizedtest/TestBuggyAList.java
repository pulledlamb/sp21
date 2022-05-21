package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        int n = 3;
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bAl = new BuggyAList<>();
        al.addLast(4); bAl.addLast(4);
        al.addLast(3); bAl.addLast(3);
        al.addLast(109); bAl.addLast(109);

        assertEquals(al.size(), bAl.size());
        assertEquals(al.removeLast(), bAl.removeLast());
        assertEquals(al.removeLast(), bAl.removeLast());
        assertEquals(al.removeLast(), bAl.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                //System.out.println("size: " + size);
                assertEquals(L.size(), B.size());
            } else if (L.size() > 0) {
                // getLast
                assertEquals(L.removeLast(), B.removeLast());
            }
        }
    }
}
