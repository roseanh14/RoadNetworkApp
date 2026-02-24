package GUI;

import Algorithm.Dijkstra;
import Data.GraphFileIO;
import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControlActions {

    public enum EdgeOp { EDIT_WEIGHT, ADD, REMOVE }

    private final Graph<Node<String>, Edge<Node<String>>> g;
    private final GraphPanel gp;
    private final ResultPanel rp;
    private final JComponent parent;

    private String lastResult = "";

    private Node<String> blockedFrom = null;
    private Node<String> blockedTo = null;

    public ControlActions(Graph<Node<String>, Edge<Node<String>>> g, GraphPanel gp, ResultPanel rp, JComponent parent) {
        this.g = g; this.gp = gp; this.rp = rp; this.parent = parent;
    }

    public void fill(JComboBox<String> start, JComboBox<String> end) {
        start.removeAllItems();
        end.removeAllItems();
        for (Node<String> n : g.nodes()) {
            String id = String.valueOf(n.getId());
            start.addItem(id);
            end.addItem(id);
        }
    }

    public void addNode() {
        String id = ask("ID of new node:");
        if (id == null) return;

        g.addNode(new Node<>(id, 400, 250));
        rp.show("Node '" + id + "' added.");
        gp.repaint();
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
        if (lastResult == null || lastResult.isBlank()) {
            rp.show("Nothing to save (run Calculate Routes first).");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save result.txt");
        fc.setSelectedFile(new File("result.txt"));

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        try {
            GraphFileIO.saveText(f, lastResult);
            rp.show("Saved: " + f.getAbsolutePath());
        } catch (Exception ex) {
            rp.show("Save failed: " + ex.getMessage());
        }
    }

    // ---------- ROUTES ----------
    public void routes(String startId, String endId) {
        Node<String> start = g.getNodeById(startId);
        Node<String> end   = g.getNodeById(endId);

        if (start == null || end == null) { rp.show("Start or end node not found."); return; }

        // baseline main route = without block
        List<Node<String>> baseline = Dijkstra.shortestPath(g, start, end);
        if (baseline.isEmpty()) { rp.show("No path found."); return; }
        double baselineDist = Dijkstra.pathDistance(g, baseline);

        // current shortest = with GUI block (if any)
        List<Node<String>> current = Dijkstra.shortestPath(g, start, end, blockedFrom, blockedTo);
        if (current.isEmpty()) {
            String msg = "No path found";
            if (blockedFrom != null) msg += " (because blocked road " + blockedFrom.getId() + "-" + blockedTo.getId() + ")";
            rp.show(msg + ".");
            return;
        }
        double currentDist = Dijkstra.pathDistance(g, current);

        // highlight current
        gp.highlightPath(current);

        StringBuilder sb = new StringBuilder();
        sb.append("=== MAIN SHORTEST ROUTE (baseline) ===\n");
        sb.append("Path:\n").append(pathToString(baseline)).append("\n\n");
        sb.append("Calculation:\n").append(calculationString(baseline)).append(" = ").append((int)baselineDist).append("\n\n");

        if (blockedFrom != null && blockedTo != null) {
            sb.append("=== CURRENT SHORTEST ROUTE (with blocked road ")
                    .append(blockedFrom.getId()).append("-").append(blockedTo.getId()).append(") ===\n");
            sb.append("Path:\n").append(pathToString(current)).append("\n\n");
            sb.append("Calculation:\n").append(calculationString(current)).append(" = ").append((int)currentDist).append("\n\n");
            sb.append("Difference from baseline:\n")
                    .append((int)currentDist).append(" - ").append((int)baselineDist)
                    .append(" = +").append((int)(currentDist - baselineDist)).append(" min\n\n");
        }

        sb.append("=== ALTERNATIVE ROUTES (TOP 3) ===\n\n");

        List<Dijkstra.Alternative<String>> alts = Dijkstra.topAlternatives(g, start, end, 3, blockedFrom, blockedTo);

        if (alts.isEmpty()) {
            sb.append("No alternative routes found.\n");
        } else {
            int idx = 1;
            for (Dijkstra.Alternative<String> alt : alts) {
                sb.append("#").append(idx++).append(" Blocked edge (")
                        .append(alt.blockedFrom.getId()).append("-").append(alt.blockedTo.getId()).append(")\n\n");

                sb.append("Path:\n").append(pathToString(alt.path)).append("\n\n");
                sb.append("Calculation:\n").append(calculationString(alt.path))
                        .append(" = ").append((int)alt.distance).append("\n\n");

                sb.append("Difference from baseline:\n")
                        .append((int)alt.distance).append(" - ").append((int)baselineDist)
                        .append(" = +").append((int)(alt.distance - baselineDist)).append(" min\n\n");
            }
        }

        lastResult = sb.toString();
        rp.show(lastResult);
    }

    // ---------- BLOCK / UNBLOCK ----------
    public void blockTemporary() {
        String fromId = ask("Block road FROM node:");
        String toId   = ask("Block road TO node:");
        if (fromId == null || toId == null) return;

        Node<String> from = g.getNodeById(fromId);
        Node<String> to   = g.getNodeById(toId);

        if (from == null || to == null) {
            rp.show("Node not found.");
            return;
        }

        // check edge exists
        if (Dijkstra.findEdge(g, from, to) == null) {
            rp.show("This road does not exist: " + fromId + "-" + toId);
            return;
        }

        blockedFrom = from;
        blockedTo = to;

        gp.setBlockedEdge(from, to);
        rp.show("Blocked road temporarily: " + fromId + "-" + toId);
    }

    public void unblock() {
        blockedFrom = null;
        blockedTo = null;
        gp.setBlockedEdge(null, null);
        rp.show("Blocked road cleared.");
    }

    private String pathToString(List<Node<String>> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getId());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private String calculationString(List<Node<String>> path) {
        List<Integer> weights = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            Node<String> a = path.get(i);
            Node<String> b = path.get(i + 1);

            Edge<Node<String>> e = Dijkstra.findEdge(g, a, b);
            if (e != null) weights.add((int)e.getWeight());
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