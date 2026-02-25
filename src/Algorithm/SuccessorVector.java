package Algorithm;

import Model.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuccessorVector {

    public static <ID> Map<Node<ID>, Node<ID>> build(List<Node<ID>> path) {
        Map<Node<ID>, Node<ID>> succ = new LinkedHashMap<>();
        for (int i = 0; i < path.size(); i++) {
            Node<ID> cur = path.get(i);
            Node<ID> next = (i < path.size() - 1) ? path.get(i + 1) : null;
            succ.put(cur, next);
        }
        return succ;
    }

    public static <ID> String toTableString(Map<Node<ID>, Node<ID>> succ) {
        StringBuilder start = new StringBuilder("Start: ");
        StringBuilder next  = new StringBuilder("Next : ");

        for (Map.Entry<Node<ID>, Node<ID>> e : succ.entrySet()) {
            start.append(e.getKey().id()).append(" ");
            next.append(e.getValue() == null ? "|" : e.getValue().id()).append(" ");
        }
        return start.append("\n").append(next).toString();
    }
}