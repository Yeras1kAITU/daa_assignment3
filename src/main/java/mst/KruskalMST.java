package mst;

import java.util.*;

public class KruskalMST {
    private int operationsCount;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;

        System.out.printf("=== KRUSKAL START: n=%d, m=%d ===%n",
                graph.getVertexCount(), graph.getEdges().size());

        long totalStart = System.nanoTime();

        List<Graph.Edge> mstEdges = new ArrayList<>();
        List<Graph.Edge> edges = graph.getEdges();

        // Phase 1: Sorting (should be O(m log m))
        long sortStart = System.nanoTime();
        List<Graph.Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(e -> e.weight));
        long sortEnd = System.nanoTime();
        double sortTime = (sortEnd - sortStart) / 1_000_000.0;

        // Phase 2: Union-Find (should be O(m α(n)))
        long ufStart = System.nanoTime();
        UnionFind uf = new UnionFind(graph.getVertices());
        int unionsPerformed = 0;

        for (Graph.Edge edge : sortedEdges) {
            if (mstEdges.size() == graph.getVertexCount() - 1) break;

            String root1 = uf.find(edge.from);
            String root2 = uf.find(edge.to);

            if (!root1.equals(root2)) {
                mstEdges.add(edge);
                uf.union(edge.from, edge.to);
                unionsPerformed++;
            }
        }
        long ufEnd = System.nanoTime();
        double ufTime = (ufEnd - ufStart) / 1_000_000.0;

        long totalEnd = System.nanoTime();
        double totalTime = (totalEnd - totalStart) / 1_000_000.0;

        System.out.printf("KRUSKAL TIMING: sort=%.2fms, uf=%.2fms, total=%.2fms, unions=%d%n",
                sortTime, ufTime, totalTime, unionsPerformed);
        System.out.printf("=== KRUSKAL END ===%n%n");

        // Calculate operations count
        operationsCount = edges.size() * (int)(Math.log(edges.size()) / Math.log(2)) // sort
                + edges.size() * 2 // find operations
                + unionsPerformed; // union operations

        int totalCost = mstEdges.stream().mapToInt(e -> e.weight).sum();

        return new MSTResult("Kruskal", mstEdges, totalCost, totalTime, operationsCount);
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
            // Path compression
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);

            if (!rootX.equals(rootY)) {
                // Union by rank
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