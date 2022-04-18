package com.almet.task3;

import java.util.*;

/**
 * Task 3 of DSA Assignment 2
 * Class with main logic and some test functions
 * @author Kamil Almetov BS21-05
 */
public class Main {
    public static void main(String[] args) {
        // read the input
        Scanner in = new Scanner(System.in);
        IPriorityQueue<Integer, String> queue = new FibonacciMinHeap<>(Integer.MIN_VALUE);
        int n = in.nextInt();
        in.nextLine();
        for (int i = 0; i < n; i++) {
            String[] line = in.nextLine().split(" ");
            // ADD operation
            // line == {"ADD", branch name, penalty}
            if (line[0].equals("ADD")) {
                String value = line[1];
                int key = Integer.parseInt(line[2]);
                queue.insert(new Node<>(key, value));
            }
            // PRINT_MIN operation
            else {
                System.out.println(queue.extractMin());
            }
        }
    }

    /**
     * This function was used for testing extractMin function of the heap
     * We insert n nodes with random integer keys to the queue
     * and then extract them all and add to the 'a' array.
     * It's the same as performing heap sort.
     * So if extract min works correctly, the 'a' array must be sorted
     */
    static void testExtractMin() {
        IPriorityQueue<Integer, Integer> q = new FibonacciMinHeap<>(Integer.MIN_VALUE);
        Random random = new Random();
        int n = 100000;
        List<Integer> input = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            int k = random.nextInt();
            input.add(k);
            q.insert(new Node<>(k, k));
        }

        List<Integer> a = new ArrayList<>(n);
        //System.out.println(input);
        for (int i = 0; i < n; i++) {
            Node<Integer, Integer> node = q.extractMin();
            a.add(node.key);
        }
        Collections.sort(input);
        System.out.println(input.equals(a));
        //System.out.println(a);
    }

    /**
     * This function was used to test delete operation
     * We add n nodes to the heap from 0 to n-1 integers, then delete each element i such that i%10 == 0
     * Extract all elements and check if deleted elements were actually deleted
     */
    static void testDelete() {
        IPriorityQueue<Integer, Integer> q = new FibonacciMinHeap<>(Integer.MIN_VALUE);
        int n = 100;
        List<Node<Integer, Integer>> toBeDeleted = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Node<Integer, Integer> node = new Node<>(i, i);
            if (i % 10 == 0) {
                toBeDeleted.add(node);
            }
            q.insert(node);
        }
        List<Integer> a = new ArrayList<>(n);
        for (Node<Integer, Integer> integerIntegerNode : toBeDeleted) {
            q.delete(integerIntegerNode);
        }
        for (int i = 0; i < n - 10; i++) {
            a.add((q.extractMin()).key);
        }
        System.out.println(a);
    }
}

/**
 * Interface that represents Minimum Priority Queue
 *
 * @param <K> type of the key of an element
 * @param <V> type of the value of an element
 * @see FibonacciMinHeap
 */
interface IPriorityQueue<K extends Comparable<K>, V> {
    /**
     * Insert item to the priority queue
     *
     * @param item - element that will be inserted
     */
    void insert(Node<K, V> item);

    /**
     * Method for getting minimum element of the queue
     *
     * @return minimum element od the queue
     */
    Node<K, V> findMin();

    /**
     * Method that gets minimum element of the queue and deletes it
     *
     * @return minimum element of the queue
     */
    Node<K, V> extractMin();

    /**
     * Method that decreases key of element 'item' to newKey
     *
     * @param item   element which key will be decreased
     * @param newKey the value that will be the new key of 'item'
     */
    void decreaseKey(Node<K, V> item, K newKey);

    /**
     * Method that deletes element 'item'
     *
     * @param item - deleted element
     */
    void delete(Node<K, V> item);

    /**
     * Method that merges 'anotherQueue' with the current one
     *
     * @param anotherQueue - a queue that will be added to the current one
     */
    void union(FibonacciMinHeap<K, V> anotherQueue);
}

/**
 * Class Node - a node of FibonacciMinHeap
 *
 * @param <K> the type of the key of a node
 * @param <V> the type of the value of the node
 * @see FibonacciMinHeap
 * @see Comparable
 */
