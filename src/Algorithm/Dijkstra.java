package Algorithm;

import Model.Graph;

import java.util.*;

public class Dijkstra {

    @FunctionalInterface
    public interface EdgeBlocker<KV, DV, DE> {
        boolean blocked(Graph<KV, DV, DE>.Vertex from, Graph<KV, DV, DE>.Vertex to);
    }

    private record PQItem<KV, DV, DE>(Graph<KV, DV, DE>.Vertex v, double dist) {}

    public static <KV, DV, DE extends Number>
    List<Graph<KV, DV, DE>.Vertex> shortestPath(Graph<KV, DV, DE> graph,
                                                Graph<KV, DV, DE>.Vertex start,
                                                Graph<KV, DV, DE>.Vertex end) {
        return shortestPath(graph, start, end, (a, b) -> false);
    }

    public static <KV, DV, DE extends Number>
    List<Graph<KV, DV, DE>.Vertex> shortestPath(Graph<KV, DV, DE> graph,
                                                Graph<KV, DV, DE>.Vertex start,
                                                Graph<KV, DV, DE>.Vertex end,
                                                EdgeBlocker<KV, DV, DE> blocker) {

        Map<Graph<KV, DV, DE>.Vertex, Double> dist = new HashMap<>();
        Map<Graph<KV, DV, DE>.Vertex, Graph<KV, DV, DE>.Vertex> prev = new HashMap<>();

        for (var v : graph.vertices()) dist.put(v, Double.MAX_VALUE);
        dist.put(start, 0.0);

        PriorityQueue<PQItem<KV, DV, DE>> pq =
                new PriorityQueue<>(Comparator.comparingDouble(PQItem::dist));
        pq.add(new PQItem<>(start, 0.0));

        Set<Graph<KV, DV, DE>.Vertex> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            var item = pq.poll();
            var cur = item.v();

            if (!Objects.equals(item.dist(), dist.getOrDefault(cur, Double.MAX_VALUE))) continue;
            if (!visited.add(cur)) continue;

            if (cur.equals(end)) break;

            for (var e : graph.edgesFrom(cur)) {
                var to = e.to();

                if (blocker.blocked(cur, to)) continue;
                if (visited.contains(to)) continue;

                double w = e.data().doubleValue();
                double nd = dist.get(cur) + w;

                if (nd < dist.getOrDefault(to, Double.MAX_VALUE)) {
                    dist.put(to, nd);
                    prev.put(to, cur);
                    pq.add(new PQItem<>(to, nd));
                }
            }
        }

        List<Graph<KV, DV, DE>.Vertex> path = new ArrayList<>();
        var step = end;
        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }
        Collections.reverse(path);

        if (path.isEmpty() || !path.get(0).equals(start)) return new ArrayList<>();
        return path;
    }

    public static <KV, DV, DE extends Number>
    double pathDistance(Graph<KV, DV, DE> graph, List<Graph<KV, DV, DE>.Vertex> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            var e = findEdge(graph, path.get(i), path.get(i + 1));
            if (e != null) total += e.data().doubleValue();
        }
        return total;
    }

    public static <KV, DV, DE>
    Graph<KV, DV, DE>.Edge findEdge(Graph<KV, DV, DE> graph,
                                    Graph<KV, DV, DE>.Vertex from,
                                    Graph<KV, DV, DE>.Vertex to) {
        for (var e : graph.edgesFrom(from)) {
            if (e.to().equals(to)) return e;
        }
        return null;
    }
}