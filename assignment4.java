import java.io.*;
//import java.util.ArrayList;
//import java.util.Vector;
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
                data[1] = data[1].replace("\"", "");
                // System.out.println(data[1] + ": " + index);
                mp1.put(data[1], new Node(data[1]));
                mp2.put(data[1], index);
                index++;
            }
        }
        int count = index;
        nodes_csv.close();

        Graph g = new Graph(count, mp1, mp2);

        BufferedReader edges_csv = new BufferedReader(new FileReader("edges.csv"));
        while ((row = edges_csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[0].equals("Source")) {
                // System.out.println(data[0] + ": " + data[1] + ": " + data[2]);
                data[0] = data[0].replace("\"", "");
                data[1] = data[1].replace("\"", "");
                Edge e1 = new Edge(mp1.get(data[0]), mp1.get(data[1]), Integer.parseInt(data[2]));
                Edge e2 = new Edge(mp1.get(data[1]), mp1.get(data[0]), Integer.parseInt(data[2]));
                g.addEdge(e1);
                g.addEdge(e2);
            }
        }
        edges_csv.close();

        g.average();
        g.rank();

    }
}

@SuppressWarnings("unchecked")
class Graph {
    private int V;
    private HashMap<String, Node> mp1;
    private HashMap<String, Integer> mp2;
    LinkedList<Edge> adj[];

    public Graph(int v, HashMap<String, Node> mp1, HashMap<String, Integer> mp2) {
        this.V = v;
        this.mp1 = mp1;
        this.mp2 = mp2;
        adj = new LinkedList[V];
        for (int i = 0; i < v; i++) {
            adj[i] = new LinkedList<Edge>();
        }
    }

    public void addEdge(Edge e) {
        e.getSource().increaseNumCoOccurence(e.getWeight());
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

    public void merge(Node arr[], int l, int m, int r) {
        int num1 = m - l + 1;
        int num2 = r - m;

        Node left[] = new Node[num1];
        Node right[] = new Node[num2];

        for (int i = 0; i < num1; i++)
            left[i] = arr[l + i];
        for (int j = 0; j < num2; j++)
            right[j] = arr[m + j + 1];

        int i = 0;
        int j = 0;

        int k = l;
        while (i < num1 && j < num2) {
            if (left[i].getNumCoOccurence() > right[j].getNumCoOccurence()) {
                arr[k] = left[i];
                i++;
            } else if (left[i].getNumCoOccurence() < right[j].getNumCoOccurence()) {
                arr[k] = right[j];
                j++;
            } else {
                // check this order
                if (left[i].getId().compareTo(right[i].getId()) > 0) {
                    arr[k] = left[i];
                    i++;
                } else {
                    arr[k] = right[j];
                    j++;
                }
            }
            k++;
        }

        while (i < num1) {
            arr[k] = left[i];
            k++;
            i++;
        }
        while (j < num2) {
            arr[k] = right[j];
            j++;
            k++;
        }
    }

    public void customSort(Node arr[], int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            customSort(arr, l, m);
            customSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    public void rank() {
        // Node temp = mp1.get("Black Panther / T'chal");
        // System.out.println((temp.getNumCoOccurence()));
        Iterator<Map.Entry<String, Node>> hashmapiter = mp1.entrySet().iterator();
        Node[] nodearr = new Node[V];
        int i = 0;
        while (hashmapiter.hasNext()) {
            Map.Entry<String, Node> elem = hashmapiter.next();
            nodearr[i] = elem.getValue();
            i++;
        }

        customSort(nodearr, 0, V - 1);
        for (int j = 0; j < V; j++) {
            // System.out.println(nodearr[j].getId() + ": " +
            // nodearr[j].getNumCoOccurence());
            System.out.print(nodearr[j].getId() + ",");
        }
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
    private int numCoOcc;

    public Node(String id) {
        this.id = id;
        this.numCoOcc = 0;
    }

    public int getNumCoOccurence() {
        return numCoOcc;
    }

    public void increaseNumCoOccurence(int n) {
        this.numCoOcc += n;
    }

    public String getId() {
        return id;
    }
}