package edu.ucsd.cse110.team56.zooseeker.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.Objects;

@Entity
public class EdgeInfo {
    @PrimaryKey @NonNull
    public String id;
    public String street;

    public EdgeInfo() {

    }

    public EdgeInfo(@NonNull String id, String street) {
        this.id = id;
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeInfo edgeInfo = (EdgeInfo) o;
        return id.equals(edgeInfo.id) && Objects.equals(street, edgeInfo.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street);
    }
}
