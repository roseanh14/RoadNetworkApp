package Model;

import java.io.Serializable;
import java.util.*;

public class Graph<N, E> implements Serializable {

    @FunctionalInterface
    public interface EdgeFactory<N, E> extends Serializable {
        E create(N from, N to, double weight);
    }

    private final EdgeFactory<N, E> edgeFactory;

    private final Map<String, N> nodesById = new LinkedHashMap<>();

    private final Map<N, List<E>> adj = new HashMap<>();

    public Graph(EdgeFactory<N, E> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }


    public void addNode(N node) {
        if (node == null) return;
        String id = nodeId(node);
        nodesById.put(id, node);
        adj.computeIfAbsent(node, k -> new ArrayList<>());
    }

    public N getNodeById(String id) {
        if (id == null) return null;
        return nodesById.get(id);
    }

    public Collection<N> nodes() {
        return nodesById.values();
    }

    public boolean removeNode(N node) {
        if (node == null) return false;

        String id = nodeId(node);
        N real = nodesById.get(id);
        if (real == null) return false;

        adj.remove(real);

        for (List<E> list : adj.values()) {
            list.removeIf(e -> edgeTo(e).equals(real));
        }

        nodesById.remove(id);

        return true;
    }


    public Collection<E> edges() {
        List<E> all = new ArrayList<>();
        for (List<E> list : adj.values()) all.addAll(list);
        return all;
    }

    public List<E> edgesFrom(N from) {
        return adj.getOrDefault(from, List.of());
    }

    public void addUndirectedEdge(N a, N b, double w) {
        if (a == null || b == null) return;

        if (!nodesById.containsKey(nodeId(a))) addNode(a);
        if (!nodesById.containsKey(nodeId(b))) addNode(b);

        E ab = findDirectedEdge(a, b);
        if (ab == null) adj.get(a).add(edgeFactory.create(a, b, w));
        else setEdgeWeight(ab, w);

        E ba = findDirectedEdge(b, a);
        if (ba == null) adj.get(b).add(edgeFactory.create(b, a, w));
        else setEdgeWeight(ba, w);
    }

    public void removeUndirectedEdge(N a, N b) {
        if (a == null || b == null) return;
        removeDirectedEdge(a, b);
        removeDirectedEdge(b, a);
    }

    public void setUndirectedEdgeWeight(N a, N b, double w) {
        if (a == null || b == null) return;

        E ab = findDirectedEdge(a, b);
        E ba = findDirectedEdge(b, a);

        if (ab == null || ba == null) {
            addUndirectedEdge(a, b, w);
            return;
        }

        setEdgeWeight(ab, w);
        setEdgeWeight(ba, w);
    }


    private void removeDirectedEdge(N from, N to) {
        List<E> list = adj.get(from);
        if (list == null) return;
        list.removeIf(e -> edgeTo(e).equals(to));
    }

    private E findDirectedEdge(N from, N to) {
        List<E> list = adj.get(from);
        if (list == null) return null;
        for (E e : list) {
            if (edgeTo(e).equals(to)) return e;
        }
        return null;
    }

    private String nodeId(N node) {
        return String.valueOf(((Node<?>) node).id());
    }

    @SuppressWarnings("unchecked")
    private N edgeTo(E e) {
        return (N) ((Edge<?>) e).getTo();
    }

    private void setEdgeWeight(E e, double w) {
        ((Edge<?>) e).setWeight(w);
    }
}