class Node<K extends Comparable<K>, V> implements Comparable<Node<K, V>> {
    /**
     * Key of node
     */
    K key;
    /**
     * Value of node
     */
    V value;
    /**
     * References to the left and right siblings of the node
     * They are needed for FibonacciMinHeap for maintaining root list and child lists
     */
    Node<K, V> left, right;
    /**
     * Reference to the parent of the node
     */
    Node<K, V> parent;
    /**
     * Reference of one of the children of the node
     * (Other children can be achieved using 'left' and 'right' fields of the child)
     */
    Node<K, V> child;
    /**
     * Degree of the node
     */
    int degree;
    /**
     * boolean flag that shows whether a child of the node was deleted
     */
    boolean isLostChild;

    /**
     * Constructor of Node class
     *
     * @param key   key of the node
     * @param value value of the node
     */
    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * compareTo method - for comparing Node instances
     * If keys are distinct, compare using them, otherwise compare by values
     *
     * @param o the Node<K, V> to be compared.
     * @return number less than 0 if the current node is less than 'o', equal to 0 if they are equal,
     * greater than 0 if the current node is greater than 'o'
     */
    @Override
    public int compareTo(Node<K, V> o) {
        return key.compareTo(o.key);
    }

    /**
     * String representation of the node
     *
     * @return string representation of the value
     */
    @Override
    public String toString() {
        return value.toString();
    }
}

/**
 * Class that represents Fibonacci minimum heap
 * It has a reference to the minimum node
 * The root list is represented as circular doubly linked list using left and right references of nodes
 * Child list of every node is also represented as circular doubly linked list (a reference to one of the children is available
 * and all others are interconnected by references to the left and right siblings)
 *
 * @param <K> - the type of the key of a node
 * @param <V> - the type of the value of a node
 * @see IPriorityQueue
 */
class FibonacciMinHeap<K extends Comparable<K>, V> implements IPriorityQueue<K, V> {
    /**
     * Constant field that is less than any key in the heap (specified by a user)
     */
    public final K MIN_KEY;
    /**
     * Reference to the minimum node (using it we can access all elements in the root list using its 'left' and 'right' fields)
     */
    Node<K, V> min;
    /**
     * Number of nodes in the heap
     */
    int n;

    /**
     * Constructor for fibonacci heap that specifies MIN_KEY
     * number of elements is 0 and min node is null at the beginning
     *
     * @param MIN_KEY the minimum possible value of the type K (it's needed for decreaseKey() and delete() methods)
     */
    public FibonacciMinHeap(K MIN_KEY) {
        this.MIN_KEY = MIN_KEY;
        n = 0;
        min = null;
    }

    /**
     * Add a list which member is n2 to a list which member is n1
     * changing appropriately the left and right references
     *
     * @param n1 member of the first list
     * @param n2 member of the second list
     */
    void linkLists(Node<K, V> n1, Node<K, V> n2) {
        // if n1 is null we cannot add to it something
        if (n1 == null) {
            return;
        }
        // if n2 is null there is nothing to add
        if (n2 == null) return;
        // we take n1, n1.left, n2, n2.right and connect them in such a way that two circular lists become one combined circular list
        n1.left.right = n2.right;
        n2.right.left = n1.left;
        n1.left = n2;
        n2.right = n1;
    }

    /**
     * Method that inserts item to the heap
     *
     * @param item - element that will be inserted
     * @throws NullPointerException if the item is null
     */
    @Override
    public void insert(Node<K, V> item) throws NullPointerException {
        // item should be not null
        if (item == null) throw new NullPointerException();
        // item is new node
        item.degree = 0;
        item.parent = null;
        item.child = null;
        item.isLostChild = false;
        // if the heap is empty, item becomes the minimum node,
        // and there are no siblings, in that case left and right reference to node itself
        if (this.min == null) {
            this.min = item;
            this.min.left = this.min.right = item;
        }
        // otherwise add item to the root list to the right of the min node
        else {
            // update the references appropriately
            Node<K, V> rightToMin = this.min.right;
            this.min.right = item;
            rightToMin.left = item;
            item.left = this.min;
            item.right = rightToMin;
        }
        // update the minimum, if the item is less than the current one
        if (item.compareTo(this.min) < 0) {
            this.min = item;
        }
        // we added an item, so increase the number of elements
        this.n++;
    }

