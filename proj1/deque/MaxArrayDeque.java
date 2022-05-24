package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comp;

    /** constructor */
    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }

    public T max() {
        if (size() == 0) { return null; }
        int maxDex = 0;
        for (int i = 0; i < size(); i += 1) {
            if (comp.compare(get(i), get(maxDex)) > 0) {
                maxDex = i;
            }
        }
        return get(maxDex);
    }

    public T max(Comparator<T> c) {
        comp = c;
        return max();
    }
}
