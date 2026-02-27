package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Graph<ID> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @FunctionalInterface
    public interface EdgeFactory<ID> extends Serializable {
        Edge<ID> create(Node<ID> from, Node<ID> to, double weight);
    }

    private final EdgeFactory<ID> edgeFactory;

    private final Map<ID, Node<ID>> nodes = new LinkedHashMap<>();
    private final Map<Node<ID>, List<Edge<ID>>> adj = new HashMap<>();

    public Graph(EdgeFactory<ID> edgeFactory) {
        this.edgeFactory = edgeFactory;
    }

    public Collection<Node<ID>> nodes() {
        return nodes.values();
    }

    public Collection<Edge<ID>> edges() {
        List<Edge<ID>> all = new ArrayList<>();
        for (List<Edge<ID>> list : adj.values()) all.addAll(list);
        return all;
    }

    public List<Edge<ID>> edgesFrom(Node<ID> node) {
        return adj.getOrDefault(node, List.of());
    }

    public Node<ID> getNodeById(ID id) {
        return nodes.get(id);
    }

    public void addNode(Node<ID> node) {
        if (node == null) return;
        nodes.put(node.id(), node);
        adj.putIfAbsent(node, new ArrayList<>());
    }

    public boolean removeNode(Node<ID> node) {
        if (node == null) return false;
        if (!nodes.containsKey(node.id())) return false;

        adj.remove(node);

        for (List<Edge<ID>> list : adj.values()) {
            list.removeIf(e -> e.getTo().equals(node) || e.getFrom().equals(node));
        }

        nodes.remove(node.id());
        return true;
    }

    public void addUndirectedEdge(Node<ID> a, Node<ID> b, double w) {
        if (a == null || b == null) return;
        addDirectedEdge(a, b, w);
        addDirectedEdge(b, a, w);
    }

    private void addDirectedEdge(Node<ID> from, Node<ID> to, double w) {
        adj.putIfAbsent(from, new ArrayList<>());
        List<Edge<ID>> list = adj.get(from);

        for (Edge<ID> e : list) {
            if (e.getTo().equals(to)) {
                e.setWeight(w);
                return;
            }
        }

        list.add(edgeFactory.create(from, to, w));
    }

    public void removeUndirectedEdge(Node<ID> a, Node<ID> b) {
        removeDirectedEdge(a, b);
        removeDirectedEdge(b, a);
    }

    private void removeDirectedEdge(Node<ID> from, Node<ID> to) {
        List<Edge<ID>> list = adj.get(from);
        if (list == null) return;
        list.removeIf(e -> e.getTo().equals(to));
    }

    public void setUndirectedEdgeWeight(Node<ID> a, Node<ID> b, double w) {
        addUndirectedEdge(a, b, w);
    }

    public static <ID> boolean isBlocked(Node<ID> a, Node<ID> b, Node<ID> bf, Node<ID> bt) {
        if (bf == null || bt == null) return false;
        return (a.equals(bf) && b.equals(bt)) || (a.equals(bt) && b.equals(bf));
    }

    public static <ID> String edgeKey(Node<ID> a, Node<ID> b) {
        String x = String.valueOf(a.id());
        String y = String.valueOf(b.id());
        return (x.compareTo(y) <= 0) ? x + "-" + y : y + "-" + x;
    }
}