    /**
     * Method for finding minimum node
     *
     * @return minimum node of the heap (null if empty)
     */
    @Override
    public Node<K, V> findMin() {
        return this.min;
    }

    /**
     * Method that combines two heaps - it adds 'anotherQueue' to the current.
     *
     * @param anotherQueue - a queue that will be added to the current one
     */
    @Override
    public void union(FibonacciMinHeap<K, V> anotherQueue) throws NullPointerException {
        // if anotherQueue is null we don't need to add anything
        if (anotherQueue == null) return;
        // the same when anotherQueue is empty
        if (anotherQueue.n == 0) {
            return;
        }
        // if current heap is empty, just assign current heap to anotherQueue
        if (this.n == 0) {
            this.min = anotherQueue.min;
            this.n = anotherQueue.n;
        }
        // otherwise link the root lists of the current heap and the anotherQueue,
        // update the number of elements
        else {
            linkLists(this.min, anotherQueue.min);
            this.n += anotherQueue.n;
        }
        // update the minimum if necessary
        if (anotherQueue.min.compareTo(this.min) < 0) {
            this.min = anotherQueue.min;
        }
    }

    /**
     * Method that finds the minimum node, returns it, and then deletes it
     * @return minimum node that will be deleted
     * @throws NullPointerException if the heap is empty
     */
    @Override
    public Node<K, V> extractMin() throws NullPointerException {
        // if the heap is null, throw an exception
        if (this.min == null) throw new NullPointerException();
        // extracted - the node that will be returned and deleted
        Node<K, V> extracted = this.min;
        // transfer all children of the extracted node to the root list
        linkLists(this.min, this.min.child);
        // remove extracted from the root list
        // changing the left-right references appropriately
        Node<K, V> leftToExtracted = extracted.left;
        Node<K, V> rightToExtracted = extracted.right;
        leftToExtracted.right = rightToExtracted;
        rightToExtracted.left = leftToExtracted;
        // we delete the extracted node, so the number of elements decreases
        this.n--;
        // if extracted node was the only one node in the root list
        // (after adding its children to the root list) then extracted node
        // was the only one in the list, so now the heap is empty
        if (extracted == extracted.right) {
            this.min = null;
        }
        // otherwise change the minimum node reference to its neighbour
        // because current minimum is invalid (it refers to the extracted node)
        else {
            this.min = this.min.right;
            extracted.left = extracted.right = extracted;
            // run the consolidate function
            consolidate();
        }
        // return extracted node
        return extracted;
    }

    /**
     * Method that thins the heap in such a way that in the root list there will be no more than Degree(heap) + 1 elements
     * Degree(heap) - the maximum degree of the node in the root list
     * It's known fact that degree(heap) <= log(n) of base phi - the golden ratio (n - number of nodes in the heap)
     * In my implementation I use log(n) of base 1.6.
     * Since phi is greater than 1.6, log(n) of base 1.6 is greater than log(n) of base phi
     * In this algorithm we want to all elements in the root list have different degrees
     */
    private void consolidate() {
        // calculate the size using log(n) of base 1.6
        int size = (int) Math.round(Math.log(this.n) / Math.log(1.6)) + 1;
        // array where a[degree] = node.degree;
        List<Node<K, V>> a = new ArrayList<>(size);
        // all elements are null at the beginning
        for (int i = 0; i < size; ++i) {
            a.add(null);
        }
        // root list is needed here to iterate through all root list elements.
        // Since root list is changing during the while loop, we can't simply use 'right' references
        List<Node<K, V>> rootList = new ArrayList<>();
        // fill the root list
        Node<K, V> current = this.min;
        rootList.add(current);
        current = current.right;
        while (current != this.min) {
            rootList.add(current);
            current = current.right;
        }
        // currentIndex is used to iterate through the
        int currentIndex = 0;
        current = rootList.get(currentIndex);
        while (currentIndex < rootList.size()) {
            // if a[current.degree] is null, then put current here
            // and go to the next root list element
            if (a.get(current.degree) == null) {
                a.set(current.degree, current);
                currentIndex++;
                if (currentIndex >= rootList.size()) {
                    break;
                }
                current = rootList.get(currentIndex);
            }
            // otherwise there are some other node with the same degree
            // so combine the current node with it
            else {
                Node<K, V> nodeWithSameDegree = a.get(current.degree);
                Node<K, V> addTo, adding;
                // the root of new subtree should be minimum because it will stay in the root list,
                // and other root will be the child of the first one
                if (current.compareTo(nodeWithSameDegree) < 0) {
                    addTo = current;
                    adding = nodeWithSameDegree;
                } else {
                    addTo = nodeWithSameDegree;
                    adding = current;
                }
                // remove 'adding' from the root list
                // appropriately changing the left-right references
                adding.left.right = adding.right;
                adding.right.left = adding.left;
                adding.left = adding.right = adding;
                // if there are no children of the 'addTo' node, 'adding' becomes the first one
                if (addTo.child == null) {
                    addTo.child = adding;
                }
                // otherwise 'adding' becomes one of the children od 'addTo'
                // we add 'adding' to the list of 'addTo' children
                else {
                    linkLists(addTo.child, adding);
                }
                // 'adding' parent is now 'addTo'
                adding.parent = addTo;
                // the 'addTo' degree has increased
                addTo.degree++;
                // we finished with 'adding', so there is no more node with adding.degree in array 'a'
                a.set(adding.degree, null);
                // current now is 'addTo' because there may be a node with addTo.degree,
                // and if it so, we need to combine it with 'addTo'
                current = addTo;
            }
            // update minimum if necessary
            if (current.compareTo(this.min) <= 0) {
                this.min = current;
            }
        }
    }

