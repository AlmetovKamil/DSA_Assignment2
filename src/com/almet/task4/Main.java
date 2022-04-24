package com.almet.task4;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Class with main logic
 * </br>
 * You can find the links to Codeforces submissions in CodeforcesSubmissions.txt file
 * (com.almet.CodeforcesSubmissions.txt)
 *
 * @author Kamil Almetov BS21-05
 */
public class Main {
    public static final int SIZE = 3000;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        HashMap<String, Vertex<VertexPair>> vertices = new HashMap<>();
        Graph<VertexPair, Double> graph = new Graph<>(SIZE, Double.class);
        // array of heap nodes that contain vertices of graph
        ArrayList<Node<Double, Vertex<VertexPair>>> vertexArray = new ArrayList<>(SIZE);
        for (int i = 0; i < n; ++i) {
            String operation = in.next();
            // ADD operation
            if (operation.charAt(0) == 'A') {
                String vertexName = in.next();
                double penalty = in.nextDouble();
                Vertex<VertexPair> v = graph.insertVertex(new VertexPair(vertexName, penalty));
                vertices.put(v.value.name, v);
                if (vertexArray.size() == 0) {
                    vertexArray.add(new Node<>(0., v));
                } else {
                    vertexArray.add(new Node<>(Double.MAX_VALUE, v));
                }
            }
            // CONNECT operation
            else if (operation.charAt(0) == 'C') {
                String name1 = in.next(), name2 = in.next();
                Vertex<VertexPair> v1 = vertices.get(name1);
                Vertex<VertexPair> v2 = vertices.get(name2);
                double distance = in.nextDouble() / (v1.value.penalty + v2.value.penalty);
                graph.insertEdge(v1, v2, distance);
            }
            // PRINT_MIN operation
            else {
                AlgorithmsOnGraphs.PrimsAlgorithm(graph, vertexArray);
            }
        }
        in.close();
    }
}

/**
 * Class with algorithms on graphs.
 * (Sometimes I use in my explanation term MST. In that specific task by saying MST I mean Minimum Spanning Forest)
 */
class AlgorithmsOnGraphs {
    // We're building MST, so parent[i] = id of previous vertex in the MST (it's parent)
    static int[] parent = new int[Main.SIZE];
    // minDistance[i] = minimum distance to MST from vertex with 'i' id
    static double[] minDistance = new double[Main.SIZE];
    // if vertex with 'i' id is in queue isInQueue[i] = true
    static boolean[] isInQueue = new boolean[Main.SIZE];

    /**
     * Algorithm that finds Minimum Spanning Forest of graph
     *
     * @param graph       graph in which MST will be found
     * @param vertexArray array of heap nodes that contain vertices of graph
     */
    public static void PrimsAlgorithm(Graph<VertexPair, Double> graph, ArrayList<Node<Double, Vertex<VertexPair>>> vertexArray) {
        // priority queue based on Fibonacci heap
        FibonacciMinHeap<Double, Vertex<VertexPair>> queue = new FibonacciMinHeap<>(Double.MIN_VALUE);
        StringBuilder ans = new StringBuilder();
        int size = vertexArray.size();
        // fill arrays with starting values
        // algorithm starts from vertex with id = 0
        // so minDistance[0] = 0
        // others equal to infinity
        for (int id = 0; id < size; ++id) {
            parent[id] = -1;
            if (id == 0) minDistance[id] = 0;
            else minDistance[id] = Double.MAX_VALUE;
            // make sure that the keys of nodes are correct
            vertexArray.get(id).key = minDistance[id];
            // add to queue only vertices that have incident edges
            if (vertexArray.get(id).value.degree != 0) {
                isInQueue[id] = true;
                queue.insert(vertexArray.get(id));
            } else {
                isInQueue[id] = false;
            }
        }
        Node<Double, Vertex<VertexPair>> u;
        // while something is in the queue, perform algorithm
        while (!queue.isEmpty()) {
            // extract closest to the MST vertex from the queue
            u = queue.extractMin();
            isInQueue[u.value.id] = false;
            // look at all its adjacent vertices
            for (int i = 0; i < size; i++) {
                Double edge = graph.adjacencyMatrix[u.value.id][i];
                if (edge == null) continue;
                Node<Double, Vertex<VertexPair>> v = vertexArray.get(i);
                // if adjacent vertex is in queue and the distance between them is less than the distance from MST to the adjacent vertex
                if (isInQueue[i] && edge.compareTo(minDistance[i]) < 0) {
                    // add adjacent vertex to MST
                    parent[i] = u.value.id;
                    // update the distance from MST to adjacent vertex (in the queue also)
                    minDistance[i] = edge;
                    queue.decreaseKey(v, edge);
                }
            }
            // add the edge to the answer
            if (parent[u.value.id] != -1) {
                ans.append(vertexArray.get(parent[u.value.id]).value.value.name).append(":").append(u.value.value.name).append(" ");
            }
        }
        System.out.println(ans);
    }

}

