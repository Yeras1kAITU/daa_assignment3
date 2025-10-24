package mst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Graph> loadGraphsFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("File not found: " + filename);
        }

        List<Graph> graphs = new ArrayList<>();
        JsonNode root = mapper.readTree(file);
        JsonNode graphsNode = root.get("graphs");

        if (graphsNode == null || !graphsNode.isArray()) {
            throw new IOException("Invalid JSON format: missing 'graphs' array");
        }

        for (JsonNode graphNode : graphsNode) {
            List<String> vertices = new ArrayList<>();
            List<Graph.Edge> edges = new ArrayList<>();

            // Load vertices
            JsonNode verticesNode = graphNode.get("nodes");
            if (verticesNode == null || !verticesNode.isArray()) {
                throw new IOException("Invalid graph format: missing 'nodes' array");
            }

            Set<String> uniqueVertices = new HashSet<>();
            for (JsonNode vertexNode : verticesNode) {
                String vertex = vertexNode.asText();
                if (uniqueVertices.contains(vertex)) {
                    throw new IOException("Duplicate vertex found: " + vertex);
                }
                uniqueVertices.add(vertex);
                vertices.add(vertex);
            }

            // Load edges
            JsonNode edgesNode = graphNode.get("edges");
            if (edgesNode == null || !edgesNode.isArray()) {
                throw new IOException("Invalid graph format: missing 'edges' array");
            }

            Set<String> uniqueEdges = new HashSet<>();
            for (JsonNode edgeNode : edgesNode) {
                if (!edgeNode.has("from") || !edgeNode.has("to") || !edgeNode.has("weight")) {
                    throw new IOException("Invalid edge format: missing required fields");
                }

                String from = edgeNode.get("from").asText();
                String to = edgeNode.get("to").asText();
                int weight = edgeNode.get("weight").asInt();

                // Validate vertices exist
                if (!vertices.contains(from) || !vertices.contains(to)) {
                    throw new IOException("Edge references non-existent vertex: " + from + "-" + to);
                }

                // Check for duplicate edges
                String edgeKey1 = from + "-" + to;
                String edgeKey2 = to + "-" + from;
                if (uniqueEdges.contains(edgeKey1) || uniqueEdges.contains(edgeKey2)) {
                    throw new IOException("Duplicate edge found: " + from + "-" + to);
                }

                uniqueEdges.add(edgeKey1);
                edges.add(new Graph.Edge(from, to, weight));
            }

            graphs.add(new Graph(vertices, edges));
        }

        return graphs;
    }
}