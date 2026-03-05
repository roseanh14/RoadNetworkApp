package Algorithm;

import java.util.*;

public class SuccessorVector {

    public static <KV> Map<KV, List<KV>> buildAll(List<List<KV>> allPaths) {

        Map<KV, List<KV>> suc = new LinkedHashMap<>();

        for (var path : allPaths) {
            for (int i = 0; i < path.size(); i++) {
                KV cur  = path.get(i);
                KV next = (i < path.size() - 1) ? path.get(i + 1) : null;

                suc.computeIfAbsent(cur, k -> new ArrayList<>());

                if (!suc.get(cur).contains(next)) {
                    suc.get(cur).add(next);
                }
            }
        }
        return suc;
    }

    public static <KV> String toTableStringAll(Map<KV, List<KV>> suc) {

        StringBuilder start = new StringBuilder("Start  || ");
        StringBuilder next  = new StringBuilder("Target || ");

        for (var e : suc.entrySet()) {
            start.append(e.getKey()).append(" | ");

            StringBuilder cell = new StringBuilder();
            for (var n : e.getValue()) {
                if (!cell.isEmpty()) cell.append("|");
                cell.append(n == null ? "∅" : n);
            }
            next.append(cell).append(" | ");
        }

        return start.append("\n").append(next).toString();
    }
}