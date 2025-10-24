package mst;

import java.util.*;

public class PrimMST {
    private int operationsCount;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();

        List<Graph.Edge> mstEdges = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        if (graph.getVertices().isEmpty()) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult("Prim", mstEdges, 0, executionTimeMs, operationsCount);
        }

        // Start with first vertex
        String startVertex = graph.getVertices().get(0);
        visited.add(startVertex);
        operationsCount++;

        // Add all edges from start vertex to priority queue
        for (Graph.Edge edge : graph.getEdges()) {
            operationsCount++;
            if (edge.from.equals(startVertex) || edge.to.equals(startVertex)) {
                pq.offer(edge);
                operationsCount++;
            }
        }

        while (!pq.isEmpty() && visited.size() < graph.getVertexCount()) {
            Graph.Edge minEdge = pq.poll();
            operationsCount++;

            String nextVertex = null;
            operationsCount++;
            if (!visited.contains(minEdge.from)) {
                nextVertex = minEdge.from;
            } else if (!visited.contains(minEdge.to)) {
                nextVertex = minEdge.to;
            }

            if (nextVertex != null) {
                visited.add(nextVertex);
                mstEdges.add(minEdge);
                operationsCount += 2;

                // Add all edges from the new vertex
                for (Graph.Edge edge : graph.getEdges()) {
                    operationsCount++;
                    if ((edge.from.equals(nextVertex) && !visited.contains(edge.to)) ||
                            (edge.to.equals(nextVertex) && !visited.contains(edge.from))) {
                        pq.offer(edge);
                        operationsCount++;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        int totalCost = mstEdges.stream().mapToInt(e -> e.weight).sum();

        return new MSTResult("Prim", mstEdges, totalCost, executionTimeMs, operationsCount);
    }
}