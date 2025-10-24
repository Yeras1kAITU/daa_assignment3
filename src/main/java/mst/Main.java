package mst;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== MST Algorithms Comparison ===");
            System.out.println("Starting performance analysis...");

            MSTTester tester = new MSTTester();

            // Test all graph categories
            String[] graphFiles = {
                    "src/main/java/graphs/small_dense_graphs.json",
                    "src/main/java/graphs/medium_dense_graphs.json",
                    "src/main/java/graphs/large_dense_graphs.json",
                    "src/main/java/graphs/extra_large_dense_graphs.json"
            };

            String[] outputFiles = {
                    "src/main/java/results/small_results.json",
                    "src/main/java/results/medium_results.json",
                    "src/main/java/results/large_results.json",
                    "src/main/java/results/extra_large_results.json"
            };

            // Ensure results directory exists
            createDirectory("results");
            createDirectory("graphs");

            long totalStartTime = System.nanoTime();

            // Test individual categories
            for (int i = 0; i < graphFiles.length; i++) {
                System.out.println("\n=== Testing " + graphFiles[i] + " ===");
                long categoryStartTime = System.nanoTime();

                // Check if input file exists
                if (!new java.io.File(graphFiles[i]).exists()) {
                    System.err.println("❌ Input file not found: " + graphFiles[i]);
                    continue;
                }

                tester.testAllGraphs(graphFiles[i], outputFiles[i]);
                tester.generateCSVSummary(graphFiles[i],
                        outputFiles[i].replace(".json", "_summary.csv"));

                long categoryTime = (System.nanoTime() - categoryStartTime) / 1_000_000;
                System.out.println("✓ Category completed in " + categoryTime + "ms");
            }

            long totalTime = (System.nanoTime() - totalStartTime) / 1_000_000;
            System.out.println("\n=== All tests completed in " + totalTime + "ms ===");
            System.out.println("✓ Results saved to 'results/' directory");
            System.out.println("Check generated JSON and CSV files for detailed analysis");

        } catch (IOException e) {
            System.err.println("❌ Error during execution: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createDirectory(String dirName) {
        java.io.File dir = new java.io.File(dirName);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("✓ Created directory: " + dirName);
            } else {
                System.err.println("❌ Failed to create directory: " + dirName);
            }
        }
    }
}