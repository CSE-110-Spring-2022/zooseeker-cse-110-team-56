package edu.ucsd.cse110.team56.zooseeker.entity;

public class Edge {
    String source;
    String target;
    float weight;

    @Override
    public String toString() {
        return "Edge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", weight=" + weight +
                '}';
    }
}
