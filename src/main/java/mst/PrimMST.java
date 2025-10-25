package mst;

import java.util.*;

public class PrimMST {
    private int operationsCount;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();

        List<String> vertices = graph.getVertices();
        int n = vertices.size();

        // Early return for empty graph
        if (n == 0) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult("Prim", new ArrayList<>(), 0, executionTimeMs, operationsCount);
        }

        // 🔹 Map vertex name → index (so we can use arrays instead of HashMaps)
        Map<String, Integer> vertexIndex = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            vertexIndex.put(vertices.get(i), i);
        }

        boolean[] visited = new boolean[n];
        int[] minEdgeWeight = new int[n];
        Graph.Edge[] minEdge = new Graph.Edge[n];
        Arrays.fill(minEdgeWeight, Integer.MAX_VALUE);

        // 🔹 Use lightweight Node for PQ
        class Node implements Comparable<Node> {
            int index, weight;
            Node(int index, int weight) { this.index = index; this.weight = weight; }
            @Override
            public int compareTo(Node o) { return Integer.compare(this.weight, o.weight); }
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Start from first vertex
        minEdgeWeight[0] = 0;
        pq.offer(new Node(0, 0));

        List<Graph.Edge> mstEdges = new ArrayList<>(n - 1);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.index;
            operationsCount++;

            if (visited[u]) continue;
            visited[u] = true;

            // Add edge to MST (skip the start vertex)
            if (minEdge[u] != null) {
                mstEdges.add(minEdge[u]);
                operationsCount++;
            }

            // 🔹 Use adjacency list directly
            List<Graph.Edge> neighbors = graph.getAdjacencyList().get(vertices.get(u));
            if (neighbors == null) continue;

            for (Graph.Edge edge : neighbors) {
                operationsCount++;

                // Determine neighbor index
                String neighborName = edge.from.equals(vertices.get(u)) ? edge.to : edge.from;
                Integer v = vertexIndex.get(neighborName);
                if (v == null) continue;

                if (!visited[v] && edge.weight < minEdgeWeight[v]) {
                    minEdgeWeight[v] = edge.weight;
                    minEdge[v] = edge;
                    pq.offer(new Node(v, edge.weight));
                    operationsCount += 3;
                }
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        // Manual cost sum (faster than stream)
        int totalCost = 0;
        for (Graph.Edge e : mstEdges) totalCost += e.weight;

        return new MSTResult("Prim", mstEdges, totalCost, executionTimeMs, operationsCount);
    }
}
