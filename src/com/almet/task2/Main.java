package com.almet.task2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class with main logic
 * </br>
 * You can find the links to Codeforces submissions in CodeforcesSubmissions.txt file
 * (com.almet.CodeforcesSubmissions.txt)
 *
 * @author Kamil Almetov BS21-05
 */
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.nextLine();
        BTree<LocalDate, Integer> tree = new BTree<>(2);
        for (int i = 0; i < n; i++) {
            String[] line = in.nextLine().split(" ");
            // DEPOSIT or WITHDRAW
            if (line[0].charAt(0) != 'R') {
                LocalDate date = LocalDate.parse(line[0]);
                String operation = line[1];
                int amount = Integer.parseInt(line[2]);
                if (operation.equals("WITHDRAW")) amount = -amount;
                if (!tree.contains(date)) {
                    tree.add(date, amount);
                } else {
                    tree.update(date, tree.lookup(date) + amount);
                }
            }
            // REPORT
            else {
                LocalDate from = LocalDate.parse(line[2]);
                LocalDate to = LocalDate.parse(line[4]);
                List<Integer> res = tree.lookupRange(from, to);
                int ans = 0;
                for (int j = 0; j < res.size(); j++) {
                    ans += res.get(j);
                }
                System.out.println(ans);
            }
        }

    }

    // method for testing add method
    public static void checkAddMethod() {
        BTree<String, Integer> tree = new BTree<>(2);
        String[] strings = {"F", "S", "Q", "K", "C", "L", "H", "T", "V", "W", "M",
                "R", "N", "P", "A", "B", "X", "Y", "D", "Z", "E"};
        for (int i = 0; i < strings.length; i++) {
            tree.add(strings[i], i);
        }
    }

    // method for testing lookup method
    public static void checkLookup() {
        BTree<String, Integer> tree = new BTree<>(2);
        String[] strings = {"F", "S", "Q", "K", "C", "L", "H", "T", "V", "W", "M",
                "R", "N", "P", "A", "B", "X", "Y", "D", "Z", "E"};
        for (int i = 0; i < strings.length; i++) {
            tree.add(strings[i], i);
        }
        for (String string : strings) {
            System.out.println(tree.lookup(string));
        }
    }

    // method for testing lookupRange method
    public static void checkLookupRange() {
        BTree<String, Integer> tree = new BTree<>(2);
        String[] strings = {"F", "S", "Q", "K", "C", "L", "H", "T", "V", "W", "M",
                "R", "N", "P", "A", "B", "X", "Y", "D", "Z", "E"};
        for (int i = 0; i < strings.length; i++) {
            tree.add(strings[i], i);
        }
        List<Integer> res = tree.lookupRange("A", "M");
        for (int i = 0; i < strings.length; i++) {
            System.out.print(strings[i] + " " + i + " ");
        }
        System.out.println();
        for (int i = 0; i < res.size(); i++) {
            System.out.print(strings[res.get(i)] + " ");
        }
    }
}

/**
 * Class pair, that has 2 members
 *
 * @param <T1> type of the first member
 * @param <T2> type of the second member
 */
class Pair<T1, T2> {
    T1 first;
    T2 second;

    /**
     * Constructor
     */
    public Pair(T1 key, T2 value) {
        this.first = key;
        this.second = value;
    }
}

/**
 * Class that represents the element of the node in the tree
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
class Element<K extends Comparable<? super K>, V> implements Comparable<Element<K, V>> {
    /**
     * key of element
     */
    public K key;
    /**
     * value of element
     */
    public V value;

    /**
     * Constructor
     */
    public Element(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * String representation of the element
     */
    @Override
    public String toString() {
        return key.toString() + " " + value.toString();
    }

    /**
     * method that compare two elements
     *
     * @param o the object to be compared.
     * @return integer < 0 if current element less than o, > 0 if current is greater than o, == 0 if they're equal
     */
    @Override
    public int compareTo(Element<K, V> o) {
        return key.compareTo(o.key);
    }
}

/**
 * Class that represents node of the tree
 * It contains several elements and references to its children
 *
 * @param <K> type of key of the element
 * @param <V> type of value of element
 */
class Node<K extends Comparable<? super K>, V> {
    /**
     * number of elements in the node
     */
    public int n;
    /**
     * list of elements in the node
     */
    public List<Element<K, V>> elements;
    /**
     * list of references to the children
     */
    public List<Node<K, V>> children;
    /**
     * flag that is true if the current node is a leaf, false otherwise
     */
    public boolean isLeaf;
    /**
     * reference to the node's parent
     */
    public Node<K, V> parent;

