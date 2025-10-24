package mst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class PerformanceTest {

    @Test
    void testPerformanceOnLargeGraph() {
        // Create a larger graph for performance testing
        List<String> vertices = new ArrayList<>();
        List<Graph.Edge> edges = new ArrayList<>();

        // Create 50 vertices
        for (int i = 0; i < 50; i++) {
            vertices.add("V" + i);
        }

        // Create a connected graph with reasonable density
        Random random = new Random(42);
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                if (random.nextDouble() < 0.3) { // 30% density
                    edges.add(new Graph.Edge(vertices.get(i), vertices.get(j),
                            random.nextInt(100) + 1));
                }
            }
        }

        Graph graph = new Graph(vertices, edges);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Both should produce same cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());

        // Should have correct number of edges for MST
        assertEquals(vertices.size() - 1, primResult.getMstEdges().size());
        assertEquals(vertices.size() - 1, kruskalResult.getMstEdges().size());

        // Performance should be reasonable (less than 1 second for this size)
        assertTrue(primResult.getExecutionTimeMs() < 1000);
        assertTrue(kruskalResult.getExecutionTimeMs() < 1000);
    }

    @Test
    void testAlgorithmConsistency() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Graph.Edge> edges = Arrays.asList(
                new Graph.Edge("A", "B", 1),
                new Graph.Edge("A", "C", 4),
                new Graph.Edge("B", "C", 2),
                new Graph.Edge("B", "D", 5),
                new Graph.Edge("C", "D", 3)
        );

        Graph graph = new Graph(vertices, edges);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        // Run multiple times to ensure consistency
        for (int i = 0; i < 5; i++) {
            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());
            assertEquals(vertices.size() - 1, primResult.getMstEdges().size());
            assertEquals(vertices.size() - 1, kruskalResult.getMstEdges().size());
        }
    }
}