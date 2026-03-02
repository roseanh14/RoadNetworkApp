package Algorithm;

import Model.Graph;

import java.util.*;

public class SuccessorVector {

    public static <KV, DV, DE> Map<Graph<KV, DV, DE>.Vertex, List<Graph<KV, DV, DE>.Vertex>> buildAll(
            List<List<Graph<KV, DV, DE>.Vertex>> allPaths) {

        Map<Graph<KV, DV, DE>.Vertex, List<Graph<KV, DV, DE>.Vertex>> suc = new LinkedHashMap<>();

        for (var path : allPaths) {
            for (int i = 0; i < path.size(); i++) {
                var cur  = path.get(i);
                var next = (i < path.size() - 1) ? path.get(i + 1) : null;

                suc.computeIfAbsent(cur, k -> new ArrayList<>());
                if (!suc.get(cur).contains(next)) suc.get(cur).add(next);
            }
        }
        return suc;
    }

    public static <KV, DV, DE> String toTableStringAll(
            Map<Graph<KV, DV, DE>.Vertex, List<Graph<KV, DV, DE>.Vertex>> suc) {

        StringBuilder start = new StringBuilder("Start  || ");
        StringBuilder next  = new StringBuilder("Target || ");

        for (var e : suc.entrySet()) {
            start.append(e.getKey().key()).append(" | ");

            StringBuilder cell = new StringBuilder();
            for (var n : e.getValue()) {
                if (!cell.isEmpty()) cell.append("|");
                cell.append(n == null ? "∅" : n.key());
            }
            next.append(cell).append(" | ");
        }

        return start.append("\n").append(next).toString();
    }
}