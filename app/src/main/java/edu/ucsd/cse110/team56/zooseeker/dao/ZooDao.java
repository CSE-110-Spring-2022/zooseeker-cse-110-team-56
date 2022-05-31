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
    /**
     * Get all nodes inside the zoo
     * @return a list of nodes
     */
    @Query("SELECT * FROM `NodeInfo` ORDER BY `id`")
    List<NodeInfo> getAllNodes();

    /**
     * Get all nodes with a specific kind
     * @param kind kind of the node
     * @return a list of nodes
     */
    @Query("SELECT * FROM `NodeInfo` WHERE kind = :kind ORDER BY `id`")
    List<NodeInfo> getNodesWithKind(NodeInfo.Kind kind);

    /**
     * Get all nodes with a specific status
     * @return a list of nodes
     */
    @Query("SELECT * FROM `NodeInfo` WHERE status IN (:status) ORDER BY `order` ASC")
    List<NodeInfo> getNodesWithStatus(List<NodeInfo.Status> status);

    /**
     * Get a single node inside the zoo using its ID
     * @param id id of the node
     * @return node
     */
    @Query("SELECT * FROM `NodeInfo` WHERE `id` = :id")
    NodeInfo getNode(String id);

    @Query("SELECT * FROM `NodeInfo` WHERE `id` = :parentId")
    NodeInfo getParentNode(String parentId);

    @Query("SELECT * FROM `NodeInfo` WHERE `parentId` = :id")
    List<NodeInfo> getChildNodes(String id);

    /**
     * Add a list of nodes to the zoo
     * @param nodes list of nodes to be added
     */
    @Insert
    void addNodes(List<NodeInfo> nodes);

    /**
     * Update the nodes inside the zoo
     * @param nodes nodes to be updated
     */
    @Update
    void updateNode(NodeInfo nodes);

    /**
     * Get all edges inside the zoo
     * @return list of all edges
     */
    @Query("SELECT * FROM `EdgeInfo` ORDER BY `id`")
    List<EdgeInfo> getAllEdges();

    /**
     * Get an edge using its id
     * @param id ID of the edge
     * @return edge
     */
    @Query("SELECT * FROM `EdgeInfo` WHERE `id` = :id")
    EdgeInfo getEdge(String id);

    /**
     * Add a list of edges to the zoo
     * @param edges edges to be added
     */
    @Insert
    void addEdges(List<EdgeInfo> edges);
}
