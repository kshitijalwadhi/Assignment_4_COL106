import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class assignment4 {
    public static void main(String[] args) throws Exception {
        HashMap<String, Node> mp1 = new HashMap<>();
        HashMap<String, Integer> mp2 = new HashMap<>();
        BufferedReader nodes_csv = new BufferedReader(new FileReader("nodes.csv"));
        String row;
        int index = 0;
        while ((row = nodes_csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[1].equals("Label")) {
                // System.out.println(data[1] + ": " + index);
                mp1.put(data[1], new Node(data[1]));
                mp2.put(data[1], index);
                index++;
            }
        }
        int count = index;
        nodes_csv.close();

        Graph g = new Graph(count, mp2);

        BufferedReader edges_csv = new BufferedReader(new FileReader("edges.csv"));
        while ((row = edges_csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[0].equals("Source")) {
                // System.out.println(data[0] + ": " + data[1] + ": " + data[2]);
                Edge e1 = new Edge(mp1.get(data[0]), mp1.get(data[1]), Integer.parseInt(data[2]));
                Edge e2 = new Edge(mp1.get(data[1]), mp1.get(data[0]), Integer.parseInt(data[2]));
                g.addEdge(e1);
                g.addEdge(e2);
            }
        }
        edges_csv.close();

        g.average();

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

    public void average() {
        float sum = 0;
        for (int i = 0; i < V; i++) {
            sum += adj[i].size();
        }
        float ans = sum / V;
        String formatted = String.format("%.02f", ans);
        System.out.println(formatted);
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