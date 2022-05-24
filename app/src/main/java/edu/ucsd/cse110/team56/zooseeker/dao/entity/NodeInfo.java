package edu.ucsd.cse110.team56.zooseeker.dao.entity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;

@Entity
public class NodeInfo {
    public enum Kind {
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION,
        @SerializedName("exhibit_group") EXHIBIT_GROUP
    }

    public enum Status {
        @SerializedName("loaded") LOADED, // default state when the exhibit is not selected
        @SerializedName("added") ADDED, // after the user has selected the exhibit
        @SerializedName("visited") VISITED, // after the user has visited the exhibit
        @SerializedName("skipped") SKIPPED // after the user has skipped the exhibit
    }

    @PrimaryKey @NonNull
    public String id;
    public String name;
    public Kind kind;
    public Status status = Status.LOADED;
    @SerializedName("parent_id")
    public String parentId;
    @SerializedName("lng")
    public double longitude;
    @SerializedName("lat")
    public double latitude;

    public Status getStatus() {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public List<String> tags;

    public NodeInfo() { }

    public String getId(){ return id;}

    public String getName() {
        return name;
    }

    public Optional<Location> getLocation() {
        if (this.longitude == 0 && this.latitude == 0) {
            return Optional.empty();
        }
        Location location = new Location("");
        location.setLongitude(this.longitude);
        location.setLatitude(this.latitude);
        return Optional.of(location);
    }

    public NodeInfo(@NonNull String id, String name, Kind kind, List<String> tags) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.tags = tags;
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

    @Override
    public String toString() {
        return name + " " + tags.stream().collect(Collectors.joining(" "));
    }
}
