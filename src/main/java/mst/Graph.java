package mst;

import java.util.*;

public class Graph {
    private List<String> vertices;
    private List<Edge> edges;
    private Map<String, List<Edge>> adjacencyList;

    public Graph(List<String> vertices, List<Edge> edges) {
        if (vertices == null || edges == null) {
            throw new IllegalArgumentException("Vertices and edges cannot be null");
        }
        this.vertices = vertices;
        this.edges = edges;
        this.adjacencyList = buildAdjacencyList();
    }

    public List<String> getVertices() { return vertices; }
    public List<Edge> getEdges() { return edges; }
    public int getVertexCount() { return vertices.size(); }
    public int getEdgeCount() { return edges.size(); }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    private Map<String, List<Edge>> buildAdjacencyList() {
        Map<String, List<Edge>> adjList = new HashMap<>();

        for (Edge edge : edges) {
            // Add edge to from-vertex's list
            adjList.computeIfAbsent(edge.from, k -> new ArrayList<>()).add(edge);
            // Add edge to to-vertex's list (since graph is undirected)
            adjList.computeIfAbsent(edge.to, k -> new ArrayList<>()).add(edge);
        }

        return adjList;
    }

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