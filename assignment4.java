import java.io.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class assignment4 {
    public static void main(String[] args) throws Exception {
        HashMap<String, Node> mp1 = new HashMap<>();
        HashMap<String, Integer> mp2 = new HashMap<>();
        BufferedReader nodes_csv = new BufferedReader(new FileReader(args[0]));
        String row;
        int index = 0;
        while ((row = nodes_csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[1].equals("Label")) {
                data[1] = data[1].replace("\"", "");
                mp1.put(data[1], new Node(data[1]));
                mp2.put(data[1], index);
                index++;
            }
        }
        int count = index;
        nodes_csv.close();

        Graph g = new Graph(count, mp1, mp2);

        BufferedReader edges_csv = new BufferedReader(new FileReader(args[1]));
        while ((row = edges_csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[0].equals("Source")) {
                data[0] = data[0].replace("\"", "");
                data[1] = data[1].replace("\"", "");
                Edge e1 = new Edge(mp1.get(data[0]), mp1.get(data[1]), Integer.parseInt(data[2]));
                Edge e2 = new Edge(mp1.get(data[1]), mp1.get(data[0]), Integer.parseInt(data[2]));
                g.addEdge(e1);
                g.addEdge(e2);
            }
        }
        edges_csv.close();

        System.out.println(args[2]);
        if (args[2].equals("average"))
            g.average();
        else if (args[2].equals("rank"))
            g.rank();
        else if (args[2].equals("independent_storylines_dfs"))
            g.independent_storylines_dfs();
        // g.average();
        // g.rank();
        // g.independent_storylines_dfs();

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
            System.out.print(nodearr[j].getId() + ",");
        }
    }

    public void dfsutil(Node v, boolean[] visited, LinkedList<Node>[] indep_story, int count) {
        visited[mp2.get(v.getId())] = true;

        indep_story[count].addFirst(v);

        Iterator<Edge> itr = adj[mp2.get(v.getId())].listIterator();
        while (itr.hasNext()) {
            Edge n = itr.next();
            if (!visited[mp2.get(n.getTarget().getId())]) {
                dfsutil(n.getTarget(), visited, indep_story, count);
            }
        }
    }

    public void independent_storylines_dfs() {
        // use mp2 for string-> int mapping
        boolean[] visited = new boolean[V];
        Iterator<Map.Entry<String, Node>> hashmapiter = mp1.entrySet().iterator();
        Node[] nodearr = new Node[V];
        int i = 0;
        while (hashmapiter.hasNext()) {
            Map.Entry<String, Node> elem = hashmapiter.next();
            nodearr[i] = elem.getValue();
            i++;
        }
        int count = 0;
        LinkedList<Node> indep_story[] = new LinkedList[V];
        for (int k = 0; k < V; k++) {
            indep_story[k] = new LinkedList<Node>();
        }

        for (int j = 0; j < V; j++) {
            if (!visited[mp2.get(nodearr[j].getId())]) {
                dfsutil(nodearr[j], visited, indep_story, count);
                count++;
            }
        }

        // sorting lexicographically
        for (int j = 0; j < count; j++) {
            int size = indep_story[j].size();
            Node[] story = new Node[size];
            int cur = 0;
            Iterator<Node> itr = indep_story[j].listIterator();
            while (itr.hasNext()) {
                story[cur] = itr.next();
                cur++;
            }
            // sort this array story in lexicographical order.
            storyCharSort(story, 0, size - 1);
            indep_story[j] = new LinkedList<Node>();
            for (int k = 0; k < size; k++) {
                indep_story[j].addLast(story[k]);
            }
        }

        // sorting based on size of linkedlist
        storySort(indep_story, 0, count - 1);

        for (int j = 0; j < count; j++) {
            Iterator<Node> itr = indep_story[j].listIterator();
            while (itr.hasNext()) {
                System.out.print(itr.next().getId() + ",");
            }
            System.out.println();
        }

        // System.out.println(count);
        // System.out.println(indep_story[0].size());
    }

    public void storyCharSortMerge(Node arr[], int l, int m, int r) {
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
            if (left[i].getId().compareTo(right[j].getId()) > 0) {
                arr[k] = left[i];
                i++;
            } else {
                arr[k] = right[j];
                j++;
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

    public void storyCharSort(Node arr[], int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            storyCharSort(arr, l, m);
            storyCharSort(arr, m + 1, r);
            storyCharSortMerge(arr, l, m, r);
        }
    }

    public void storySortMerge(LinkedList<Node> arr[], int l, int m, int r) {
        int num1 = m - l + 1;
        int num2 = r - m;

        LinkedList<Node> left[] = new LinkedList[num1];
        LinkedList<Node> right[] = new LinkedList[num2];

        for (int i = 0; i < num1; i++)
            left[i] = arr[l + i];
        for (int j = 0; j < num2; j++)
            right[j] = arr[m + j + 1];

        int i = 0;
        int j = 0;

        int k = l;
        while (i < num1 && j < num2) {
            if (left[i].size() > right[j].size()) {
                arr[k] = left[i];
                i++;
            } else if (left[i].size() < right[j].size()) {
                arr[k] = right[j];
                j++;
            } else {
                Iterator<Node> leftitr = left[i].listIterator();
                Iterator<Node> rightitr = right[j].listIterator();
                while (leftitr.hasNext() && rightitr.hasNext()) {
                    if (leftitr.next().getId().compareTo(rightitr.next().getId()) > 0) {
                        arr[k] = left[i];
                        i++;
                    } else {
                        arr[k] = right[j];
                        j++;
                    }
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

    public void storySort(LinkedList<Node> arr[], int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            storySort(arr, l, m);
            storySort(arr, m + 1, r);
            storySortMerge(arr, l, m, r);
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