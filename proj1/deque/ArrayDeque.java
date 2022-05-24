package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private static final double USAGEFACTOR = 0.25, EPS = 0.000001;
    private static final int RESIZEFACTOR = 2;
    private static final int MAX = 16;
    private T[] items;
    private int size, nextFirst, nextLast;

    /** empty constructor */
    public ArrayDeque() {
        items = (T[]) new Object[100];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /** overriding add methods, addFirst and addLast */
    @Override
    public void addFirst(T t) {
        if (nextFirst == nextLast) {
            resize(items.length * RESIZEFACTOR);
        }
        items[nextFirst] = t;
        size += 1;
        updateNext(0, -1);
    }

    @Override
    public void addLast(T t) {
        if (nextFirst == nextLast) {
            resize(items.length * RESIZEFACTOR);
        }
        items[nextLast] = t;
        size += 1;
        updateNext(1, 1);
    }

    /** overriding remove methods, removeLast and removeFirst */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T t = getLast();

        items[nextLast] = null;
        updateNext(1, -1);

        size -= 1;
        double ratio = Math.abs(size() / items.length * 1.0 - USAGEFACTOR);
        if (ratio < EPS && items.length >= MAX) {
            resize(items.length / RESIZEFACTOR);
        }
        return t;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T t = getFirst();

        items[nextFirst] = null;
        updateNext(0, 1);

        size -= 1;
        double ratio = Math.abs(size() / items.length * 1.0 - USAGEFACTOR);
        if (ratio < EPS && items.length >= MAX) {
            resize(items.length / RESIZEFACTOR);
        }
        return t;
    }

    // some helper methods
    /** returns the first item of the list */
    private T getFirst() {
        return items[mod(nextFirst + 1, items.length)];
    }
    /** returns the last item of the list */
    private T getLast() {
        return items[mod(nextLast - 1, items.length)];
    }

    private void updateNext(int k, int i) {
        if (k == 0) {
            nextFirst = mod(nextFirst + i, items.length);
        } else if (k == 1) {
            nextLast = mod(nextLast + i, items.length);
        }
    }

    private static int mod(int v, int m) {
        return ((v % m + m) % m);
    }

    @Override
    public int size() {
        return size;
    }

    /** Gets the ith item in the list (0 is the front). */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) { return null; }
        return items[mod(nextFirst + index + 1, items.length)];
    }

    @Override
    public void printDeque(){
        System.out.print("{");
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println("}");
    }

/*
    @Override
    public String toString() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        for (T x : this) {
            ad.addLast(x.toString());
        }
        return "{" + String.join(" ", ad) + "}";
*/
/*        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < size - 1; i += 1) {
            sb.append(this.get(i).toString());
            sb.append(" ");
        }
        sb.append(this.get(size - 1));
        sb.append("}");

        return sb.toString();*//*

    }
*/

/*    public static <S> ArrayDeque<S> of(S... s) {
        ArrayDeque<S> res = new ArrayDeque<>();
        for (S x : s) {
            res.addLast(x);
        }
        return res;
    }*/

    /** overriding equals */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }

        Deque<T> o = (Deque) other;
        if (o.size() != size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!(o.get(i).equals(this.get(i)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private void resize(int capacity) {
        T[] rez = (T[]) new Object[capacity];
        if (nextFirst >= nextLast) {
            int n = items.length - (1 + nextFirst);
            int start = mod(nextFirst + 1, items.length);

            System.arraycopy(items, start, rez, 1, n);
            System.arraycopy(items, 0, rez, n + 1, size() - n);
        } else {
            System.out.print(nextFirst + " " + nextLast + " ");
            System.out.print(size + " " + items.length);
            int start = mod(nextFirst + 1, items.length);
            System.arraycopy(items,  start, rez, 1, size());
        }

        nextFirst = 0;
        nextLast = size() + 1;
        items = rez;
    }


    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T t = get(pos);
            pos += 1;
            return t;
        }
    }
}