    /**
     * Method that decreases key of element 'item' to newKey
     * @param item   element which key will be decreased
     * @param newKey the value that will be the new key of 'item'
     * @throws IllegalArgumentException if the new key is greater than or equal to the previous key
     */
    @Override
    public void decreaseKey(Node<K, V> item, K newKey) throws IllegalArgumentException {
        // item have to be not null
        assert item != null;
        if (newKey.compareTo(item.key) >= 0) {
            throw new IllegalArgumentException("The new key is greater than or equal to the previous key");
        }
        Node<K, V> parent = item.parent;
        // assign newKey to item key
        item.key = newKey;
        // if there is a parent and it's bigger than its child, cut the item and cascade cut for all its ancestors
        if (parent != null && item.compareTo(parent) < 0) {
            cut(item);
            cascadingCut(parent);
        }
        // update the minimum if necessary
        if (item.compareTo(this.min) < 0) {
            this.min = item;
        }
    }

    /**
     * this method cuts the node from its position in the heap
     * and transfers it to the root list
     * @param node the node that will be cut
     */
    void cut(Node<K, V> node) {
        // delete node from its position
        // appropriately changing left-right references of its neighbours
        node.left.right = node.right;
        node.right.left = node.left;
        // node.parent have to be not null,
        // otherwise this operation makes no sense
        assert node.parent != null;
        // parent's degree decreased
        node.parent.degree--;
        // if the node is the parent's reference to its child,
        // we need to change it to one of its siblings to not lose the child reference of the parent
        if (node.parent.child == node) {
            // but if node is the only one child, after removing it from its position,
            // parent doesn't have any children anymore
            if (node.right == node) {
                node.parent.child = null;
            } else {
                node.parent.child = node.right;
            }
        }
        // we cut node from its positions, so there is no neighbours and parent
        node.left = node.right = node;
        node.parent = null;
        // since node now will be in the root list, the flag should be false
        node.isLostChild = false;
        // add node to the root list
        linkLists(this.min, node);
    }

    /**
     * Function that performs cascade cut of all ancestors nodes (including current node) if they have lost their child
     * @param node the node from which cascading cut starts
     */
    void cascadingCut(Node<K, V> node) {
        // all nodes that have lost their child should be cut
        while (node.isLostChild) {
            cut(node);
            assert node.parent != null;
            node = node.parent;
        }
        // the last one is losing its child, so the flag is true for it
        node.isLostChild = true;
    }

    /**
     * This function deletes item from the heap
     * @param item - deleted element
     */
    @Override
    public void delete(Node<K, V> item) {
        // deleting item have to be not null
        assert item != null;
        // decrease item's key to the minimum possible,
        // so that the item becomes the minimum of the heap
        decreaseKey(item, MIN_KEY);
        // Now we can extract it, since it's the minimum
        extractMin();
    }


}
