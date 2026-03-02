package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

public class Dijkstra {

    @FunctionalInterface
    public interface EdgeBlocker<ID, C extends Number> {
        boolean blocked(Node<ID, C> from, Node<ID, C> to);
    }

    @SuppressWarnings({"unused", "ClassCanBeRecord"})
    public static class Alternative<ID, C extends Number> {
        private final Node<ID, C> blockedFrom;
        private final Node<ID, C> blockedTo;
        private final List<Node<ID, C>> path;
        private final double distance;

        public Alternative(Node<ID, C> blockedFrom, Node<ID, C> blockedTo, List<Node<ID, C>> path, double distance) {
            this.blockedFrom = blockedFrom;
            this.blockedTo = blockedTo;
            this.path = path;
            this.distance = distance;
        }

        public Node<ID, C> getBlockedFrom() { return blockedFrom; }
        public Node<ID, C> getBlockedTo() { return blockedTo; }
        public List<Node<ID, C>> getPath() { return path; }
        public double getDistance() { return distance; }
    }

    private record PQItem<ID, C extends Number>(Node<ID, C> node, double dist) {
    }

    public static <ID, C extends Number, W extends Number>
    List<Node<ID, C>> shortestPath(Graph<ID, C, W> graph, Node<ID, C> start, Node<ID, C> end) {
        return shortestPath(graph, start, end, (a, b) -> false);
    }

    @SuppressWarnings("unused")
    public static <ID, C extends Number, W extends Number>
    List<Node<ID, C>> shortestPath(Graph<ID, C, W> graph,
                                   Node<ID, C> start,
                                   Node<ID, C> end,
                                   Node<ID, C> blockedFrom,
                                   Node<ID, C> blockedTo) {
        return shortestPath(graph, start, end, (a, b) -> Graph.isBlocked(a, b, blockedFrom, blockedTo));
    }

    public static <ID, C extends Number, W extends Number>
    List<Node<ID, C>> shortestPath(Graph<ID, C, W> graph,
                                   Node<ID, C> start,
                                   Node<ID, C> end,
                                   EdgeBlocker<ID, C> blocker) {

        Map<Node<ID, C>, Double> dist = new HashMap<>();
        Map<Node<ID, C>, Node<ID, C>> prev = new HashMap<>();

        for (Node<ID, C> n : graph.nodes()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);

        PriorityQueue<PQItem<ID, C>> pq = new PriorityQueue<>(Comparator.comparingDouble(it -> it.dist));
        pq.add(new PQItem<>(start, 0.0));

        Set<Node<ID, C>> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            PQItem<ID, C> item = pq.poll();
            Node<ID, C> cur = item.node;

            if (item.dist != dist.getOrDefault(cur, Double.MAX_VALUE)) continue;
            if (!visited.add(cur)) continue;

            if (cur.equals(end)) break;

            for (Edge<ID, C, W> e : graph.edgesFrom(cur)) {
                Node<ID, C> ef = e.getFrom();
                Node<ID, C> et = e.getTo();

                if (blocker.blocked(ef, et)) continue;
                if (visited.contains(et)) continue;

                double nd = dist.get(cur) + e.getWeight().doubleValue();
                if (nd < dist.getOrDefault(et, Double.MAX_VALUE)) {
                    dist.put(et, nd);
                    prev.put(et, cur);
                    pq.add(new PQItem<>(et, nd));
                }
            }
        }

        List<Node<ID, C>> path = new ArrayList<>();
        Node<ID, C> step = end;
        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }
        Collections.reverse(path);

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    public static <ID, C extends Number, W extends Number>
    double pathDistance(Graph<ID, C, W> graph, List<Node<ID, C>> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Edge<ID, C, W> e = findEdge(graph, path.get(i), path.get(i + 1));
            if (e != null) total += e.getWeight().doubleValue();
        }
        return total;
    }

    public static <ID, C extends Number, W extends Number>
    Edge<ID, C, W> findEdge(Graph<ID, C, W> graph, Node<ID, C> from, Node<ID, C> to) {
        for (Edge<ID, C, W> e : graph.edgesFrom(from)) {
            if (e.getTo().equals(to)) return e;
        }
        return null;
    }
}