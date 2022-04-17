package com.almet.task2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.nextLine();
        BTree<LocalDate, Integer> tree = new BTree<>(32);
        for (int i = 0; i < n; i++) {
            String[] line = in.nextLine().split(" ");
            if (line[0].charAt(0) != 'R') {
                LocalDate date = LocalDate.parse(line[0]);
                String operation = line[1];
                int amount = Integer.parseInt(line[2]);
                if (operation.equals("WITHDRAW")) amount = -amount;
                if (!tree.contains(date)) {
                    tree.add(date, amount);
                }
                else {
                    tree.update(date, tree.lookup(date) + amount);
                }
            }
            else {
                LocalDate from = LocalDate.parse(line[2]);
                LocalDate to = LocalDate.parse(line[4]);
                //System.out.println(tree.lookupRange(from, to));
                System.out.println(tree.lookupRange(from, to).stream().mapToInt(value -> value).sum());
            }
        }

    }

    public static void checkAddMethod() {
        BTree<String, Integer> tree = new BTree<>(2);
        String[] strings = {"F", "S", "Q", "K", "C", "L", "H", "T", "V", "W", "M",
                "R", "N", "P", "A", "B", "X", "Y", "D", "Z", "E"};
        for (int i = 0; i < strings.length; i++) {
            tree.add(strings[i], i);
        }
    }

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

class Pair<T1, T2> {
    T1 first;
    T2 second;
    public Pair(T1 key, T2 value) {
        this.first = key;
        this.second = value;
    }
}

class Element<K extends Comparable<? super K>, V> implements Comparable<Element<K, V>> {
    public K key;
    public V value;

    public Element(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key.toString() + " " + value.toString();
    }

    @Override
    public int compareTo(Element<K, V> o) {
        return key.compareTo(o.key);
    }
}

class Node<K extends Comparable<? super K>, V> {
    public int n;
    public List<Element<K, V>> elements;
    public List<Node<K, V>> children;
    public boolean isLeaf;
    public Node<K, V> parent;

    public Node(int n, boolean isLeaf, Node<K, V> parent) {
        this.n = n;
        this.isLeaf = isLeaf;
        this.elements = new ArrayList<>(n);
        this.children = new ArrayList<>(n+1);
        this.parent = parent;
    }

    public Node(int n, List<Element<K, V>> elements, List<Node<K, V>> children, boolean isLeaf, Node<K, V> parent) {
        this.n = n;
        this.elements = elements;
        this.children = children;
        this.isLeaf = isLeaf;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}

interface RangeMap<K, V> {
    int size();

    boolean isEmpty();
    // insert new item into the map
    void add(K key, V value);
    // check if a key is present
    boolean contains(K key);
    // lookup a value by the key
    V lookup(K key);
    // lookup values for a range of keys
    List<V> lookupRange(K from, K to);
    // optional: remove an item from a map (+1% extra credit)
    //Object remove(K key);
}

class BTree<K extends Comparable<? super K>, V> implements RangeMap<K, V> {
    private final int t;
    Node<K, V> root;
    private int size;

    public BTree(int t) {
        this.t = t;
        this.root = new Node<>(0, new ArrayList<>(2 * t - 1), new ArrayList<>(2 * t), true, null);
        this.root.parent = this.root;
        this.size = 0;
    }


    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    private void splitChild(Node<K, V> x, Integer index) {
        Node<K, V> y = x.children.get(index);
        Node<K, V> z = new Node<>(t - 1, y.isLeaf, x);

        for (int i = 0; i < t - 1; i++) {
            z.elements.add(i, y.elements.get(i + t));
        }
        if (!y.isLeaf) {
            for (int i = 0; i < t; i++) {
                z.children.add(i, y.children.get(i+t));
                z.children.get(i).parent = z;
            }
            if (y.n >= t) {
                y.children.subList(t, y.n + 1).clear();
            }
        }

        x.children.add(index + 1, z);
        x.elements.add(index, y.elements.get(t - 1));
        if (y.n >= t) {
            y.elements.subList(t - 1, y.n).clear();
        }
        y.n = t - 1;
        x.n = x.n + 1;
    }

    private void addNonFull(Node<K, V> x, Element<K, V> element) {
        int i = x.n - 1;
        while (i >= 0 && element.key.compareTo(x.elements.get(i).key) < 0) {
            i--;
        }
        i++;
        if (x.isLeaf) {
            x.elements.add(i, element);
            x.n = x.n + 1;
            size++;
        } else {
            if (x.children.get(i).n == 2 * t - 1) {
                splitChild(x, i);
                if (element.key.compareTo(x.elements.get(i).key) > 0) {
                    i++;
                }
            }
            addNonFull(x.children.get(i), element);
        }
    }

    @Override
    public void add(K key, V value) {
        Element<K, V> addedElement = new Element<>(key, value);
        if (root.n == 2 * t - 1) {
            Node<K, V> node = root;
            root = new Node<>(0, false, null);
            node.parent = root;
            root.children.add(0, node);
            splitChild(root, 0);
            addNonFull(root, addedElement);
        } else {
            addNonFull(root, addedElement);
        }
        size++;
    }

    @Override
    public boolean contains(K key) {
        return lookup(key) != null;
    }

    private Pair<Node<K, V>, Integer> lookup(Node<K, V> x, K key) {
        int i = 0;
        while (i < x.n && key.compareTo(x.elements.get(i).key) > 0) {
            i++;
        }
        if (i < x.n && key.compareTo(x.elements.get(i).key) == 0) {
            return new Pair<>(x, i);
        } else if (x.isLeaf) {
            return null;
        } else {
            return lookup(x.children.get(i), key);
        }
    }

    @Override
    public V lookup(K key) {
        Pair<Node<K, V>, Integer> result = lookup(root, key);
        if (result == null) {
            return null;
        } else {
            return result.first.elements.get(result.second).value;
        }
    }

    public void update(K key, V value) {
        Pair<Node<K, V>, Integer> pair = lookup(root, key);
        if (pair != null) {
            pair.first.elements.get(pair.second).value = value;
        }
    }

    private void traverse(K from, K to, Node<K, V> current, List<V> result) {
        int currentIndex = 0;
        while (currentIndex < current.n) {
            if (!current.isLeaf && current.elements.get(currentIndex).key.compareTo(from) > 0) {
                traverse(from, to, current.children.get(currentIndex), result);
            }
            if (current.elements.get(currentIndex).key.compareTo(from) >= 0 && current.elements.get(currentIndex).key.compareTo(to) <= 0) {
                result.add(current.elements.get(currentIndex).value);
            }
            if (!current.isLeaf && currentIndex == current.n - 1 && current.elements.get(currentIndex).key.compareTo(to) < 0) {
                traverse(from, to, current.children.get(currentIndex + 1), result);
            }
            currentIndex++;
        }
    }



    @Override
    public List<V> lookupRange(K from, K to) {
        List<V> result = new ArrayList<>();
        traverse(from, to, root, result);
        return result;
    }
}
