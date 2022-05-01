package edu.ucsd.cse110.team56.zooseeker.path;

import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphEdge extends DefaultWeightedEdge {
    private String id = null;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "(" + getSource() + " :" + id + ": " + getTarget() + ")";
    }
}