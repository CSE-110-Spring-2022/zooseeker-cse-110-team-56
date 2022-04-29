package edu.ucsd.cse110.team56.zooseeker.entity;

import java.util.List;

public class Graph {
    class Node {
        String id;
    }

    class Edge {
        String id;
        String street;
    }

    List<Node> nodes;
    List<Edge> edges;

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                '}';
    }
}
