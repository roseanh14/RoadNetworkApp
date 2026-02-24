package Model;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public void addNode(Node node) { nodes.add(node); }

    public Node getNode(String name) {
        for (Node n : nodes) {
            if (n.name.equals(name)) return n;
        }
        return null;
    }

    // Adds a road in both directions (undirected)
    public void addEdge(Node from, Node to, double weight) {
        edges.add(new Edge(from, to, weight));
        edges.add(new Edge(to, from, weight));
    }

    public void removeEdge(Node from, Node to) {
        edges.removeIf(e ->
                (e.from.equals(from) && e.to.equals(to)) ||
                        (e.from.equals(to)   && e.to.equals(from))
        );
    }

    public void setEdgeWeight(Node from, Node to, double w) {
        for (Edge e : edges) {
            if ((e.from.equals(from) && e.to.equals(to)) ||
                    (e.from.equals(to)   && e.to.equals(from))) {
                e.weight = w;
            }
        }
    }

    public void setProblematic(Node from, Node to, boolean value) {
        for (Edge e : edges) {
            if ((e.from.equals(from) && e.to.equals(to)) ||
                    (e.from.equals(to)   && e.to.equals(from))) {
                e.problematic = value;
            }
        }
    }

    public List<Edge> getEdgesFrom(Node node) {
        List<Edge> result = new ArrayList<>();
        for (Edge e : edges) {
            if (e.from.equals(node)) result.add(e);
        }
        return result;
    }

    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
}