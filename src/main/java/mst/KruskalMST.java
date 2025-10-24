package mst;

import java.util.*;

public class KruskalMST {

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationsCount = 0;

        List<Graph.Edge> mstEdges = new ArrayList<>();
        List<Graph.Edge> sortedEdges = new ArrayList<>(graph.getEdges());

        // Sort edges by weight
        sortedEdges.sort(Comparator.comparingInt(e -> e.weight));
        operationsCount += sortedEdges.size() * (int)(Math.log(sortedEdges.size()) / Math.log(2));

        UnionFind uf = new UnionFind(graph.getVertices());

        for (Graph.Edge edge : sortedEdges) {
            operationsCount++;
            if (mstEdges.size() == graph.getVertexCount() - 1) {
                break;
            }

            String root1 = uf.find(edge.from);
            String root2 = uf.find(edge.to);
            operationsCount += 2;

            operationsCount++;
            if (!root1.equals(root2)) {
                mstEdges.add(edge);
                uf.union(edge.from, edge.to);
                operationsCount++;
            }
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        int totalCost = mstEdges.stream().mapToInt(e -> e.weight).sum();

        return new MSTResult("Kruskal", mstEdges, totalCost, executionTimeMs, operationsCount);
    }

    private static class UnionFind {
        private Map<String, String> parent;
        private Map<String, Integer> rank;

        public UnionFind(List<String> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (String vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        public String find(String x) {
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);

            if (!rootX.equals(rootY)) {
                if (rank.get(rootX) < rank.get(rootY)) {
                    parent.put(rootX, rootY);
                } else if (rank.get(rootX) > rank.get(rootY)) {
                    parent.put(rootY, rootX);
                } else {
                    parent.put(rootY, rootX);
                    rank.put(rootX, rank.get(rootX) + 1);
                }
            }
        }
    }
}