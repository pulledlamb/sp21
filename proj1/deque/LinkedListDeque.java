package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    /** circular sentinel toplogy */
    private int size;
    private Node sentinel;

    private class Node {
        private T item;
        private Node next;
        private Node prev;

        Node(T t, Node n, Node p) {
            item = t;
            next = n;
            prev = p;
        }
    }

    /** empty constructor */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;

        size = 0;
    }

    /** overriding add methods, addFirst and addLast */
    @Override
    public void addFirst(T t) {
        Node n = new Node(t, sentinel.next, sentinel);
        sentinel.next.prev = n;
        sentinel.next = n;
        size += 1;
    }

    @Override
    public void addLast(T t) {
        Node n = new Node(t, sentinel, sentinel.prev);
        sentinel.prev.next = n;
        sentinel.prev = n;
        size += 1;
    }

    /** overriding remove methods, removeFirst and removeLast */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T first = sentinel.next.item;
        Node curr = sentinel.next.next;

        sentinel.next = curr;
        curr.prev = sentinel;
        size -= 1;
        return first;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T last = sentinel.prev.item;
        Node curr = sentinel.prev.prev;

        sentinel.prev = curr;
        curr.next = sentinel;
        size -= 1;
        return last;
    }

    /** overriding get method, iteratively */
    public T get(int index) {
        Node curr = sentinel;
        if (index < 0 || index >= size) {
            return null;
        }
        for (int i = 0; i <= index; i += 1) {
            curr = curr.next;
        }

        return curr.item;
    }

    /** get the ith item, recursively */
    public T getRecursive(int index) {
        return getRecursiveNode(index, sentinel.next);
    }
    private T getRecursiveNode(int index, Node n) {
        if (index == 0) {
            return n.item;
        }
        return getRecursiveNode(index - 1, n.next);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        System.out.print("{");
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println("}");
    }

    /** returns an iterator */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T>  {
        private int pos;

        LinkedListDequeIterator() {
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

/*    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < size - 1; i += 1) {
            sb.append(this.get(i).toString());
            sb.append(" ");
        }
        sb.append(this.get(size - 1));
        sb.append("}");

        return sb.toString();
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
}
