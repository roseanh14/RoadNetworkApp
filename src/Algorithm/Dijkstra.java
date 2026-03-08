package Algorithm;

import Model.Graph;

import java.util.*;

//pouzit v algoritmu vynechani hran ktere maji isActive() -> false.
public class Dijkstra {

    @FunctionalInterface
    public interface EdgeBlocker<KV> {
        boolean blocked(KV fromKey, KV toKey);
    }

    private record PQItem<KV>(KV key, double dist) {}

    public static <KV, DV, DE extends Number>
    List<KV> shortestPath(Graph<KV, DV, DE> graph, KV startKey, KV endKey) {
        return shortestPath(graph, startKey, endKey, (a, b) -> false);
    }

    public static <KV, DV, DE extends Number>
    List<KV> shortestPath(Graph<KV, DV, DE> graph,
                          KV startKey,
                          KV endKey,
                          EdgeBlocker<KV> blocker) {

        if (!graph.containsVertex(startKey) || !graph.containsVertex(endKey)) {
            return new ArrayList<>();
        }

        Map<KV, Double> dist = new HashMap<>();
        Map<KV, KV> prev = new HashMap<>();

        for (KV key : graph.keys()) {
            dist.put(key, Double.MAX_VALUE);
        }
        dist.put(startKey, 0.0);

        PriorityQueue<PQItem<KV>> pq =
                new PriorityQueue<>(Comparator.comparingDouble(PQItem::dist));
        pq.add(new PQItem<>(startKey, 0.0));

        Set<KV> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            PQItem<KV> item = pq.poll();
            KV curKey = item.key();

            if (!Objects.equals(item.dist(), dist.getOrDefault(curKey, Double.MAX_VALUE))) continue;
            if (!visited.add(curKey)) continue;

            if (Objects.equals(curKey, endKey)) break;

            Map<KV, DE> neighbors = graph.neighbors(curKey);

            for (Map.Entry<KV, DE> entry : neighbors.entrySet()) {
                KV toKey = entry.getKey();
                DE edgeData = entry.getValue();

                if (blocker.blocked(curKey, toKey)) continue;
                if (visited.contains(toKey)) continue;
                if (edgeData == null) continue;

                double w = edgeData.doubleValue();
                double nd = dist.get(curKey) + w;

                if (nd < dist.getOrDefault(toKey, Double.MAX_VALUE)) {
                    dist.put(toKey, nd);
                    prev.put(toKey, curKey);
                    pq.add(new PQItem<>(toKey, nd));
                }
            }
        }

        List<KV> path = new ArrayList<>();
        KV step = endKey;

        while (step != null) {
            path.add(step);
            step = prev.get(step);
        }

        Collections.reverse(path);

        if (path.isEmpty() || !Objects.equals(path.get(0), startKey)) {
            return new ArrayList<>();
        }

        return path;
    }

    public static <KV, DV, DE extends Number>
    double pathDistance(Graph<KV, DV, DE> graph, List<KV> path) {
        double total = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            DE w = findEdgeWeight(graph, path.get(i), path.get(i + 1));
            if (w != null) {
                total += w.doubleValue();
            }
        }

        return total;
    }

    public static <KV, DV, DE>
    DE findEdgeWeight(Graph<KV, DV, DE> graph, KV fromKey, KV toKey) {
        return graph.getEdgeData(fromKey, toKey);
    }
}