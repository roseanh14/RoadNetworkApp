package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class Dijkstra {

    @FunctionalInterface
    public interface EdgeBlocker<ID> {
        boolean blocked(Node<ID> from, Node<ID> to);
    }

    private static class PQItem<ID> {
        final Node<ID> node;
        final double dist;

        PQItem(Node<ID> node, double dist) {
            this.node = node;
            this.dist = dist;
        }
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<ID> graph, Node<ID> start, Node<ID> end) {
        return shortestPath(graph, start, end, (a, b) -> false);
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<ID> graph,
                                                   Node<ID> start,
                                                   Node<ID> end,
                                                   Node<ID> blockedFrom,
                                                   Node<ID> blockedTo) {
        return shortestPath(graph, start, end, (a, b) -> Graph.isBlocked(a, b, blockedFrom, blockedTo));
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<ID> graph,
                                                   Node<ID> start,
                                                   Node<ID> end,
                                                   EdgeBlocker<ID> blocker) {

        Map<Node<ID>, Double> dist = new HashMap<>();
        Map<Node<ID>, Node<ID>> prev = new HashMap<>();

        for (Node<ID> n : graph.nodes()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);

        PriorityQueue<PQItem<ID>> pq = new PriorityQueue<>(Comparator.comparingDouble(it -> it.dist));
        pq.add(new PQItem<>(start, 0.0));

        Set<Node<ID>> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            PQItem<ID> item = pq.poll();
            Node<ID> cur = item.node;

            if (item.dist != dist.getOrDefault(cur, Double.MAX_VALUE)) continue;
            if (!visited.add(cur)) continue;

            if (cur.equals(end)) break;

            for (Edge<ID> e : graph.edgesFrom(cur)) {
                Node<ID> ef = e.getFrom();
                Node<ID> et = e.getTo();

                if (blocker.blocked(ef, et)) continue;
                if (visited.contains(et)) continue;

                double nd = dist.get(cur) + e.getWeight();
                if (nd < dist.getOrDefault(et, Double.MAX_VALUE)) {
                    dist.put(et, nd);
                    prev.put(et, cur);
                    pq.add(new PQItem<>(et, nd));
                }
            }
        }

        List<Node<ID>> path = new ArrayList<>();
        Node<ID> step = end;
        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }
        Collections.reverse(path);

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    public static <ID> double pathDistance(Graph<ID> graph, List<Node<ID>> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Edge<ID> e = findEdge(graph, path.get(i), path.get(i + 1));
            if (e != null) total += e.getWeight();
        }
        return total;
    }

    public static <ID> Edge<ID> findEdge(Graph<ID> graph, Node<ID> from, Node<ID> to) {
        for (Edge<ID> e : graph.edgesFrom(from)) {
            if (e.getTo().equals(to)) return e;
        }
        return null;
    }
}