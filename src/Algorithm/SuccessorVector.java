package Algorithm;

import Model.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuccessorVector {

    public static <ID, C extends Number> Map<Node<ID, C>, List<Node<ID, C>>> buildAll(
            List<List<Node<ID, C>>> allPaths) {

        Map<Node<ID, C>, List<Node<ID, C>>> suc = new LinkedHashMap<>();

        for (List<Node<ID, C>> path : allPaths) {
            for (int i = 0; i < path.size(); i++) {
                Node<ID, C> cur  = path.get(i);
                Node<ID, C> next = (i < path.size() - 1) ? path.get(i + 1) : null;

                suc.computeIfAbsent(cur, k -> new ArrayList<>());

                if (!suc.get(cur).contains(next)) {
                    suc.get(cur).add(next);
                }
            }
        }
        return suc;
    }

    public static <ID, C extends Number> String toTableStringAll(
            Map<Node<ID, C>, List<Node<ID, C>>> suc) {

        StringBuilder start = new StringBuilder("Start  || ");
        StringBuilder next  = new StringBuilder("Target || ");

        for (Map.Entry<Node<ID, C>, List<Node<ID, C>>> e : suc.entrySet()) {
            start.append(e.getKey().id()).append(" | ");

            StringBuilder cell = new StringBuilder();
            for (Node<ID, C> n : e.getValue()) {
                if (!cell.isEmpty()) cell.append("|");
                cell.append(n == null ? "∅" : n.id());
            }
            next.append(cell).append(" | ");
        }

        return start.append("\n").append(next).toString();
    }
}