    /**
     * Constructors
     */
    public Node(int n, boolean isLeaf, Node<K, V> parent) {
        this.n = n;
        this.isLeaf = isLeaf;
        this.elements = new ArrayList<>(n);
        this.children = new ArrayList<>(n + 1);
        this.parent = parent;
    }

    public Node(int n, List<Element<K, V>> elements, List<Node<K, V>> children, boolean isLeaf, Node<K, V> parent) {
        this.n = n;
        this.elements = elements;
        this.children = children;
        this.isLeaf = isLeaf;
        this.parent = parent;
    }

    /**
     * String representation of the node
     */
    @Override
    public String toString() {
        return elements.toString();
    }
}

/**
 * Interface that represents RangeMap. It's a data structure that contains ordered key-value pairs that are sorted.
 * It supports the following operations:
 * (see the description of methods below)
 *
 * @param <K> type of key
 * @param <V> type of value
 */
interface RangeMap<K, V> {
    /**
     * @return number of elements in the map
     */
    int size();

    /**
     * @return true if the map is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Method that inserts new item into the map
     *
     * @param key   key of the inserting element
     * @param value value of the inserting element
     */
    void add(K key, V value);

    /**
     * Method that checks if a key is present
     *
     * @param key key that will be checked
     * @return true if the key is presented, false otherwise
     */
    boolean contains(K key);

    /**
     * Method that lookups a value by the key
     *
     * @param key key of the looking element
     * @return value of the element
     */
    V lookup(K key);

    /**
     * Method that lookups values for a range of keys
     *
     * @param from starting key
     * @param to   ending key
     * @return list of appropriate values
     */
    List<V> lookupRange(K from, K to);

    // optional: remove an item from a map (+1% extra credit)
    //Object remove(K key);
}

/**
 * Class that represents the BTree.
 *
 * @param <K> type of key
 * @param <V> type of value
 */
class BTree<K extends Comparable<? super K>, V> implements RangeMap<K, V> {
    /**
     * Special constant for BTree.
     * In the tree each node must contain no more than 2*t-1 key,
     * and each node except root must contain at least t-1 key (root can contain 1 or more keys)
     */
    private final int t;
    /**
     * Root of the tree
     */
    Node<K, V> root;
    /**
     * Number of elements in the tree
     */
    private int size;

    /**
     * Constructor
     */
    public BTree(int t) {
        this.t = t;
        this.root = new Node<>(0, new ArrayList<>(2 * t - 1), new ArrayList<>(2 * t), true, null);
        this.root.parent = this.root;
        this.size = 0;
    }

    /**
     * @return size of the tree
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * @return true if the tree is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Method that splits full child of x into 2 children, moving middle element of the child to the x
     *
     * @param x     non-full inner node, which full child will be split
     * @param index index of the child in the children list of x
     */
    private void splitChild(Node<K, V> x, Integer index) {
        // y - the child that will be split (the first half will remain in y)
        Node<K, V> y = x.children.get(index);
        // z - second half of the child
        Node<K, V> z = new Node<>(t - 1, y.isLeaf, x);
        // add second half of elements of y to z
        for (int i = 0; i < t - 1; i++) {
            z.elements.add(i, y.elements.get(i + t));
        }
        // if y has children, move second part of them to z, also z becomes a new parent for all of them
        if (!y.isLeaf) {
            for (int i = 0; i < t; i++) {
                z.children.add(i, y.children.get(i + t));
                z.children.get(i).parent = z;
            }
            // remove from y all moved children
            y.children.subList(t, y.n + 1).clear();

        }
        // z becomes the child of x, right after y (index + 1)
        x.children.add(index + 1, z);
        // the middle element of y goes to x
        x.elements.add(index, y.elements.get(t - 1));
        // remove from y all moved elements
        y.elements.subList(t - 1, y.n).clear();
        // update the sizes of y and x
        y.n = t - 1;
        x.n = x.n + 1;
    }

    /**
     * Method that adds 'element' to node 'x'. It's guarantied that 'x' is non-full when this method is called
     *
     * @param x       node where an element will be added
     * @param element element that will be added
     */
    private void addNonFull(Node<K, V> x, Element<K, V> element) {
        // find the exact place (index) in the node where to insert 'element'
        int i = x.n - 1;
        while (i >= 0 && element.key.compareTo(x.elements.get(i).key) < 0) {
            i--;
        }
        i++;
        // if node is leaf, just add the element and increase the size
        if (x.isLeaf) {
            x.elements.add(i, element);
            x.n = x.n + 1;
            size++;
        }
        // otherwise, we add element to child number i.
        // Split the child number i if it's full. That's necessary because the child must be non-full.
        else {
            if (x.children.get(i).n == 2 * t - 1) {
                splitChild(x, i);
                // there is one more child so we need to decide where to put the element (to i or i+1 child)
                if (element.key.compareTo(x.elements.get(i).key) > 0) {
                    i++;
                }
            }
            // add the element to the child
            addNonFull(x.children.get(i), element);
        }
    }

