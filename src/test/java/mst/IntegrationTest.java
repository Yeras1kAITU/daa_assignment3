package mst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class IntegrationTest {

    @Test
    void testEndToEndWithJSONFile() throws IOException {
        MSTTester tester = new MSTTester();

        // Test with the sample graph file
        tester.testAllGraphs("graphs/test_graph.json", "results/test_output.json");

        // Verify output file was created
        java.io.File outputFile = new java.io.File("results/test_output.json");
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);

        // Clean up
        outputFile.delete();
    }

    @Test
    void testCSVExport() throws IOException {
        MSTTester tester = new MSTTester();

        tester.generateCSVSummary("graphs/test_graph.json", "results/test_summary.csv");

        // Verify CSV file was created
        java.io.File csvFile = new java.io.File("results/test_summary.csv");
        assertTrue(csvFile.exists());
        assertTrue(csvFile.length() > 0);

        // Clean up
        csvFile.delete();
    }
}