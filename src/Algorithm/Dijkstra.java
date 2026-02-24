package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

public class Dijkstra {

    // Finds shortest path, blockedEdge = road to skip (or null)
    public static List<Node> shortestPath(Graph graph, Node start, Node end, Edge blockedEdge) {
        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Node>   prev = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE))
        );

        for (Node n : graph.getNodes()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            for (Edge e : graph.getEdgesFrom(current)) {
                if (isBlocked(e, blockedEdge)) continue;
                double newDist = dist.get(current) + e.weight;
                if (newDist < dist.getOrDefault(e.to, Double.MAX_VALUE)) {
                    dist.put(e.to, newDist);
                    prev.put(e.to, current);
                    queue.add(e.to);
                }
            }
        }

        // Rebuild path backwards from end to start
        List<Node> path = new ArrayList<>();
        Node step = end;
        while (step != null) { path.add(0, step); step = prev.get(step); }
        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    private static boolean isBlocked(Edge e, Edge blocked) {
        if (blocked == null) return false;
        return (e.from.equals(blocked.from) && e.to.equals(blocked.to)) ||
                (e.from.equals(blocked.to)   && e.to.equals(blocked.from));
    }

    public static double pathDistance(Graph graph, List<Node> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            for (Edge e : graph.getEdgesFrom(path.get(i))) {
                if (e.to.equals(path.get(i + 1))) { total += e.weight; break; }
            }
        }
        return total;
    }

    public static Edge findEdge(Graph graph, Node from, Node to) {
        for (Edge e : graph.getEdgesFrom(from)) {
            if (e.to.equals(to)) return e;
        }
        return null;
    }
}