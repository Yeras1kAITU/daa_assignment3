package mst;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            MSTTester tester = new MSTTester();

            // Test all graph categories
            String[] graphFiles = {
                    "graphs/small_dense_graphs.json",
                    "graphs/medium_dense_graphs.json",
                    "graphs/large_dense_graphs.json",
                    "graphs/extra_large_dense_graphs.json"
            };

            String[] outputFiles = {
                    "results/small_results.json",
                    "results/medium_results.json",
                    "results/large_results.json",
                    "results/extra_large_results.json"
            };

            // Test individual categories
            for (int i = 0; i < graphFiles.length; i++) {
                System.out.println("=== Testing " + graphFiles[i] + " ===");
                tester.testAllGraphs(graphFiles[i], outputFiles[i]);
                tester.generateCSVSummary(graphFiles[i],
                        outputFiles[i].replace(".json", "_summary.csv"));
            }

            System.out.println("All tests completed successfully!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}