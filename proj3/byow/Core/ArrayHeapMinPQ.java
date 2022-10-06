package byow.Core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private int size;
    private Node[] pq;
    private static final int INITCAPACITY = 16;
    private HashMap<T, Integer> itemPos;
    private HashSet<T> items;

    private static final double LOADFACTOR = 0.75;

    /* Constructor */
    public ArrayHeapMinPQ(int cap) {
        size = 0;
        pq = new ArrayHeapMinPQ.Node[cap];
        pq[0] = null;
        items = new HashSet<>();
        itemPos = new HashMap<>();
    }

    public ArrayHeapMinPQ() {
        this(INITCAPACITY);
    }

    @Override
    public int size() {
        return size;
    }

    private void resize(int capacity) {
        Node[] temp = new ArrayHeapMinPQ.Node[capacity];
        for (int i = 0; i <= size; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    @Override
    public boolean contains(T item) {
        return items.contains(item);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T getSmallest() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return pq[1].item();
    }

    @Override
    public T removeSmallest() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }

        Node min = pq[1];
        swap(1, size--);
        sink(1);
        pq[size + 1] = null;
        if ((size > 0) && (size * 1.0 / pq.length < 1 - LOADFACTOR)) {
            resize(pq.length / 2);
        }
        // remove from items hashSet
        items.remove(min.item());
        return min.item();
    }

    @Override
    public void add(T item, double priority) {
        if (items.contains(item)) {
            throw new IllegalArgumentException("Item already exists!");
        }

        if (size == pq.length - 1) {
            resize(2 * pq.length);
        }
        //if (size / pq.length < 1 - loadFactor) resize(pq.length / 2);

        pq[++size] = new Node(item, priority);
        itemPos.put(item, size);
        swim(size);
        items.add(item);

    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new IllegalArgumentException("Item does not exist!");
        }

        int pos = itemPos.get(item);
        double oldPri = pq[pos].priority();

        pq[pos] = new Node(item, priority);
        if (oldPri > priority) {
            swim(pos);
        } else {
            sink(pos);
        }
    }

    /*
       Methods to print heaps.
       @source: PrintHeapDemo.java by Josh Hug
     */
    void printSimpleHeapDrawing() {
        int depth = ((int) (Math.log(pq.length) / Math.log(2)));
        int level = 0;
        int itemsUntilNext = (int) Math.pow(2, level);
        for (int j = 0; j < depth; j++) {
            System.out.print(" ");
        }

        for (int i = 1; i < pq.length; i++) {
            System.out.printf("%d ", pq[i].item(), " ", pq[i].priority());
            if (i == itemsUntilNext) {
                System.out.println();
                level++;
                itemsUntilNext += Math.pow(2, level);
                depth--;
                for (int j = 0; j < depth; j++) {
                    System.out.print(" ");
                }
            }
        }
        System.out.println();
    }

    /** Prints out a drawing of the given array of Objects assuming it
     *  is a heap starting at index 1. You're welcome to copy and paste
     *  code from this method into your code, just make sure to cite
     *  this with the @source tag. */
    void printFancyHeapDrawing() {
        String drawing = fancyHeapDrawingHelper(1, "");
        System.out.println(drawing);
    }

    /* Recursive helper method for toString. */
    private String fancyHeapDrawingHelper(int index, String soFar) {
        if (index >= pq.length || pq[index] == null) {
            return "";
        } else {
            String toReturn = "";
            int rightIndex = 2 * index + 1;
            toReturn += fancyHeapDrawingHelper(rightIndex, "        " + soFar);
            if (rightIndex < pq.length && pq[rightIndex] != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + pq[index].item() + " " + pq[index].priority() + "\n";
            int leftIndex = 2 * index;
            if (leftIndex < pq.length && pq[leftIndex] != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += fancyHeapDrawingHelper(leftIndex, "        " + soFar);
            return toReturn;
        }
    }

    /* helper methods to restore the heap invariant. */
    // swim up small out of place items;
    private void swim(int k) {
        while (k > 1 && pq[k / 2].priority() > pq[k].priority()) {
            swap(k, k / 2);
            k = k / 2;
        }
    }

    // sink down large out of place items;
    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && pq[j + 1].priority() < pq[j].priority()) {
                j++;
            }
            if (pq[k].priority() < pq[j].priority()) {
                break;
            }
            swap(k, j);
            k = j;
        }
    }


    /* methods to swap */
    private void swap(int i, int j) {
        Node temp = pq[i];
        itemPos.put(temp.item, j);
        itemPos.put(pq[j].item, i);
        pq[i] = pq[j];
        pq[j] = temp;
    }

    private class Node {
        private T item;
        private double priority;

        Node(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        T item() {
            return item;
        }

        double priority() {
            return priority;
        }

        public boolean equals(Node other) {
            double epsilon = 1e-6;
            return this.item.equals(other) && Math.abs(this.priority - other.priority) < epsilon;
        }
    }

}
