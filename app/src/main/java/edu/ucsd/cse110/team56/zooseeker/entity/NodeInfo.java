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

@Entity
public class NodeInfo {
    @PrimaryKey @NonNull
    public String id;

    public String name;
    public String kind;
    public List<String> tags;
}
