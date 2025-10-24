package mst;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MSTTester {
    private final PrimMST prim;
    private final KruskalMST kruskal;
    private final ObjectMapper mapper;

    public MSTTester() {
        this.prim = new PrimMST();
        this.kruskal = new KruskalMST();
        this.mapper = new ObjectMapper();
    }

    public void testGraphs(String inputFile, String outputFile) throws IOException {
        List<Graph> graphs = GraphLoader.loadGraphsFromFile(inputFile);
        ArrayNode results = mapper.createArrayNode();

        for (int i = 0; i < graphs.size(); i++) {
            Graph graph = graphs.get(i);

            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            ObjectNode resultNode = mapper.createObjectNode();
            resultNode.put("graph_id", i + 1);

            // Input stats
            ObjectNode inputStats = mapper.createObjectNode();
            inputStats.put("vertices", graph.getVertexCount());
            inputStats.put("edges", graph.getEdgeCount());
            resultNode.set("input_stats", inputStats);

            // Prim results
            ObjectNode primNode = mapper.createObjectNode();
            primNode.set("mst_edges", createEdgesArray(primResult.getMstEdges()));
            primNode.put("total_cost", primResult.getTotalCost());
            primNode.put("operations_count", primResult.getOperationsCount());
            primNode.put("execution_time_ms", primResult.getExecutionTimeMs());
            resultNode.set("prim", primNode);

            // Kruskal results
            ObjectNode kruskalNode = mapper.createObjectNode();
            kruskalNode.set("mst_edges", createEdgesArray(kruskalResult.getMstEdges()));
            kruskalNode.put("total_cost", kruskalResult.getTotalCost());
            kruskalNode.put("operations_count", kruskalResult.getOperationsCount());
            kruskalNode.put("execution_time_ms", kruskalResult.getExecutionTimeMs());
            resultNode.set("kruskal", kruskalNode);

            results.add(resultNode);
        }

        ObjectNode output = mapper.createObjectNode();
        output.set("results", results);

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), output);
        System.out.println("Results saved to: " + outputFile);
    }

    private ArrayNode createEdgesArray(List<Graph.Edge> edges) {
        ArrayNode edgesArray = mapper.createArrayNode();
        for (Graph.Edge edge : edges) {
            ObjectNode edgeNode = mapper.createObjectNode();
            edgeNode.put("from", edge.from);
            edgeNode.put("to", edge.to);
            edgeNode.put("weight", edge.weight);
            edgesArray.add(edgeNode);
        }
        return edgesArray;
    }
}