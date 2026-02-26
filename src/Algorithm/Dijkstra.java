package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

@SuppressWarnings({"DuplicatedCode", "unused", "ClassCanBeRecord"})
public class Dijkstra {

    @FunctionalInterface
    public interface EdgeBlocker<ID> {
        boolean blocked(Node<ID> from, Node<ID> to);
    }

    public static class Alternative<ID> {
        public final Node<ID> blockedFrom;
        public final Node<ID> blockedTo;
        public final List<Node<ID>> path;
        public final double distance;

        public Alternative(Node<ID> blockedFrom, Node<ID> blockedTo, List<Node<ID>> path, double distance) {
            this.blockedFrom = blockedFrom;
            this.blockedTo = blockedTo;
            this.path = path;
            this.distance = distance;
        }
    }

    private static class PQItem<ID> {
        final Node<ID> node;
        final double dist;

        PQItem(Node<ID> node, double dist) {
            this.node = node;
            this.dist = dist;
        }
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<Node<ID>, Edge<Node<ID>>> graph,
                                                   Node<ID> start,
                                                   Node<ID> end) {
        return shortestPath(graph, start, end, (a, b) -> false);
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<Node<ID>, Edge<Node<ID>>> graph,
                                                   Node<ID> start,
                                                   Node<ID> end,
                                                   Node<ID> blockedFrom,
                                                   Node<ID> blockedTo) {
        return shortestPath(graph, start, end, (a, b) -> isBlocked(a, b, blockedFrom, blockedTo));
    }

    public static <ID> List<Node<ID>> shortestPath(Graph<Node<ID>, Edge<Node<ID>>> graph,
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

            for (Edge<Node<ID>> e : graph.edgesFrom(cur)) {
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

    public static <ID> double pathDistance(Graph<Node<ID>, Edge<Node<ID>>> graph, List<Node<ID>> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Edge<Node<ID>> e = findEdge(graph, path.get(i), path.get(i + 1));
            if (e != null) total += e.getWeight();
        }
        return total;
    }

    public static <ID> Edge<Node<ID>> findEdge(Graph<Node<ID>, Edge<Node<ID>>> graph, Node<ID> from, Node<ID> to) {
        for (Edge<Node<ID>> e : graph.edgesFrom(from)) {
            if (e.getTo().equals(to)) return e;
        }
        return null;
    }

    public static <ID> List<Alternative<ID>> topAlternatives(Graph<Node<ID>, Edge<Node<ID>>> graph,
                                                             Node<ID> start,
                                                             Node<ID> end,
                                                             int limit,
                                                             Node<ID> globalBlockedFrom,
                                                             Node<ID> globalBlockedTo) {

        EdgeBlocker<ID> globalBlocker = (a, b) -> isBlocked(a, b, globalBlockedFrom, globalBlockedTo);
        return topAlternatives(graph, start, end, limit, globalBlocker);
    }

    public static <ID> List<Alternative<ID>> topAlternatives(Graph<Node<ID>, Edge<Node<ID>>> graph,
                                                             Node<ID> start,
                                                             Node<ID> end,
                                                             int limit,
                                                             EdgeBlocker<ID> globalBlocker) {

        List<Node<ID>> base = shortestPath(graph, start, end, globalBlocker);
        if (base.isEmpty()) return List.of();

        String baseKey = pathKey(base);

        List<Alternative<ID>> alts = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < base.size() - 1; i++) {
            Node<ID> blockFrom = base.get(i);
            Node<ID> blockTo = base.get(i + 1);

            if (globalBlocker.blocked(blockFrom, blockTo)) continue;

            EdgeBlocker<ID> twoBlocks = (a, b) ->
                    globalBlocker.blocked(a, b) || isBlocked(a, b, blockFrom, blockTo);

            List<Node<ID>> altPath = shortestPath(graph, start, end, twoBlocks);
            if (altPath.isEmpty()) continue;

            String key = pathKey(altPath);
            if (key.equals(baseKey)) continue;
            if (!seen.add(key)) continue;

            double dist = pathDistance(graph, altPath);
            alts.add(new Alternative<>(blockFrom, blockTo, altPath, dist));
        }

        alts.sort(Comparator.comparingDouble(a -> a.distance));
        return alts.size() > limit ? alts.subList(0, limit) : alts;
    }

    private static <ID> boolean isBlocked(Node<ID> a, Node<ID> b, Node<ID> bf, Node<ID> bt) {
        if (bf == null || bt == null) return false;
        return (a.equals(bf) && b.equals(bt)) || (a.equals(bt) && b.equals(bf));
    }

    private static <ID> String pathKey(List<Node<ID>> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).id());
            if (i < path.size() - 1) sb.append("->");
        }
        return sb.toString();
    }
}