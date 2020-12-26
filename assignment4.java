import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;

public class assignment4 {
    public static void main(String[] args) {
        String[] arr1 = { "A", "B", "C" };
        HashMap<String, Node> mp1 = new HashMap<>();
        HashMap<String, Integer> mp2 = new HashMap<>();
        for (int i = 0; i < arr1.length; i++) {
            mp1.put(arr1[i], new Node(arr1[i]));
            mp2.put(arr1[i], i);
        }
        Graph g = new Graph(arr1.length, mp2);
        Edge e1 = new Edge(mp1.get("A"), mp1.get("B"), 7);
        Edge e2 = new Edge(mp1.get("B"), mp1.get("C"), 6);
        Edge e3 = new Edge(mp1.get("C"), mp1.get("A"), 3);
        Edge[] e = { e1, e2, e3 };
        for (int i = 0; i < e.length; i++) {
            g.addEdge(e[i]);
        }
    }
}

class Graph {
    private int V;
    private HashMap<String, Integer> mp2;
    LinkedList<Edge> adj[];

    public Graph(int v, HashMap<String, Integer> mp2) {
        this.V = v;
        this.mp2 = mp2;
        adj = new LinkedList[V];
        for (int i = 0; i < v; i++) {
            adj[i] = new LinkedList<Edge>();
        }
    }

    public void addEdge(Edge e) {
        int loc = mp2.get(e.getSource().getId());
        adj[loc].addFirst(e);
    }

}

class Edge {
    private Node source;
    private Node target;
    private int weight;

    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }
}

class Node {
    private String id;

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}