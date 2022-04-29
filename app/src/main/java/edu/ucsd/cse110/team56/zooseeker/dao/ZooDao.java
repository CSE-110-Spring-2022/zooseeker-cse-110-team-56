package edu.ucsd.cse110.team56.zooseeker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.entity.NodeInfo;

@Dao
public interface ZooDao {
    @Query("SELECT * FROM `NodeInfo` ORDER BY `id`")
    List<NodeInfo> getAllNodes();

    @Insert
    void addNodes(List<NodeInfo> nodes);

    @Query("SELECT * FROM `EdgeInfo` ORDER BY `id`")
    List<EdgeInfo> getAllEdges();

    @Insert
    void addEdges(List<EdgeInfo> edges);
}