    /**
     * Method that adds element with 'key' and 'value' to the tree
     *
     * @param key   key of the inserting element
     * @param value value of the inserting element
     */
    @Override
    public void add(K key, V value) {
        // adding element
        Element<K, V> addedElement = new Element<>(key, value);
        // if root is full, split it
        if (root.n == 2 * t - 1) {
            Node<K, V> node = root;
            // new root now is the node with the middle element of the previous root
            root = new Node<>(0, false, null);
            // previous root is now new root's child
            node.parent = root;
            root.children.add(0, node);
            splitChild(root, 0);
            // now we ensure that the root is non-full, so run the addNonFull() method
            addNonFull(root, addedElement);
        } else {
            // the root is non-full, so run the addNonFull() method
            addNonFull(root, addedElement);
        }
    }

    /**
     * Method that checks whether the key is in the tree
     *
     * @param key key that will be checked
     * @return true if the key is presented in the tree
     */
    @Override
    public boolean contains(K key) {
        return lookup(key) != null;
    }

    /**
     * Private method for inner needs. It performs lookup of the element, returning the reference to the node
     * and the index in it where the element is localed
     *
     * @param x   node reference where we're looking for the element
     * @param key the key of looking element
     * @return the pair contains reference to node where the element is located and element's index within it.
     */
    private Pair<Node<K, V>, Integer> lookup(Node<K, V> x, K key) {
        // while key < current element key, increase the index i
        int i = 0;
        while (i < x.n && key.compareTo(x.elements.get(i).key) > 0) {
            i++;
        }
        // if we found the element with the same key, return it
        if (i < x.n && key.compareTo(x.elements.get(i).key) == 0) {
            return new Pair<>(x, i);
        }
        // else it is leaf, there is no element with the key in the tree
        else if (x.isLeaf) {
            return null;
        }
        // if key is less than current element key, look for the key in the left subtree of current element
        else {
            return lookup(x.children.get(i), key);
        }
    }

    /**
     * Method that finds the value by its key in the tree
     *
     * @param key key of the looking element
     * @return the value of the found element
     */
    @Override
    public V lookup(K key) {
        // perform the lookup using private method written above
        Pair<Node<K, V>, Integer> result = lookup(root, key);
        // return null if we found nothing
        if (result == null) {
            return null;
        }
        // otherwise return the element's value
        else {
            return result.first.elements.get(result.second).value;
        }
    }

    /**
     * Method that updates the value of the element with the key 'key'
     *
     * @param key   key of the element
     * @param value new value of the element
     */
    public void update(K key, V value) {
        Pair<Node<K, V>, Integer> pair = lookup(root, key);
        if (pair != null) {
            pair.first.elements.get(pair.second).value = value;
        }
    }

    /**
     * Method that finds all elements whose keys are between 'from' and 'to' keys
     * using in-order traversal
     *
     * @param from    left boundary of looking segment
     * @param to      right boundary of looking segment
     * @param current current node
     * @param result  list of values of elements whose keys are between 'from' and 'to' keys
     */
    private void traverse(K from, K to, Node<K, V> current, List<V> result) {
        // start from the first element of the current node
        int currentIndex = 0;
        // while we haven't looked at all elements in the current node
        while (currentIndex < current.n) {
            // if current isn't leaf and the current element is bigger than 'from' key
            // go to the left subtree
            // otherwise there are no elements that we're interested in the left subtree
            if (!current.isLeaf && current.elements.get(currentIndex).key.compareTo(from) > 0) {
                traverse(from, to, current.children.get(currentIndex), result);
            }
            // add current element if it's within the boundaries
            if (current.elements.get(currentIndex).key.compareTo(from) >= 0 && current.elements.get(currentIndex).key.compareTo(to) <= 0) {
                result.add(current.elements.get(currentIndex).value);
            }
            // since we have n + 1 children, we need to check the last child separately
            if (!current.isLeaf && currentIndex == current.n - 1 && current.elements.get(currentIndex).key.compareTo(to) < 0) {
                traverse(from, to, current.children.get(currentIndex + 1), result);
            }
            currentIndex++;
        }
    }


    /**
     * Method that finds all elements within the [from, to] segment
     *
     * @param from starting key
     * @param to   ending key
     * @return list of found elements
     */
    @Override
    public List<V> lookupRange(K from, K to) {
        List<V> result = new ArrayList<>();
        traverse(from, to, root, result);
        return result;
    }
}
