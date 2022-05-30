package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.team56.zooseeker.dao.ZooDao;
import edu.ucsd.cse110.team56.zooseeker.dao.ZooDatabase;
import edu.ucsd.cse110.team56.zooseeker.dao.entity.NodeInfo;
import edu.ucsd.cse110.team56.zooseeker.path.Graph;
import edu.ucsd.cse110.team56.zooseeker.path.GraphVertex;
import edu.ucsd.cse110.team56.zooseeker.path.Path;

/**
 * A container of static methods for managing the state of `NodeInfo` lists
 */
public class ExhibitsManager {
    private static ExhibitsManager singleton = null;
    private Context context;
    private ZooDao dao() {
        return ZooDatabase.getSingleton(context).zooDao();
    }

    private ExhibitsManager(Context context) {
        this.context = context;
    }

    public synchronized static ExhibitsManager getSingleton(Context context) {
        if (singleton == null) {
            singleton = new ExhibitsManager(context);
        }
        return singleton;
    }

    // ------------- when user in en route -----------------

    /**
     * Get the path to the next exhibit. It does not replan.
     */
    public Path getPathToNextExhibit(NodeInfo current) {
        if (current == null) {
            current = getLastVisitedNode();
        }
        return Graph.load(context).findPath(getGraphVertex(current), getGraphVertex(getNextNode()));
    }

    /**
     * Shuffle the exhibits that haven't been visited by the user to minimize the total distance.
     */
    public void plan(NodeInfo current) {
        List<NodeInfo> toBeVisited = getAddedList();
        NodeInfo lastNode = getLastVisitedNode();
        NodeInfo gate = getGate();

        if (current == null) {
            current = lastNode == null ? gate : lastNode;
        }

        long idx = lastNode != null ? lastNode.order + 1 : 0;
        ArrayList<Path> paths = Graph.load(context).findPaths(getGraphVertex(current), getNavigationVertexIds(toBeVisited), getGraphVertex(getGate()));

        for(Path path: paths) {
            NodeInfo info = path.endInfo.getActualExhibit();
            info.order = idx;
            ++idx;

            dao().updateNode(info);
        }
    }

    public void next() {
        NodeInfo node = getNextNode();
        node.status = NodeInfo.Status.VISITED;
        dao().updateNode(node);
    }

    public boolean canSkip() {
        return getAddedList().size() >= 1;
    }

    public void skip() {
        NodeInfo node = getNextNode();
        node.status = NodeInfo.Status.LOADED;
        dao().updateNode(node);
    }

    public boolean canStepBack() {
        return getLastVisitedNode() != null;
    }

    public void stepBack() {
        NodeInfo node = getLastVisitedNode();
        node.status = NodeInfo.Status.ADDED;
        dao().updateNode(node);
    }

    public List<Pair<NodeInfo, Double>> getPlan() {
        List<NodeInfo> list = dao().getNodesWithStatus(List.of(NodeInfo.Status.ADDED, NodeInfo.Status.VISITED));
        List<Pair<NodeInfo, Double>> plan = new ArrayList<>();

        Graph graph = Graph.load(context);
        GraphVertex current = getGraphVertex(getGate());

        for(NodeInfo node: list) {
            Path path = graph.findPath(current, getGraphVertex(node));
            plan.add(new Pair<>(node, path.path.getWeight()));
        }

        Path path = graph.findPath(current, getGraphVertex(getGate()));
        plan.add(new Pair<>(getGate(), path.path.getWeight()));

        return plan;
    }


    // ---------------- when user is planning their visit -----------------

    /**
     * Add an exhibit to the list to be visited by the user.
     */
    public void add(NodeInfo item) {
        item.setStatus(NodeInfo.Status.ADDED);
        dao().updateNode(item);
    }

    /**
     * Remove an exhibit from the list
     */
    public void remove(NodeInfo item) {
        item.setStatus(NodeInfo.Status.LOADED);
        dao().updateNode(item);
    }

    public int getAddedCount() {
        return getAddedList().size();
    }

    public List<NodeInfo> getAddedList() {
        return dao().getNodesWithStatus(List.of(NodeInfo.Status.ADDED));
    }

    public List<NodeInfo> getVisitedList() {
        return dao().getNodesWithStatus(List.of(NodeInfo.Status.VISITED));
    }

    public NodeInfo getNextNode() {
        List<NodeInfo> list = getAddedList();
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return getGate();
        }
    }

    public List<String> getAddedListNames() {
        return getNames(getAddedList());
    }

    public GraphVertex getGraphVertex(NodeInfo node) {
        NodeInfo parent = dao().getParentNode(node.parentId);
        return parent != null ? new GraphVertex(parent, node) : new GraphVertex(node);
    }

    /**
     * @param list the list to map to IDs
     * @return the IDs of all items in the list
     */
    public List<GraphVertex> getNavigationVertexIds(List<NodeInfo> list) {
        return list.stream()
                .map(this::getGraphVertex)
                .collect(Collectors.toList());
    }

    /**
     * @param list a list of `NodeInfo` objects to map from
     * @return a list of the name attributes of all `NodeInfo` objects
     * from the given list
     */
    public List<String> getNames(List<NodeInfo> list) {
        return list.stream()
                .map(NodeInfo::getName)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return getAllExhibits().stream()
                .map(NodeInfo::getName)
                .collect(Collectors.toList());
    }

    /**
     * retrieves EXHIBIT nodes from the database
     *
     * @return the list of all nodes that are of kind EXHIBIT
     */
    public List<NodeInfo> getAllExhibits() {
        return dao()
                .getNodesWithKind(NodeInfo.Kind.EXHIBIT);
    }

    public NodeInfo getLastVisitedNode() {
        List<NodeInfo> visited = getVisitedList();
        return visited.get(visited.size() - 1);
    }



    public NodeInfo getGate() {
        return dao().getNodesWithKind(NodeInfo.Kind.GATE).get(0);
    }
}
