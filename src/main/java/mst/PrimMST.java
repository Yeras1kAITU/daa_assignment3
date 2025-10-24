package mst;

import java.util.*;

public class PrimMST {
    private int operationsCount;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();

        List<Graph.Edge> mstEdges = new ArrayList<>();
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Integer> minEdgeWeight = new HashMap<>();
        Map<String, Graph.Edge> minEdge = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingInt(v -> minEdgeWeight.getOrDefault(v, Integer.MAX_VALUE))
        );

        if (graph.getVertices().isEmpty()) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult("Prim", mstEdges, 0, executionTimeMs, operationsCount);
        }

        // Initialize data structures
        for (String vertex : graph.getVertices()) {
            visited.put(vertex, false);
            minEdgeWeight.put(vertex, Integer.MAX_VALUE);
            operationsCount += 2;
        }

        // Start with first vertex
        String startVertex = graph.getVertices().get(0);
        minEdgeWeight.put(startVertex, 0);
        pq.offer(startVertex);
        operationsCount += 2;

        while (!pq.isEmpty()) {
            String currentVertex = pq.poll();
            operationsCount++;

            if (visited.get(currentVertex)) {
                continue;
            }

            visited.put(currentVertex, true);
            operationsCount++;

            // Add the minimum edge to MST (except for the start vertex)
            Graph.Edge edgeToAdd = minEdge.get(currentVertex);
            if (edgeToAdd != null) {
                mstEdges.add(edgeToAdd);
                operationsCount++;
            }

            // Update neighbors using adjacency list (CRITICAL OPTIMIZATION)
            for (Graph.Edge edge : graph.getAdjacencyList().getOrDefault(currentVertex, new ArrayList<>())) {
                operationsCount++;

                String neighbor = edge.from.equals(currentVertex) ? edge.to : edge.from;

                if (!visited.get(neighbor) && edge.weight < minEdgeWeight.get(neighbor)) {
                    minEdgeWeight.put(neighbor, edge.weight);
                    minEdge.put(neighbor, edge);
                    operationsCount += 2;

                    // Instead of decrease-key, we add duplicate (more efficient in Java's PQ)
                    pq.offer(neighbor);
                    operationsCount++;
                }
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        int totalCost = mstEdges.stream().mapToInt(e -> e.weight).sum();

        return new MSTResult("Prim", mstEdges, totalCost, executionTimeMs, operationsCount);
    }
}