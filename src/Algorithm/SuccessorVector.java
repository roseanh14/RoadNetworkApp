package Algorithm;

import Model.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuccessorVector {

    public static <ID, C extends Number> Map<Node<ID, C>, Node<ID, C>> build(List<Node<ID, C>> path) {
        Map<Node<ID, C>, Node<ID, C>> suc = new LinkedHashMap<>();
        for (int i = 0; i < path.size(); i++) {
            Node<ID, C> cur = path.get(i);
            Node<ID, C> next = (i < path.size() - 1) ? path.get(i + 1) : null;
            suc.put(cur, next);
        }
        return suc;
    }

    public static <ID, C extends Number> String toTableString(Map<Node<ID, C>, Node<ID, C>> suc) {
        StringBuilder start = new StringBuilder("Start: ");
        StringBuilder next  = new StringBuilder("Next : ");

        for (Map.Entry<Node<ID, C>, Node<ID, C>> e : suc.entrySet()) {
            start.append(e.getKey().id()).append(" ");
            next.append(e.getValue() == null ? "|" : e.getValue().id()).append(" ");
        }
        return start.append("\n").append(next).toString();
    }
}