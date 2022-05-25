package edu.ucsd.cse110.team56.zooseeker.path;

import org.jgrapht.GraphPath;

public class Path {
    public GraphPath<String, GraphEdge> path;
    public GraphVertex startInfo;
    public GraphVertex endInfo;

    Path(GraphPath<String, GraphEdge> path, GraphVertex startInfo, GraphVertex endInfo) {
        this.path = path;
        this.startInfo = startInfo;
        this.endInfo = endInfo;
    }
}
