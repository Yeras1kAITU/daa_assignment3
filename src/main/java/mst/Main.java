package mst;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            MSTTester tester = new MSTTester();

            // Test with sample graph
            tester.testGraphs("graphs/test_graph.json", "results/output.json");

            System.out.println("MST algorithms test completed successfully!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}