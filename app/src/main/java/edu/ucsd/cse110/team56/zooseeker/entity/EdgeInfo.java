package edu.ucsd.cse110.team56.zooseeker.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class EdgeInfo {
    @PrimaryKey @NonNull
    public String id;
    public String street;
}
