package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph<N extends Node, E extends Edge<N>> {

    private final List<N> nodes = new ArrayList<>();
    private final List<E> edges = new ArrayList<>();

    public interface EdgeFactory<N extends Node, E extends Edge<N>> {
        E create(N from, N to, double weight);
    }

    private final EdgeFactory<N, E> edgeFactory;

    public Graph(EdgeFactory<N, E> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public void addNode(N node) {
        if (node == null) return;
        if (!nodes.contains(node)) nodes.add(node);
    }

    public N getNodeByName(String name) {
        for (N n : nodes) {
            if (n.getName().equals(name)) return n;
        }
        return null;
    }

    public List<N> getNodesView() {
        return Collections.unmodifiableList(nodes);
    }

    public void addUndirectedEdge(N a, N b, double weight) {
        if (a == null || b == null) return;
        addNode(a);
        addNode(b);

        edges.add(edgeFactory.create(a, b, weight));
        edges.add(edgeFactory.create(b, a, weight));
    }

    public void removeUndirectedEdge(N a, N b) {
        edges.removeIf(e ->
                (e.getFrom().equals(a) && e.getTo().equals(b)) ||
                        (e.getFrom().equals(b) && e.getTo().equals(a))
        );
    }

    public void setUndirectedEdgeWeight(N a, N b, double newWeight) {
        for (E e : edges) {
            boolean matches =
                    (e.getFrom().equals(a) && e.getTo().equals(b)) ||
                            (e.getFrom().equals(b) && e.getTo().equals(a));
            if (matches) e.setWeight(newWeight);
        }
    }

    public void setUndirectedProblematic(N a, N b, boolean value) {
        for (E e : edges) {
            boolean matches =
                    (e.getFrom().equals(a) && e.getTo().equals(b)) ||
                            (e.getFrom().equals(b) && e.getTo().equals(a));
            if (matches) e.setProblematic(value);
        }
    }

    public List<E> getEdgesFrom(N node) {
        List<E> out = new ArrayList<>();
        for (E e : edges) {
            if (e.getFrom().equals(node)) out.add(e);
        }
        return Collections.unmodifiableList(out);
    }

    public List<E> getEdgesView() {
        return Collections.unmodifiableList(edges);
    }

    public N getNode(String name) {
        return getNodeByName(name);
    }

    public void addEdge(N from, N to, double weight) {
        addUndirectedEdge(from, to, weight);
    }

    public void setProblematic(N from, N to, boolean value) {
        setUndirectedProblematic(from, to, value);
    }

    public void setEdgeWeight(N from, N to, double w) {
        setUndirectedEdgeWeight(from, to, w);
    }

    public void removeEdge(N from, N to) {
        removeUndirectedEdge(from, to);
    }

    public List<N> getNodes() {
        return getNodesView();
    }

    public List<E> getEdges() {
        return getEdgesView();
    }
}