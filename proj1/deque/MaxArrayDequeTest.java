package deque;

import jh61b.junit.In;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer i1, Integer i2) {
            return i1 - i2;
        }
    }
    @Test
    public void maxIntTest() {
        IntComparator intc = new IntComparator();
        MaxArrayDeque<Integer> maxd = new MaxArrayDeque<>(intc);

        for (int i = 0; i < 4; i += 1) {
            maxd.addFirst(i);
        }

        assertEquals(3, (long) maxd.max(intc));
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }
    @Test
    public void maxStringTest() {
        StringComparator strc = new StringComparator();
        MaxArrayDeque<String> maxd = new MaxArrayDeque<>(strc);

        maxd.addFirst("Alice");
        maxd.addFirst("Joey");
        maxd.addFirst("Joseph");

        assertEquals("Joseph", maxd.max(strc));
    }

}
