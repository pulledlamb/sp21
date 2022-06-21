package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private int size;
    private double loadFactor;
    private int initialSize;
    private static final int MULFACTOR = 2;
    private Set<K> set = new HashSet<>();

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize < 1 || maxLoad <= 0.0) {
            throw new IllegalArgumentException("initial size must be greater than 1 or max load greater than 0.");
        }
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
        this.initialSize = initialSize;
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
         return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    @Override
    public void clear() {
        buckets = createTable(initialSize);
        set.clear();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return set.contains(key);
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null key.");
        } else if (containsKey(key)) {
            int h = hash(key.hashCode(), initialSize);
            for (Node n : buckets[h]) {
                if (key.equals(n.key)) {
                    return n.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int h = hash(key.hashCode(), initialSize);
        if (set.contains(key)) {
            for (Node n : buckets[h]) {
                if (key.equals(n.key)) {
                    n.value = value;
                }
            }
        } else {
             if (buckets[h] == null) {
                 buckets[h] = createBucket();
             }
             buckets[h].add(createNode(key, value));
             size += 1;
             set.add(key);
             if (size * 1.0 / initialSize >= loadFactor) {
                 resize(initialSize * MULFACTOR);
             }
        }
    }

    private void resize(int cap) {
        Collection<Node>[] newBuckets = createTable(cap);
        for (int i = 0; i < cap; i++) {
            newBuckets[i] = createBucket();
        }
        reHashing(cap, newBuckets);
        initialSize = cap;
        this.buckets = newBuckets;
    }

    private void reHashing(int initSize, Collection<Node>[] buckets) {
        for (K key : set) {
            V v = get(key);
            int h = hash(key.hashCode(), initSize);
            buckets[h].add(createNode(key, v));
        }
    }

    private int hash(int h, int denom) {
        return Math.floorMod(h, denom);
    }

    @Override
    public Set<K> keySet() {
        return set;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls remove() with null key");
        }
        if (set.contains(key)) {
            int h = hash(key.hashCode(), initialSize);
            V v = get(key);
            buckets[h].remove(createNode(key, v));
            size -= 1;
            set.remove(key);
            return v;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls remove() with null key.");
        }
        if (set.contains(key)) {
            int h = hash(key.hashCode(), initialSize);
            if (value.equals(get(key))) {
                buckets[h].remove(createNode(key, value));
                size -= 1;
                set.remove(key);
                return value;
            }
        }
        return null;
    }
}
