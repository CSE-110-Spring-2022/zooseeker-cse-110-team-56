package edu.ucsd.cse110.team56.zooseeker.entity;

import java.util.List;

public class Exhibit {
    String id;
    String itemType;
    List<String> tags;

    @Override
    public String toString() {
        return "Exhibit{" +
                "id='" + id + '\'' +
                ", itemType='" + itemType + '\'' +
                ", tags=" + tags +
                '}';
    }
}
