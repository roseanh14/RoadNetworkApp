package Algorithm;

import Model.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuccessorVector {

    public static <ID> Map<Node<ID>, Node<ID>> build(List<Node<ID>> path) {
        Map<Node<ID>, Node<ID>> succ = new LinkedHashMap<>();
        if (path == null || path.isEmpty()) return succ;

        for (int i = 0; i < path.size() - 1; i++) {
            succ.put(path.get(i), path.get(i + 1));
        }
        succ.put(path.get(path.size() - 1), null);
        return succ;
    }

    public static <ID> String toTableString(Map<Node<ID>, Node<ID>> succ) {
        if (succ == null || succ.isEmpty()) return "(empty successor vector)\n";

        StringBuilder start = new StringBuilder("Start: ");
        StringBuilder goal  = new StringBuilder("Next : ");

        for (Map.Entry<Node<ID>, Node<ID>> e : succ.entrySet()) {
            start.append(e.getKey().getId()).append(" ");
            goal.append(e.getValue() == null ? "|" : e.getValue().getId()).append(" ");
        }

        return start.append("\n").append(goal).append("\n").toString();
    }
}