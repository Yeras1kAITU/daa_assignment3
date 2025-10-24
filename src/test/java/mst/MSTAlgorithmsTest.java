package mst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class MSTAlgorithmsTest {

    @Test
    void testBothAlgorithmsProduceSameCost() {
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

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());
    }

    @Test
    void testMSTHasCorrectEdgeCount() {
        List<String> vertices = Arrays.asList("A", "B", "C");
        List<Graph.Edge> edges = Arrays.asList(
                new Graph.Edge("A", "B", 1),
                new Graph.Edge("B", "C", 2),
                new Graph.Edge("A", "C", 3)
        );

        Graph graph = new Graph(vertices, edges);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertEquals(vertices.size() - 1, primResult.getMstEdges().size());
        assertEquals(vertices.size() - 1, kruskalResult.getMstEdges().size());
    }

    @Test
    void testSingleVertexGraph() {
        List<String> vertices = Collections.singletonList("A");
        List<Graph.Edge> edges = Collections.emptyList();

        Graph graph = new Graph(vertices, edges);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertEquals(0, primResult.getTotalCost());
        assertEquals(0, kruskalResult.getTotalCost());
        assertEquals(0, primResult.getMstEdges().size());
    }

    @Test
    void testPerformanceMetricsAreNonNegative() {
        List<String> vertices = Arrays.asList("A", "B", "C");
        List<Graph.Edge> edges = Arrays.asList(
                new Graph.Edge("A", "B", 1),
                new Graph.Edge("B", "C", 2)
        );

        Graph graph = new Graph(vertices, edges);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertTrue(primResult.getExecutionTimeMs() >= 0);
        assertTrue(kruskalResult.getExecutionTimeMs() >= 0);
        assertTrue(primResult.getOperationsCount() >= 0);
        assertTrue(kruskalResult.getOperationsCount() >= 0);
    }
}