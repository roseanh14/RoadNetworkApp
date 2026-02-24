package Algorithm;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.*;

public class Dijkstra {

    public static List<Node> shortestPath(
            Graph<Node, Edge<Node>> graph,
            Node start,
            Node end,
            Edge<Node> blockedEdge
    ) {
        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingDouble(n -> dist.getOrDefault(n, Double.MAX_VALUE))
        );

        for (Node n : graph.getNodesView()) dist.put(n, Double.MAX_VALUE);
        dist.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            for (Edge<Node> e : graph.getEdgesFrom(current)) {
                if (isBlocked(e, blockedEdge)) continue;

                Node next = e.getTo();
                double newDist = dist.get(current) + e.getWeight();

                if (newDist < dist.getOrDefault(next, Double.MAX_VALUE)) {
                    dist.put(next, newDist);
                    prev.put(next, current);
                    queue.add(next);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Node step = end;
        while (step != null) {
            path.add(0, step);
            step = prev.get(step);
        }

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    private static boolean isBlocked(Edge<Node> e, Edge<Node> blocked) {
        if (blocked == null) return false;

        Node ef = e.getFrom(), et = e.getTo();
        Node bf = blocked.getFrom(), bt = blocked.getTo();

        return (ef.equals(bf) && et.equals(bt)) || (ef.equals(bt) && et.equals(bf));
    }

    public static double pathDistance(Graph<Node, Edge<Node>> graph, List<Node> path) {
        double total = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            Node a = path.get(i);
            Node b = path.get(i + 1);

            for (Edge<Node> e : graph.getEdgesFrom(a)) {
                if (e.getTo().equals(b)) {
                    total += e.getWeight();
                    break;
                }
            }
        }
        return total;
    }

    public static Edge<Node> findEdge(Graph<Node, Edge<Node>> graph, Node from, Node to) {
        for (Edge<Node> e : graph.getEdgesFrom(from)) {
            if (e.getTo().equals(to)) return e;
        }
        return null;
    }
}