package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    int size;
    BSTNode root;
    private boolean nullVal = false;

    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }

    public BSTMap() {
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls containsKey() with a null key.");
        }
        V tmpVal = get(key);
        boolean tmpBoo = nullVal;
        nullVal = false;
        return (tmpVal != null || tmpBoo);
    }


    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key.");
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode root, K key, V value) {
        if (root == null) {
            size += 1;
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = put(root.left, key, value);
        } else if (cmp > 0) {
            root.right = put(root.right, key, value);
        } else {
            root.value = value;
        }
        return root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        } else {
            return get(root, key);
        }
    }

    private V get(BSTNode T, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key.");
        }
        if (T == null) {
            return null;
        }
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            return get(T.left, key);
        } else if (cmp > 0) {
            return get(T.right, key);
        } else {
            if (T.value == null) {
                nullVal = true;
            }
            return T.value;
        }
    }

    public void printInOrder() {
        inOrder(root);
    }

    private void inOrder(BSTNode T) {
        if (T == null) {
            return;
        }
        inOrder(T.left);
        System.out.println(T.value);
        inOrder(T.right);
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }
        V val = get(key);
        if (val.equals(value)) {
            return remove(key);
        } else {
            return null;
        }
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls remove() with a null key.");
        }
        V val = get(key);
        root = remove(root, key);
        size -= 1;

        return val;
    }

    private BSTNode remove(BSTNode root, K key) {
        if (root == null) {
            return null;
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = remove(root.left, key);
        } else if (cmp > 0) {
            root.right = remove(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            BSTNode t = root;
            root = min(t.right);
            root.right = removeMin(t.right);
            root.left = t.left;
        }

        return root;
    }

    private BSTNode removeMin(BSTNode root) {
        if (root.left == null) {
            return root.right;
        }
        root.left = removeMin(root.left);
        return root;
    }


    private BSTNode min(BSTNode root) {
        if (root.left == null) {
            return root;
        }
        return min(root.left);
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<>(size);
        return keySet(root, keys);
    }

    private Set<K> keySet(BSTNode root, HashSet<K> keys) {
        if (root == null) {
            return keys;
        }
        keySet(root.left, keys);
        keys.add(root.key);
        keySet(root.right, keys);

        return keys;
    }


    @Override
    public Iterator<K> iterator() {
        Set<K> ks = keySet();
        return ks.iterator();
    }

}

