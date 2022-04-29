package edu.ucsd.cse110.team56.zooseeker.entity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

@Entity
public class NodeInfo {
    @PrimaryKey @NonNull
    public String id;

    public String name;
    public String kind;
    public List<String> tags;

    public NodeInfo() {

    }

    public NodeInfo(@NonNull String id, String name, String kind, List<String> tags) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.tags = tags;
    }

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