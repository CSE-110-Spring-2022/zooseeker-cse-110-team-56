package edu.ucsd.cse110.team56.zooseeker.activity.manager;

import android.content.Context;
import android.util.Pair;

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
            NodeInfo lastNode = getLastVisitedNode();
            NodeInfo gate = getGate();

            current = lastNode == null ? gate : lastNode;
        }
        return Graph.load(context).findPath(getGraphVertex(current), getGraphVertex(getNextExhibit()));
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
        ArrayList<Path> paths = Graph.load(context).findPaths(getGraphVertex(current), getGraphVertices(toBeVisited), getGraphVertex(getGate()));

        for(Path path: paths) {
            NodeInfo info = path.endInfo.getActualExhibit();
            info.order = idx;
            ++idx;

            dao().updateNode(info);
        }
    }

    /**
     * Go to the next exhibit.
     */
    public void next() {
        NodeInfo node = getNextExhibit();
        node.status = NodeInfo.Status.VISITED;
        dao().updateNode(node);
    }

    /**
     * Can skip the next exhibit.
     */
    public boolean canSkip() {
        return getAddedList().size() >= 1;
    }

    /**
     * Skip the next exhibit.
     */
    public void skip() {
        NodeInfo node = getNextExhibit();
        node.status = NodeInfo.Status.LOADED;
        dao().updateNode(node);
    }

    /**
     * Can go to previous exhibit.
     */
    public boolean canStepBack() {
        return getLastVisitedNode() != null;
    }

    /**
     * Go back to previous exhibit.
     */
    public void stepBack() {
        NodeInfo node = getLastVisitedNode();
        node.status = NodeInfo.Status.ADDED;
        dao().updateNode(node);
    }

    /**
     * Shuffle the planned exhibits to minimize the distance walked.
     * @return a pair of the exhibit and the distance
     */
    public List<Pair<NodeInfo, Double>> getPlan() {
        List<NodeInfo> list = getAddedAndVisitedList();
        List<Pair<NodeInfo, Double>> plan = new ArrayList<>();

        Graph graph = Graph.load(context);
        GraphVertex current = getGraphVertex(getGate());

        for(NodeInfo node: list) {
            Path path = graph.findPath(current, getGraphVertex(node));
            plan.add(new Pair<>(node, path.path.getWeight()));
            current = getGraphVertex(node);
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

    /**
     * Get the list of exhibit added by the user but not visited yet.
     */
    public List<NodeInfo> getAddedList() {
        return dao().getNodesWithStatus(List.of(NodeInfo.Status.ADDED));
    }

    /**
     * Get the list of exhibits visited by the user, ordered by sequence that the
     * user visited the exhibit.
     */
    public List<NodeInfo> getVisitedList() {
        return dao().getNodesWithStatus(List.of(NodeInfo.Status.VISITED));
    }

    /**
     * Get the combined list of exhibits added and visited by the user.
     */
    public List<NodeInfo> getAddedAndVisitedList() {
        return dao().getNodesWithStatus(List.of(NodeInfo.Status.ADDED, NodeInfo.Status.VISITED));
    }

    /**
     * Get the next exhibit to be visited by the user.
     */
    public NodeInfo getNextExhibit() {
        List<NodeInfo> list = getAddedList();
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return getGate();
        }
    }

    /**
     * Get a list of names of the exhibits added and visited by the user.
     */
    public List<String> getAddedAndVisitedNames() {
        return getNames(getAddedAndVisitedList());
    }

    /**
     * Convert the exhibit to a Graph Vertex.
     */
    public GraphVertex getGraphVertex(NodeInfo node) {
        NodeInfo parent = dao().getParentNode(node.parentId);
        return parent != null ? new GraphVertex(parent, node) : new GraphVertex(node);
    }

    /**
     * Convert a list of exhibits to graph vertices.
     */
    public List<GraphVertex> getGraphVertices(List<NodeInfo> list) {
        return list.stream()
                .map(this::getGraphVertex)
                .collect(Collectors.toList());
    }

    /**
     * @param list a list of `NodeInfo` objects to map from
     * @return a list of the name attributes of all `NodeInfo` objects
     * from the given list
     */
    public static List<String> getNames(List<NodeInfo> list) {
        return list.stream()
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

    /**
     * Get the last node visited by the user
     * @return null if there's none.
     */
    public NodeInfo getLastVisitedNode() {
        List<NodeInfo> visited = getVisitedList();
        if (visited.size() == 0) {
            return null;
        } else {
            return visited.get(visited.size() - 1);
        }
    }

    /**
     * Get the gate of the zoo.
     */
    public NodeInfo getGate() {
        return dao().getNodesWithKind(NodeInfo.Kind.GATE).get(0);
    }
}