/**
 * Class that represents the vertex of graph
 *
 * @param <V> type of value of vertex
 */
class Vertex<V> {
    int id;
    V value;
    // number of incident edges
    int degree;

    public Vertex(int id, V value) {
        this.id = id;
        this.value = value;
        this.degree = 0;
    }
}

/**
 * Class that represents edge of graph
 *
 * @param <V> type of vertex value
 * @param <E> type of edge distance
 */
class Edge<V, E> {
    E distance;
    Vertex<V> v1, v2;
}

/**
 * Interface that represents graph operations
 *
 * @param <V> type of vertex value
 * @param <E> type of edge distance
 */
interface IGraph<V, E> {
    /**
     * Method that inserts vertex with value 'v' to the graph
     *
     * @param v value of vertex
     * @return reference to created vertex
     */
    Vertex<V> insertVertex(V v);

    /**
     * Method that inserts edge to the graph
     *
     * @param from first ending of edge
     * @param to   second ending of edge
     * @param w    edge's distance
     * @return reference to created edge
     */
    Edge<V, E> insertEdge(Vertex<V> from, Vertex<V> to, E w);

    /**
     * Method that removes vertex 'v' from the graph
     *
     * @param v removing vertex
     */
    void removeVertex(Vertex<V> v);

    /**
     * Method that removes edge 'e' from the graph
     *
     * @param e removing edge
     */
    void removeEdge(Edge<V, E> e);

    /**
     * Method that returns degree of vertex 'v'
     *
     * @param v vertex which degree is found
     * @return degree of vertex
     */
    int degree(Vertex<V> v);

    /**
     * @param v first vertex
     * @param u second vertex
     * @return true if 'u' and 'v' are adjacent, false otherwise
     */
    boolean areAdjacent(Vertex<V> v, Vertex<V> u);
}

/**
 * Class that represents vertex value for this task
 */
class VertexPair {
    String name;
    double penalty;


    public VertexPair(String name, double penalty) {
        this.name = name;
        this.penalty = penalty;
    }
}

/**
 * Class that implements IGraph based on adjacency matrix
 *
 * @param <V> type of vertex value
 * @param <E> type of edge distance
 * @see IGraph
 */
class Graph<V, E> implements IGraph<V, E> {
    // currentIndex is used to insert new vertices
    int currentIndex;
    // capacity of adjacent matrix
    int capacity;
    E[][] adjacencyMatrix;
    // HashMap of vertices. key is the value of vertex, value - the reference to vertex
    public HashMap<V, Vertex<V>> vertices;

    public Graph(int capacity, Class<? extends E> ex) {
        this.currentIndex = 0;
        this.capacity = capacity;
        this.adjacencyMatrix = (E[][]) Array.newInstance(ex, capacity, capacity);
        this.vertices = new HashMap<>();
    }

    /**
     * @param v value of vertex
     * @return the reference to inserted vertex
     * @see IGraph#insertVertex(V)
     */
    @Override
    public Vertex<V> insertVertex(V v) {
        Vertex<V> vertex = new Vertex<>(currentIndex++, v);
        this.vertices.put(vertex.value, vertex);
        return vertex;
    }

