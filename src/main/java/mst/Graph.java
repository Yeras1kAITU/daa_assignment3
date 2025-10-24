package mst;

import java.util.List;

public class Graph {
    private List<String> vertices;
    private List<Edge> edges;

    public Graph(List<String> vertices, List<Edge> edges) {
        if (vertices == null || edges == null) {
            throw new IllegalArgumentException("Vertices and edges cannot be null");
        }
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<String> getVertices() { return vertices; }
    public List<Edge> getEdges() { return edges; }
    public int getVertexCount() { return vertices.size(); }
    public int getEdgeCount() { return edges.size(); }

    public static class Edge {
        public final String from;
        public final String to;
        public final int weight;

        public Edge(String from, String to, int weight) {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Edge vertices cannot be null");
            }
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return from + "-" + to + "(" + weight + ")";
        }
    }
}