package com.almet.task3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        IPriorityQueue<Integer, String> queue = new FibonacciMinHeap<>();
        int n = in.nextInt();
        in.nextLine();
        for (int i = 0; i < n; i++) {
            String[] line = in.nextLine().split(" ");
            if (line[0].equals("ADD")) {
                String value = line[1];
                int key = Integer.parseInt(line[2]);
                queue.insert(new Node<>(key, value));
            } else {
                System.out.println(queue.extractMin());
            }
        }
    }
}

class ListNode<E> {
    E value;
    ListNode<E> left, right;

    public ListNode(E value) {
        this.value = value;
        left = right = this;
    }
}

class CircularLinkedList<E> {
    ListNode<E> first;
    ListNode<E> current;
    int size;

    public CircularLinkedList() {
        first = null;
        current = null;
        size = 0;
    }

    public void addAfter(ListNode<E> place, ListNode<E> el) {
        el.left = place.left;
        el.right = place;
        place.left.right = el;
        place.left = el;
    }

    public void add(E el) {
        ListNode<E> listEl = new ListNode<>(el);
        if (first == null) {
            first = listEl;
            current = first;
        } else {
            addAfter(first, listEl);
        }
    }

    public void remove(ListNode<E> el) {
        el.left.right = el.right;
        el.right.left = el.left;
        el.right = el.left = el;
    }

    public void combine(CircularLinkedList<E> list) {
        first.right.left = list.first.left;
        list.first.left.right = first.right;
        first.right = list.first;
        list.first.left = first;
    }

    public boolean hasNext() {
        return current.right != first;
    }

    public E next() {
        return current.value;
    }
}

interface IPriorityQueue<K, V> {
    void insert(Object item);

    Object findMin();

    Object extractMin();

    void decreaseKey(Object item, K newKey);

    void delete(Object item);

    void union(Object anotherQueue);
}

class Node<K extends Comparable<K>, V> implements Comparable<Node<K, V>> {
    K key;
    V value;
    Node<K, V> parent;
    CircularLinkedList<Node<K, V>> childList;
    int degree;
    boolean isLostChild;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(Node<K, V> o) {
        return key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

class FibonacciMinHeap<K extends Comparable<K>, V> implements IPriorityQueue<K, V> {
    Node<K, V> min;
    CircularLinkedList<Node<K, V>> rootList;
    int n;
    int potential;

    public FibonacciMinHeap() {
        n = 0;
        min = null;
        potential = 0;
        rootList = new CircularLinkedList<>();
    }

    @Override
    public void insert(Object item) throws NullPointerException {
        if (item == null) throw new NullPointerException();
        Node<K, V> x = (Node<K, V>) item;
        x.degree = 0;
        x.parent = null;
        x.childList = new CircularLinkedList<>();
        x.isLostChild = false;
        if (this.min == null) {
            this.min = x;
        }
        this.rootList.add(x);
        this.n++;
    }

    @Override
    public void union(Object anotherQueue) throws NullPointerException {
        if (anotherQueue == null) throw new NullPointerException();
        FibonacciMinHeap<K, V> H = (FibonacciMinHeap<K, V>) anotherQueue;
        this.rootList.combine(H.rootList);
        if (H.min != null && H.min.compareTo(this.min) < 0) {
            this.min = H.min;
        }
        this.n += H.n;
        this.potential += H.potential;
    }

    @Override
    public Object findMin() throws NullPointerException {
        return this.min;
    }

    @Override
    public Object extractMin() {
        Node<K, V> extracted = this.min;
        if (extracted != null) {
            while (extracted.childList.hasNext()) {
                Node<K, V> child = extracted.childList.next();
                this.rootList.add(child);
                child.parent = null;
            }
            this.rootList.remove(extracted);
            if (extracted.compareTo(extracted.rightSibling) == 0) {
                this.min = null;
            } else {
                this.min = extracted.rightSibling;
                consolidate();
            }
            this.n--;
        }
        return extracted;
    }

    private void consolidate() {
        //TODO - size is log(phi)(n), might be problems with size
        int size = (int) Math.round(Math.log(this.n) / Math.log(1.6)) + 1;
        List<Node<K, V>> a = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            a.add(null);
        }
        Node<K, V> w = this.min;
        while (true) {
            w = w.rightSibling;
            Node<K, V> currentNode = w;
            int currentDegree = w.degree;
            if (a.get(currentDegree) != null && currentNode.compareTo(a.get(currentDegree)) == 0) {
                break;
            }
            while (a.get(currentDegree) != null) {
                Node<K, V> suspendedNode = a.get(currentDegree);
                if (currentNode.key.compareTo(suspendedNode.key) > 0) {
                    Node<K, V> tmp = currentNode;
                    currentNode = suspendedNode;
                    suspendedNode = tmp;
                    assert currentNode.compareTo(suspendedNode) != 0 : "Incorrect swap";
                }
                link(suspendedNode, currentNode);
                a.set(currentDegree, null);
                currentDegree++;
            }
            a.set(currentDegree, currentNode);
        }
        this.min = null;
        for (int i = 0; i < size; ++i) {
            if (a.get(i) != null) {
                if (this.min == null) {
                    this.min = a.get(i);
                    this.min.leftSibling = this.min.rightSibling = a.get(i);
                } else {
                    Node<K, V> inserted = a.get(i);
                    inserted.leftSibling = this.min;
                    inserted.rightSibling = this.min.rightSibling;
                    this.min.rightSibling.leftSibling = inserted;
                    this.min.rightSibling = inserted;
                    if (a.get(i).compareTo(this.min) < 0) {
                        this.min = a.get(i);
                    }
                }
            }
        }

    }

    private void link(Node<K, V> adding, Node<K, V> addTo) {
        adding.leftSibling.rightSibling = adding.rightSibling;
        adding.rightSibling.leftSibling = adding.leftSibling;
        if (addTo.child == null) {
            addTo.child = adding;
            adding.leftSibling = adding.rightSibling = adding;
        } else {
            adding.leftSibling = addTo.leftSibling;
            adding.rightSibling = addTo.rightSibling;
            addTo.child.rightSibling.leftSibling = adding;
            addTo.child.rightSibling = adding;
        }
        addTo.degree++;
        adding.isLostChild = false;
    }

    @Override
    public void decreaseKey(Object item, K newKey) {

    }

    @Override
    public void delete(Object item) {

    }


}
