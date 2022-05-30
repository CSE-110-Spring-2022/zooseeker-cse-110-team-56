package edu.ucsd.cse110.team56.zooseeker.path;

import org.jgrapht.GraphPath;

import java.util.HashSet;
import java.util.Set;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

public class Path {
    public GraphPath<String, GraphEdge> path;
    public GraphVertex startInfo;
    public GraphVertex endInfo;

    Path(GraphPath<String, GraphEdge> path, GraphVertex startInfo, GraphVertex endInfo) {
        this.path = path;
        this.startInfo = startInfo;
        this.endInfo = endInfo;
    }

    public Set<String> nodesSet() {
        HashSet<String> info = new HashSet<>();
        for (GraphEdge edge: path.getEdgeList()) {
            info.add(edge.getDestination());
            info.add(edge.getOrigin());
        }
        return info;
    }
}
