import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Driver {

    static final String[] cases = new String[] { "small", "large", "dfs" };
    static final long SEED = 12199;
    static String inDir = "inputs/";
    static String outDir = "outputs/";
    static String nodesFile = "GeneratedNodes.csv";
    static String egdesFile = "GeneratedEdges.csv";

    static String name;

    public static void main(String[] args) throws FileNotFoundException {
        System.out.print("Enter your name: ");
        Scanner sc = new Scanner(System.in);
        GraphGenerator gen = new GraphGenerator();
        name = sc.nextLine();
        System.out.print("\n");

        for (String s : cases) {
            gen.buildGraph(s);
        }
        for (String s : cases) {
            runCode(s);
        }
    }

    static void runCode(String type) throws FileNotFoundException {
        long avgTime;
        long rankTime;
        long dfsTime;

        File file = new File(outDir + name + "_" + type + "_" + "out.txt");
        file.getParentFile().mkdirs();
        PrintStream console = System.out;
        System.out.println("Running testcases \"" + type + "\"...");
        PrintStream ps = new PrintStream(file);
        System.setOut(ps);
        String nodes = inDir + type + nodesFile;
        String edges = inDir + type + egdesFile;

        avgTime = runFunction("average", nodes, edges);
        rankTime = runFunction("rank", nodes, edges);
        dfsTime = runFunction("independent_storylines_dfs", nodes, edges);

        System.setOut(console);
        System.out.println("Time taken to run average is: " + avgTime / 1000000 + "ms");
        System.out.println("Time taken to run rank is: " + rankTime / 1000000 + "ms");
        System.out.println("Time taken to run independant_storylines_dfs is: " + dfsTime / 1000000 + "ms");
        System.out.println("Output stored in " + name + "_" + type + "_" + "out.txt");
        System.out.print("\n");
    }

    static long runFunction(String func, String nodes, String edges) throws FileNotFoundException {
        long start;
        long end;
        System.out.println(func);
        start = System.nanoTime();
        try {
            assignment4.main(new String[] { nodes, edges, func });
        } catch (Exception e) {
        }
        end = System.nanoTime() - start;
        return end;
    }
}

class GraphGenerator {
    Random rand;
    int numV;
    int numE;
    int maxWeight = 500;

    GraphGenerator() {
        rand = new Random();
        rand.setSeed(Driver.SEED);
    }

    void buildGraph(String type) {
        if (type.compareTo("dfs") == 0) {
            generateDfs();
        } else {
            switch (type) {
                case "small":
                    numV = 1000;
                    numE = (numV * (numV - 1) / 4) + rand.nextInt(numV * (numV - 1) / 4);
                    break;
                case "large":
                    numV = 100000;
                    numE = (int) (numV * 1.5) + rand.nextInt(numV / 2);
                    break;
                default:
                    break;
            }
            generate(type);
        }
    }

    void generateDfs() {
        int numComponents = 20 + rand.nextInt(10);
        int sum = 0;
        System.out.println("Generating \"dfs\" testcase...");
        for (int i = 0; i < numComponents; i++) {
            numV = 300 + rand.nextInt(200);
            numE = (numV * (numV - 1) / 4) + rand.nextInt(numV * (numV - 1) / 4);
            makeNodesCSV("dfs", i == 0, sum);
            makeEdgesCSV("dfs", i == 0, sum);
            sum += numV;
        }
        System.out.println("Nodes file created");
        System.out.println("Edges file created\n");
    }

    void generate(String type) {
        System.out.println("Generating \"" + type + "\" testcase...");
        makeNodesCSV(type, true, 0);
        System.out.println("Nodes file created");
        makeEdgesCSV(type, true, 0);
        System.out.println("Edges file created\n");
    }

    private void makeEdgesCSV(String type, boolean newFile, int base) {
        File file = new File(Driver.inDir + type + Driver.egdesFile);
        file.getParentFile().mkdirs();
        try (BufferedWriter out = new BufferedWriter(
                new FileWriter(Driver.inDir + type + Driver.egdesFile, !newFile))) {
            if (newFile)
                out.write("Source,Target,Weight\n");
            HashSet<String> set = new HashSet<>();
            for (int i = 0; i < numE; i++) {
                int a = base + rand.nextInt(numV);
                int b = base + rand.nextInt(numV);
                while (a == b) {
                    b = base + rand.nextInt(numV);
                }
                while (set.contains(a + "," + b) || set.contains(b + "," + a)) {
                    a = base + rand.nextInt(numV);
                    b = base + rand.nextInt(numV);
                    while (a == b) {
                        b = base + rand.nextInt(numV);
                    }
                }
                set.add(a + "," + b);
                int wt = 1 + rand.nextInt(maxWeight);
                out.write(a + "," + b + "," + wt + "\n");
            }
        } catch (IOException e) {
            System.out.println("Exception Occurred" + e);
        }
    }

    private void makeNodesCSV(String type, boolean newFile, int base) {
        File file = new File(Driver.inDir + type + Driver.nodesFile);
        file.getParentFile().mkdirs();
        try (BufferedWriter out = new BufferedWriter(
                new FileWriter(Driver.inDir + type + Driver.nodesFile, !newFile))) {
            if (newFile)
                out.write("Id,Label\n");
            for (int i = 0; i < numV; i++) {
                int node = base + i;
                out.write(node + "," + node + "\n");
            }
        } catch (IOException e) {
            System.out.println("Exception Occurred" + e);
        }
    }
}
