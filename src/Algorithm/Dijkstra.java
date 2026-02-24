package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

public class Dijkstra {

    // result for alternative route
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

    // ---------- shortest path (optionally with one blocked edge) ----------
    public static <ID> List<Node<ID>> shortestPath(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            Node<ID> start,
            Node<ID> end,
            Node<ID> blockedFrom,
            Node<ID> blockedTo
    ) {
        Map<Node<ID>, Double> dist = new HashMap<>();
        Map<Node<ID>, Node<ID>> prev = new HashMap<>();

        PriorityQueue<Node<ID>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE))
        );

        for (Node<ID> n : graph.nodes()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node<ID> cur = pq.poll();
            if (cur.equals(end)) break;

            for (Edge<Node<ID>> e : graph.edgesFrom(cur)) {
                if (isBlocked(e.getFrom(), e.getTo(), blockedFrom, blockedTo)) continue;

                Node<ID> nxt = e.getTo();
                double nd = dist.get(cur) + e.getWeight();

                if (nd < dist.getOrDefault(nxt, Double.MAX_VALUE)) {
                    dist.put(nxt, nd);
                    prev.put(nxt, cur);
                    pq.add(nxt);
                }
            }
        }

        // rebuild path
        List<Node<ID>> path = new ArrayList<>();
        Node<ID> step = end;
        while (step != null) {
            path.add(0, step);
            step = prev.get(step);
        }

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    // convenience (no blocked edge)
    public static <ID> List<Node<ID>> shortestPath(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            Node<ID> start,
            Node<ID> end
    ) {
        return shortestPath(graph, start, end, null, null);
    }

    public static <ID> double pathDistance(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            List<Node<ID>> path
    ) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node<ID> a = path.get(i);
            Node<ID> b = path.get(i + 1);

            for (Edge<Node<ID>> e : graph.edgesFrom(a)) {
                if (e.getTo().equals(b)) {
                    total += e.getWeight();
                    break;
                }
            }
        }
        return total;
    }

    public static <ID> Edge<Node<ID>> findEdge(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            Node<ID> from,
            Node<ID> to
    ) {
        for (Edge<Node<ID>> e : graph.edgesFrom(from)) {
            if (e.getTo().equals(to)) return e;
        }
        return null;
    }

    // ---------- alternatives ----------
    public static <ID> List<Alternative<ID>> topAlternatives(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            Node<ID> start,
            Node<ID> end,
            int limit,
            Node<ID> globalBlockedFrom,
            Node<ID> globalBlockedTo
    ) {
        List<Node<ID>> base = shortestPath(graph, start, end, globalBlockedFrom, globalBlockedTo);
        if (base.isEmpty()) return List.of();

        double baseDist = pathDistance(graph, base);

        List<Alternative<ID>> alts = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // try blocking every edge from the base path
        for (int i = 0; i < base.size() - 1; i++) {
            Node<ID> blockFrom = base.get(i);
            Node<ID> blockTo = base.get(i + 1);

            // if the edge is already globally blocked, skip
            if (isBlocked(blockFrom, blockTo, globalBlockedFrom, globalBlockedTo)) continue;

            List<Node<ID>> altPath = shortestPathWithTwoBlocks(
                    graph, start, end,
                    globalBlockedFrom, globalBlockedTo,
                    blockFrom, blockTo
            );
            if (altPath.isEmpty()) continue;

            String key = pathKey(altPath);
            if (!seen.add(key)) continue;

            double dist = pathDistance(graph, altPath);

            // keep only meaningful alternatives (different from base)
            if (key.equals(pathKey(base))) continue;

            alts.add(new Alternative<>(blockFrom, blockTo, altPath, dist));
        }

        alts.sort(Comparator.comparingDouble(a -> a.distance));
        if (alts.size() > limit) return alts.subList(0, limit);
        return alts;
    }

    private static <ID> List<Node<ID>> shortestPathWithTwoBlocks(
            Graph<Node<ID>, Edge<Node<ID>>> graph,
            Node<ID> start,
            Node<ID> end,
            Node<ID> blockedFrom1,
            Node<ID> blockedTo1,
            Node<ID> blockedFrom2,
            Node<ID> blockedTo2
    ) {
        Map<Node<ID>, Double> dist = new HashMap<>();
        Map<Node<ID>, Node<ID>> prev = new HashMap<>();

        PriorityQueue<Node<ID>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE))
        );

        for (Node<ID> n : graph.nodes()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node<ID> cur = pq.poll();
            if (cur.equals(end)) break;

            for (Edge<Node<ID>> e : graph.edgesFrom(cur)) {
                Node<ID> ef = e.getFrom();
                Node<ID> et = e.getTo();

                if (isBlocked(ef, et, blockedFrom1, blockedTo1)) continue;
                if (isBlocked(ef, et, blockedFrom2, blockedTo2)) continue;

                Node<ID> nxt = et;
                double nd = dist.get(cur) + e.getWeight();

                if (nd < dist.getOrDefault(nxt, Double.MAX_VALUE)) {
                    dist.put(nxt, nd);
                    prev.put(nxt, cur);
                    pq.add(nxt);
                }
            }
        }

        List<Node<ID>> path = new ArrayList<>();
        Node<ID> step = end;
        while (step != null) {
            path.add(0, step);
            step = prev.get(step);
        }

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    private static <ID> boolean isBlocked(Node<ID> a, Node<ID> b, Node<ID> bf, Node<ID> bt) {
        if (bf == null || bt == null) return false;
        return (a.equals(bf) && b.equals(bt)) || (a.equals(bt) && b.equals(bf));
    }

    private static <ID> String pathKey(List<Node<ID>> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getId());
            if (i < path.size() - 1) sb.append("->");
        }
        return sb.toString();
    }
}