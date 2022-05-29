package edu.ucsd.cse110.team56.zooseeker.path;

import java.util.ArrayList;
import java.util.List;

public class CombinedGraphEdge extends GraphEdge {
    List<GraphEdge> edges = new ArrayList<>();

    public CombinedGraphEdge(GraphEdge edge) {
        super();
        this.edges.add(edge);
    }

    public void addEdge(GraphEdge edge) {
        this.edges.add(edge);
    }

    @Override
    public String getId() {
        return this.edges.get(0).getId();
    }

    public String getDestination() {
        return (String) this.edges.get(this.edges.size() - 1).getDestination();
    }

    @Override
    public double getLength() {
        return edges.stream().mapToDouble(GraphEdge::getLength).sum();
    }
}
