package edu.ucsd.cse110.team56.zooseeker.dao.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;

@Entity
public class NodeInfo {
    @PrimaryKey @NonNull
    public String id;
    public String name;
    public String kind;
    public boolean added;

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public List<String> tags;

    public NodeInfo() { }

    public String getId(){ return id;}

    public String getName() {
        return name;
    }

    public NodeInfo(@NonNull String id, String name, String kind, List<String> tags) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.tags = tags;
        this.added = false;
    }

    /**
     * two NodeInfo objects are equal if their `id`, `name`, `kind`, and `tags` are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return id.equals(nodeInfo.id) && Objects.equals(name, nodeInfo.name) && Objects.equals(kind, nodeInfo.kind) && Objects.equals(tags, nodeInfo.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, kind, tags);
    }
}
