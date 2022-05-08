package edu.ucsd.cse110.team56.zooseeker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.ucsd.cse110.team56.zooseeker.dao.entity.EdgeInfo;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;

@Dao
public interface ZooDao {
    @Query("SELECT * FROM `NodeInfo` ORDER BY `id`")
    List<NodeInfo> getAllNodes();

    @Query("SELECT * FROM `NodeInfo` WHERE `id` = :id")
    NodeInfo getNode(String id);

    @Insert
    void addNodes(List<NodeInfo> nodes);

    @Update
    void updateNode(NodeInfo nodes);

    @Query("SELECT * FROM `EdgeInfo` ORDER BY `id`")
    List<EdgeInfo> getAllEdges();

    @Query("SELECT * FROM `EdgeInfo` WHERE `id` = :id")
    EdgeInfo getEdge(String id);

    @Insert
    void addEdges(List<EdgeInfo> edges);
}
