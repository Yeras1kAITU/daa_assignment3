package mst;

import java.util.List;

public class MSTResult {
    private String algorithm;
    private List<Graph.Edge> mstEdges;
    private int totalCost;
    private double executionTimeMs;
    private int operationsCount;

    public MSTResult(String algorithm, List<Graph.Edge> mstEdges, int totalCost,
                     double executionTimeMs, int operationsCount) {
        this.algorithm = algorithm;
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.executionTimeMs = executionTimeMs;
        this.operationsCount = operationsCount;
    }

    // Getters
    public String getAlgorithm() { return algorithm; }
    public List<Graph.Edge> getMstEdges() { return mstEdges; }
    public int getTotalCost() { return totalCost; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public int getOperationsCount() { return operationsCount; }
}