    /**
     * @param from first ending of edge
     * @param to   second ending of edge
     * @param w    edge's distance
     * @return reference to created edge
     * @see IGraph#insertEdge(Vertex, Vertex, E)
     */
    @Override
    public Edge<V, E> insertEdge(Vertex<V> from, Vertex<V> to, E w) {
        Edge<V, E> edge = new Edge<>();
        edge.v1 = from;
        edge.v2 = to;
        edge.distance = w;
        adjacencyMatrix[from.id][to.id] = w;
        adjacencyMatrix[to.id][from.id] = w;
        from.degree++;
        to.degree++;
        return edge;
    }

    /**
     * @param v removing vertex
     * @see IGraph#removeVertex(Vertex)
     */
    @Override
    public void removeVertex(Vertex<V> v) {
        for (int i = 0; i < vertices.size(); ++i) {
            adjacencyMatrix[v.id][i] = null;
            adjacencyMatrix[i][v.id] = null;
        }
        vertices.remove(v.value);
    }

    /**
     * @param e removing edge
     * @see IGraph#removeEdge(Edge)
     */
    @Override
    public void removeEdge(Edge<V, E> e) {
        adjacencyMatrix[e.v1.id][e.v2.id] = null;
        adjacencyMatrix[e.v2.id][e.v1.id] = null;
        e.v1.degree--;
        e.v2.degree--;

    }

    /**
     * @param v vertex which degree is found
     * @return degree of vertex
     * @see IGraph#degree(Vertex)
     */
    @Override
    public int degree(Vertex<V> v) {
        return v.degree;
    }

    /**
     * @param v first vertex
     * @param u second vertex
     * @return true if 'u' and 'v' are adjacent, false otherwise
     * @see IGraph#areAdjacent(Vertex, Vertex)
     */
    @Override
    public boolean areAdjacent(Vertex<V> v, Vertex<V> u) {
        return adjacencyMatrix[v.id][u.id] != null;
    }
}

/**
 * Interface of priority queue.
 *
 * @param <K> type of key
 * @param <V> type of value
 */
interface IPriorityQueue<K extends Comparable<K>, V> {
    /**
     * Insert item to the priority queue
     *
     * @param item element that will be inserted
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
     * @param item deleted element
     */
    void delete(Node<K, V> item);

    /**
     * Method that merges 'anotherQueue' with the current one
     *
     * @param anotherQueue a queue that will be added to the current one
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
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return n == 0;
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
     *
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
        // nodes in the root list don't have a parent, so remove it
        Node<K, V> current = this.min;
        current.parent = null;
        rootList.add(current);
        current = current.right;
        while (current != this.min) {
            current.parent = null;
            rootList.add(current);
            current = current.right;
        }
        // currentIndex is used to iterate through the rootList
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
                    adding.isLostChild = false;
                }
                // otherwise 'adding' becomes one of the children of 'addTo'
                // we add 'adding' to the list of 'addTo' children
                // adding is not lost child
                else {
                    linkLists(addTo.child, adding);
                    adding.isLostChild = false;
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
     *
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
        // if there is a parent, and it's bigger than its child, cut the item and cascade cut for all its ancestors
        if (parent != null && item.compareTo(parent) < 0) {
            cut(item, parent);
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
     *
     * @param node   the node that will be cut
     * @param parent node's parent
     */
    void cut(Node<K, V> node, Node<K, V> parent) {
        // delete node from its position
        // appropriately changing left-right references of its neighbours
        node.left.right = node.right;
        node.right.left = node.left;

        // parent's degree decreased
        parent.degree--;
        // if the node is the parent's reference to its child,
        // we need to change it to one of its siblings to not lose the child reference of the parent
        if (parent.child == node) {
            // but if node is the only one child, after removing it from its position,
            // parent doesn't have any children anymore
            if (node.right == node) {
                parent.child = null;
            } else {
                parent.child = node.right;
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
     *
     * @param node the current node of cascading cut
     */
    void cascadingCut(Node<K, V> node) {
        Node<K, V> parent = node.parent;
        if (parent != null) {
            if (!node.isLostChild) {
                node.isLostChild = true;
            } else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    /**
     * This function deletes item from the heap
     *
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
