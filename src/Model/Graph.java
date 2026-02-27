package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Graph<ID, C extends Number, W extends Number> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @FunctionalInterface
    public interface EdgeFactory<ID, C extends Number, W extends Number> extends Serializable {
        Edge<ID, C, W> create(Node<ID, C> from, Node<ID, C> to, W weight);
    }

    private final EdgeFactory<ID, C, W> edgeFactory;

    private final Map<ID, Node<ID, C>> nodes = new LinkedHashMap<>();
    private final Map<Node<ID, C>, List<Edge<ID, C, W>>> adj = new HashMap<>();

    public Graph(EdgeFactory<ID, C, W> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public Collection<Node<ID, C>> nodes() {
        return nodes.values();
    }

    public Collection<Edge<ID, C, W>> edges() {
        List<Edge<ID, C, W>> all = new ArrayList<>();
        for (List<Edge<ID, C, W>> list : adj.values()) all.addAll(list);
        return all;
    }

    public List<Edge<ID, C, W>> edgesFrom(Node<ID, C> node) {
        return adj.getOrDefault(node, List.of());
    }

    public Node<ID, C> getNodeById(ID id) {
        return nodes.get(id);
    }

    public void addNode(Node<ID, C> node) {
        if (node == null) return;
        nodes.put(node.id(), node);
        adj.putIfAbsent(node, new ArrayList<>());
    }

    public boolean removeNode(Node<ID, C> node) {
        if (node == null) return false;
        if (!nodes.containsKey(node.id())) return false;

        adj.remove(node);

        for (List<Edge<ID, C, W>> list : adj.values()) {
            list.removeIf(e -> e.getTo().equals(node) || e.getFrom().equals(node));
        }

        nodes.remove(node.id());
        return true;
    }

    public void addUndirectedEdge(Node<ID, C> a, Node<ID, C> b, W w) {
        if (a == null || b == null) return;
        addDirectedEdge(a, b, w);
        addDirectedEdge(b, a, w);
    }

    private void addDirectedEdge(Node<ID, C> from, Node<ID, C> to, W w) {
        adj.putIfAbsent(from, new ArrayList<>());
        List<Edge<ID, C, W>> list = adj.get(from);

        for (Edge<ID, C, W> e : list) {
            if (e.getTo().equals(to)) {
                e.setWeight(w);
                return;
            }
        }
        list.add(edgeFactory.create(from, to, w));
    }

    public void removeUndirectedEdge(Node<ID, C> a, Node<ID, C> b) {
        removeDirectedEdge(a, b);
        removeDirectedEdge(b, a);
    }

    private void removeDirectedEdge(Node<ID, C> from, Node<ID, C> to) {
        List<Edge<ID, C, W>> list = adj.get(from);
        if (list == null) return;
        list.removeIf(e -> e.getTo().equals(to));
    }

    public void setUndirectedEdgeWeight(Node<ID, C> a, Node<ID, C> b, W w) {
        addUndirectedEdge(a, b, w);
    }

    public static <ID, C extends Number> boolean isBlocked(Node<ID, C> a, Node<ID, C> b,
                                                           Node<ID, C> bf, Node<ID, C> bt) {
        if (bf == null || bt == null) return false;
        return (a.equals(bf) && b.equals(bt)) || (a.equals(bt) && b.equals(bf));
    }

    public static <ID, C extends Number> String edgeKey(Node<ID, C> a, Node<ID, C> b) {
        String x = String.valueOf(a.id());
        String y = String.valueOf(b.id());
        return (x.compareTo(y) <= 0) ? x + "-" + y : y + "-" + x;
    }
}