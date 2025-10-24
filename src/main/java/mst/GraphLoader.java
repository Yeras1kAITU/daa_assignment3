package mst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Graph> loadGraphsFromFile(String filename) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        JsonNode root = mapper.readTree(new File(filename));
        JsonNode graphsNode = root.get("graphs");

        if (graphsNode == null || !graphsNode.isArray()) {
            throw new IOException("Invalid JSON format: missing 'graphs' array");
        }

        for (JsonNode graphNode : graphsNode) {
            List<String> vertices = new ArrayList<>();
            List<Graph.Edge> edges = new ArrayList<>();

            // Load vertices
            JsonNode verticesNode = graphNode.get("nodes");
            if (verticesNode != null && verticesNode.isArray()) {
                for (JsonNode vertexNode : verticesNode) {
                    vertices.add(vertexNode.asText());
                }
            }

            // Load edges
            JsonNode edgesNode = graphNode.get("edges");
            if (edgesNode != null && edgesNode.isArray()) {
                for (JsonNode edgeNode : edgesNode) {
                    String from = edgeNode.get("from").asText();
                    String to = edgeNode.get("to").asText();
                    int weight = edgeNode.get("weight").asInt();
                    edges.add(new Graph.Edge(from, to, weight));
                }
            }

            graphs.add(new Graph(vertices, edges));
        }

        return graphs;
    }
}