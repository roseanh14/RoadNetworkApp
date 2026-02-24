package Model;

import java.util.*;

public class Graph<N extends Node<?>, E extends Edge<N>> {

    private final List<N> nodes = new ArrayList<>();
    private final List<E> edges = new ArrayList<>();

    // fast lookup by id
    private final Map<Object, N> nodeById = new HashMap<>();

    // adjacency list (fast edgesFrom)
    private final Map<N, List<E>> adj = new HashMap<>();

    public interface EdgeFactory<N extends Node<?>, E extends Edge<N>> {
        E create(N from, N to, double weight);
    }

    private final EdgeFactory<N, E> edgeFactory;

    public Graph(EdgeFactory<N, E> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public void addNode(N node) {
        nodes.add(node);
        nodeById.put(node.getId(), node);
        adj.putIfAbsent(node, new ArrayList<>());
    }

    public N getNodeById(Object id) {
        return nodeById.get(id);
    }

    public void addUndirectedEdge(N a, N b, double w) {
        addDirectedEdge(a, b, w);
        addDirectedEdge(b, a, w);
    }

    private void addDirectedEdge(N from, N to, double w) {
        E e = edgeFactory.create(from, to, w);
        edges.add(e);
        adj.computeIfAbsent(from, k -> new ArrayList<>()).add(e);
    }

    public void removeUndirectedEdge(N a, N b) {
        edges.removeIf(e ->
                (e.getFrom().equals(a) && e.getTo().equals(b)) ||
                        (e.getFrom().equals(b) && e.getTo().equals(a))
        );

        List<E> la = adj.get(a);
        if (la != null) la.removeIf(e -> e.getTo().equals(b));

        List<E> lb = adj.get(b);
        if (lb != null) lb.removeIf(e -> e.getTo().equals(a));
    }

    public void setUndirectedEdgeWeight(N a, N b, double w) {
        for (E e : edgesFrom(a)) if (e.getTo().equals(b)) e.setWeight(w);
        for (E e : edgesFrom(b)) if (e.getTo().equals(a)) e.setWeight(w);
    }

    public List<E> edgesFrom(N node) {
        return Collections.unmodifiableList(adj.getOrDefault(node, List.of()));
    }

    // do not expose internal structure
    public List<N> nodes() { return Collections.unmodifiableList(nodes); }
    public List<E> edges() { return Collections.unmodifiableList(edges); }
}