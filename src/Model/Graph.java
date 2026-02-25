package Model;

import java.util.*;

public class Graph<N extends Node<?>, E extends Edge<N>> {

    @FunctionalInterface
    public interface EdgeFactory<N extends Node<?>, E extends Edge<N>> {
        E create(N from, N to, double weight);
    }

    private final EdgeFactory<N, E> edgeFactory;

    private final List<N> nodes = new ArrayList<>();
    private final List<E> edges = new ArrayList<>();

    // adjacency for fast edgesFrom
    private final Map<N, List<E>> adj = new HashMap<>();

    public Graph(EdgeFactory<N, E> edgeFactory) {
        this.edgeFactory = Objects.requireNonNull(edgeFactory);
    }

    public void addNode(N node) {
        if (node == null) return;
        if (nodes.contains(node)) return;
        nodes.add(node);
        adj.put(node, new ArrayList<>());
    }

    // Read-only views (no internal structure leakage)
    public List<N> nodes() {
        return Collections.unmodifiableList(nodes);
    }

    public List<E> edges() {
        return Collections.unmodifiableList(edges);
    }

    public List<E> edgesFrom(N node) {
        List<E> list = adj.get(node);
        if (list == null) return List.of();
        return Collections.unmodifiableList(list);
    }

    public N getNodeById(Object id) {
        for (N n : nodes) {
            if (Objects.equals(n.id(), id)) return n;
        }
        return null;
    }

    public void addUndirectedEdge(N a, N b, double w) {
        addDirectedEdge(a, b, w);
        addDirectedEdge(b, a, w);
    }

    private void addDirectedEdge(N from, N to, double w) {
        if (from == null || to == null) return;
        if (!adj.containsKey(from)) addNode(from);
        if (!adj.containsKey(to)) addNode(to);

        // replace if exists
        E existing = findDirected(from, to);
        if (existing != null) {
            existing.setWeight(w);
            return;
        }

        E e = edgeFactory.create(from, to, w);
        edges.add(e);
        adj.get(from).add(e);
    }

    public void removeUndirectedEdge(N a, N b) {
        removeDirectedEdge(a, b);
        removeDirectedEdge(b, a);
    }

    private void removeDirectedEdge(N from, N to) {
        E e = findDirected(from, to);
        if (e == null) return;
        edges.remove(e);
        List<E> list = adj.get(from);
        if (list != null) list.remove(e);
    }

    public void setUndirectedEdgeWeight(N a, N b, double w) {
        E ab = findDirected(a, b);
        E ba = findDirected(b, a);
        if (ab != null) ab.setWeight(w);
        if (ba != null) ba.setWeight(w);
    }

    public E findDirected(N from, N to) {
        List<E> list = adj.get(from);
        if (list == null) return null;
        for (E e : list) if (e.getTo().equals(to)) return e;
        return null;
    }
}