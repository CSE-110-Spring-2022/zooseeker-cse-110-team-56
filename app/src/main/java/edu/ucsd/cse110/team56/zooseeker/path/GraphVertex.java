package edu.ucsd.cse110.team56.zooseeker.path;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class GraphVertex {
    NodeInfo current;
    NodeInfo child;

    public String getNavigatableId() {
        return current.id;
    }

    public GraphVertex(NodeInfo parent, NodeInfo node) {
        this.current = parent;
        this.child = node;
    }

    public GraphVertex(NodeInfo node) {
        this.current = node;
        this.child = null;
    }
}
