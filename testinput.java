import java.io.*;

public class testinput {
    public static void main(String[] args) throws Exception {
        BufferedReader csv = new BufferedReader(new FileReader("nodes.csv"));
        String row;
        int count = 0;
        while ((row = csv.readLine()) != null) {
            String[] data = row.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");
            if (!data[1].equals("Label")) {
                System.out.println(data[1]);
                count++;
            }
        }
        System.out.println(count);
        csv.close();
    }
}
