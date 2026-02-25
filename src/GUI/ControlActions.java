package GUI;

import Algorithm.Dijkstra;
import Algorithm.SuccessorVector;
import Data.GraphFileIO;
import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControlActions {

    public enum EdgeOp { EDIT_WEIGHT, ADD, REMOVE }

    private final Graph<Node<String>, Edge<Node<String>>> g;
    private final GraphPanel gp;
    private final ResultPanel rp;
    private final JComponent parent;

    private Node<String> blockedFrom = null;
    private Node<String> blockedTo = null;

    public ControlActions(Graph<Node<String>, Edge<Node<String>>> g,
                          GraphPanel gp,
                          ResultPanel rp,
                          JComponent parent) {
        this.g = g;
        this.gp = gp;
        this.rp = rp;
        this.parent = parent;
    }

    public void fill(JComboBox<String> start, JComboBox<String> end) {
        start.removeAllItems();
        end.removeAllItems();
        for (Node<String> n : g.nodes()) {
            start.addItem(n.id());
            end.addItem(n.id());
        }
    }

    public void routes(String startId, String endId) {
        Node<String> start = g.getNodeById(startId);
        Node<String> end   = g.getNodeById(endId);
        if (start == null || end == null) { rp.show("Start or end node not found."); return; }

        // baseline (no blocking)
        List<Node<String>> base = Dijkstra.shortestPath(g, start, end);
        if (base.isEmpty()) { rp.show("No path found."); return; }
        double baseDist = Dijkstra.pathDistance(g, base);

        // current (global temporary block)
        List<Node<String>> current = Dijkstra.shortestPath(g, start, end, blockedFrom, blockedTo);
        if (current.isEmpty()) {
            rp.show("No path found (blocked road may disconnect graph).");
            return;
        }
        double currentDist = Dijkstra.pathDistance(g, current);

        gp.highlightPath(current);

        StringBuilder sb = new StringBuilder();

        sb.append("=== MAIN SHORTEST ROUTE ===\n");
        sb.append("Path:\n").append(pathToString(base)).append("\n\n");
        sb.append("Calculation:\n").append(calculationString(base)).append(" = ").append((int) baseDist).append("\n\n");

        sb.append("=== SUCCESSOR VECTOR (Tab.1) ===\n");
        Map<Node<String>, Node<String>> successor = SuccessorVector.build(base);
        sb.append(SuccessorVector.toTableString(successor)).append("\n\n");

        if (blockedFrom != null && blockedTo != null) {
            sb.append("=== CURRENT (with blocked road ")
                    .append(blockedFrom.id()).append("-").append(blockedTo.id()).append(") ===\n");
            sb.append("Path:\n").append(pathToString(current)).append("\n\n");
            sb.append("Calculation:\n").append(calculationString(current)).append(" = ").append((int) currentDist).append("\n\n");
            sb.append("Difference from main:\n")
                    .append((int) currentDist).append(" - ").append((int) baseDist)
                    .append(" = +").append((int) (currentDist - baseDist)).append(" min\n\n");
        }

        sb.append("=== ALTERNATIVE ROUTES (TOP 3) ===\n\n");
        List<Dijkstra.Alternative<String>> alts = Dijkstra.topAlternatives(g, start, end, 3, blockedFrom, blockedTo);

        if (alts.isEmpty()) {
            sb.append("No alternative routes found.\n");
        } else {
            int idx = 1;
            for (Dijkstra.Alternative<String> alt : alts) {
                sb.append("#").append(idx++).append(" Blocked edge (")
                        .append(alt.blockedFrom.id()).append("-").append(alt.blockedTo.id()).append(")\n\n");

                sb.append("Path:\n").append(pathToString(alt.path)).append("\n\n");
                sb.append("Calculation:\n").append(calculationString(alt.path))
                        .append(" = ").append((int) alt.distance).append("\n\n");

                sb.append("Difference from main:\n")
                        .append((int) alt.distance).append(" - ").append((int) baseDist)
                        .append(" = +").append((int) (alt.distance - baseDist)).append(" min\n\n");
            }
        }

        rp.show(sb.toString());
    }

    public void addNode() {
        String id = ask("ID of new node:");
        if (id == null) return;
        g.addNode(new Node<>(id, 400, 250));
        gp.repaint();
        rp.show("Node '" + id + "' added.");
    }

    public void findNode() {
        String id = ask("Search for node ID:");
        if (id == null) return;
        rp.show(g.getNodeById(id) != null ? "Node found." : "Node not found.");
    }

    public void edge(EdgeOp op) {
        String fromId = ask("From node ID:");
        String toId   = ask("To node ID:");
        if (fromId == null || toId == null) return;

        Node<String> from = g.getNodeById(fromId);
        Node<String> to   = g.getNodeById(toId);
        if (from == null || to == null) { rp.show("Node not found."); return; }

        try {
            switch (op) {
                case REMOVE -> g.removeUndirectedEdge(from, to);
                case ADD -> g.addUndirectedEdge(from, to, Double.parseDouble(req("Weight (minutes):")));
                case EDIT_WEIGHT -> g.setUndirectedEdgeWeight(from, to, Double.parseDouble(req("New weight (minutes):")));
            }
        } catch (NumberFormatException ex) {
            rp.show("Invalid number.");
            return;
        }

        gp.repaint();
        rp.show("Edge (" + fromId + "-" + toId + ") updated.");
    }

    public void blockTemporary() {
        String fromId = ask("Block road FROM node:");
        String toId   = ask("Block road TO node:");
        if (fromId == null || toId == null) return;

        Node<String> from = g.getNodeById(fromId);
        Node<String> to   = g.getNodeById(toId);
        if (from == null || to == null) { rp.show("Node not found."); return; }

        if (Dijkstra.findEdge(g, from, to) == null) {
            rp.show("This road does not exist: " + fromId + "-" + toId);
            return;
        }

        blockedFrom = from;
        blockedTo = to;
        gp.setBlockedEdge(from, to);
        rp.show("Blocked temporarily: " + fromId + "-" + toId);
    }

    public void unblock() {
        blockedFrom = null;
        blockedTo = null;
        gp.setBlockedEdge(null, null);
        rp.show("Blocked road cleared.");
    }

    public void loadFromFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load edges.csv");
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (fc.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        try {
            GraphFileIO.loadEdgesCsv(g, f);
            gp.repaint();
            rp.show("Loaded: " + f.getName());
        } catch (Exception ex) {
            rp.show("Load failed: " + ex.getMessage());
        }
    }

    public void saveResult() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save result.txt");
        fc.setSelectedFile(new File("result.txt"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        try {
            GraphFileIO.saveText(f, rp.getText());
            rp.show("Saved: " + f.getAbsolutePath());
        } catch (Exception ex) {
            rp.show("Save failed: " + ex.getMessage());
        }
    }

    private String pathToString(List<Node<String>> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).id());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private String calculationString(List<Node<String>> path) {
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Edge<Node<String>> e = Dijkstra.findEdge(g, path.get(i), path.get(i + 1));
            if (e != null) weights.add((int) e.getWeight());
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < weights.size(); i++) {
            sb.append(weights.get(i));
            if (i < weights.size() - 1) sb.append(" + ");
        }
        return sb.toString();
    }

    private String ask(String q) {
        String s = JOptionPane.showInputDialog(parent, q);
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private String req(String q) {
        String s = ask(q);
        if (s == null) throw new NumberFormatException();
        return s;
    }
}