package GUI;

import Algorithm.Dijkstra;
import Algorithm.SuccessorVector;
import Data.GraphFileIO;
import Model.Graph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Point;
import java.io.File;
import java.util.*;
import java.util.List;

public class ControlActions {

    public enum EdgeOp { EDIT_WEIGHT, ADD, REMOVE }
    private static final int ALT_LIMIT = 3;

    private final Graph<String, Point, Double> g;
    private final GraphPanel gp;
    private final ResultPanel rp;
    private final JComponent parent;

    private JComboBox<String> startBox;
    private JComboBox<String> endBox;

    private final Set<String> blockedEdges = new HashSet<>();

    public ControlActions(Graph<String, Point, Double> g,
                          GraphPanel gp,
                          ResultPanel rp,
                          JComponent parent) {
        this.g = g;
        this.gp = gp;
        this.rp = rp;
        this.parent = parent;
    }

    public void fill(JComboBox<String> start, JComboBox<String> end) {
        this.startBox = start;
        this.endBox   = end;
        start.removeAllItems();
        end.removeAllItems();
        for (var v : g.vertices()) {
            start.addItem(String.valueOf(v.key()));
            end.addItem(String.valueOf(v.key()));
        }
    }

    public void routes(String startId, String endId) {
        var start = getExistingVertex(startId, "Start node not found.");
        var end   = getExistingVertex(endId,   "End node not found.");
        if (start == null || end == null) return;

        List<Graph<String, Point, Double>.Vertex> base = Dijkstra.shortestPath(g, start, end);
        if (base.isEmpty()) { rp.show("No path found."); return; }
        double baseDist = Dijkstra.pathDistance(g, base);

        List<Graph<String, Point, Double>.Vertex> current = shortestPathWithBlocks(start, end, blockedEdges);
        if (current.isEmpty()) {
            rp.show("No path found (blocked roads may disconnect graph).");
            gp.highlightPath(List.of());
            return;
        }
        double currentDist = Dijkstra.pathDistance(g, current);

        gp.setBlockedEdges(blockedEdges);
        gp.highlightPath(current);

        StringBuilder sb = new StringBuilder();

        sb.append("=== MAIN SHORTEST ROUTE (no blocks) ===\n");
        appendRouteBlock(sb, base, baseDist);

        sb.append("\n=== SUCCESSOR VECTOR (Tab.1) ===\n");
        List<List<Graph<String, Point, Double>.Vertex>> allPaths = new ArrayList<>();
        allPaths.add(base);

        List<Alt> altsForVector = topAlternativesFromBase(start, end, base, blockedEdges);
        for (Alt alt : altsForVector) allPaths.add(alt.path());

        var sv = SuccessorVector.buildAll(allPaths);
        sb.append(SuccessorVector.toTableStringAll(sv)).append("\n");

        if (!blockedEdges.isEmpty()) {
            sb.append("\n=== CURRENT ROUTE (with blocked roads: ").append(blockedEdges).append(") ===\n");
            appendRouteBlock(sb, current, currentDist);
            appendDiffBlock(sb, "Difference from main:", currentDist, baseDist);
        }

        sb.append("\n=== ALTERNATIVE ROUTES (TOP ").append(ALT_LIMIT).append(") ===\n\n");

        List<Alt> alts = topAlternativesFromBase(start, end, base, blockedEdges);
        if (alts.isEmpty()) sb.append("No alternative routes found.\n");
        else appendAlternatives(sb, alts, currentDist);

        rp.show(sb.toString());
    }

    private record Alt(String blockedKey, List<Graph<String, Point, Double>.Vertex> path, double distance) {}

    private void appendAlternatives(StringBuilder sb, List<Alt> alts, double currentDist) {
        int idx = 1;
        for (Alt alt : alts) {
            sb.append("#").append(idx++)
                    .append(" Extra blocked edge (").append(alt.blockedKey()).append(")\n\n");
            appendRouteBlock(sb, alt.path(), alt.distance());
            appendDiffBlock(sb, "Difference from CURRENT:", alt.distance(), currentDist);
            sb.append("\n");
        }
    }

    private List<Graph<String, Point, Double>.Vertex> shortestPathWithBlocks(
            Graph<String, Point, Double>.Vertex start,
            Graph<String, Point, Double>.Vertex end,
            Set<String> blocks) {

        return Dijkstra.shortestPath(g, start, end,
                (a, b) -> blocks.contains(Graph.edgeKey(a, b)));
    }

    private List<Alt> topAlternativesFromBase(
            Graph<String, Point, Double>.Vertex start,
            Graph<String, Point, Double>.Vertex end,
            List<Graph<String, Point, Double>.Vertex> base,
            Set<String> globalBlocks) {

        if (base.isEmpty()) return List.of();
        String baseKey = pathKey(base);

        List<Alt> out = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>();

        for (int i = 0; i < base.size() - 1; i++) {
            String extraBlock = Graph.edgeKey(base.get(i), base.get(i + 1));
            if (globalBlocks.contains(extraBlock)) continue;

            Set<String> twoBlocks = new HashSet<>(globalBlocks);
            twoBlocks.add(extraBlock);

            var altPath = shortestPathWithBlocks(start, end, twoBlocks);
            if (altPath.isEmpty()) continue;

            String k = pathKey(altPath);
            if (k.equals(baseKey)) continue;
            if (!seenPaths.add(k)) continue;

            out.add(new Alt(extraBlock, altPath, Dijkstra.pathDistance(g, altPath)));
        }

        out.sort(Comparator.comparingDouble(Alt::distance));
        return out.size() > ALT_LIMIT ? out.subList(0, ALT_LIMIT) : out;
    }

    public void addNode() {
        String id = ask("ID of new node:");
        if (id == null) return;

        if (g.getVertex(id) != null) {
            rp.show("Node '" + id + "' already exists.");
            return;
        }

        gp.beginAddVertex(id);
        gp.setOnVertexAdded(() -> {
            if (startBox != null && endBox != null) fill(startBox, endBox);
        });
        rp.show("Click in the graph to place node '" + id + "'.");
    }

    public void findNode() {
        String id = ask("Search for node ID:");
        if (id == null) return;

        var v = g.getVertex(id);
        if (v == null) {
            gp.setSelectedVertex(null);
            rp.show("Node not found: " + id);
            return;
        }

        gp.setSelectedVertex(v);
        rp.show("Node found and selected: " + id);
    }

    public void removeNode() {
        var sel = gp.getSelectedVertex();
        String id = (sel != null) ? String.valueOf(sel.key()) : ask("ID of node to remove:");
        if (id == null) return;

        var v = getExistingVertex(id, "Node not found: " + id);
        if (v == null) return;

        if (!g.removeVertex(v)) { rp.show("Remove failed: " + id); return; }

        blockedEdges.removeIf(k -> k.startsWith(id + "-") || k.endsWith("-" + id));
        gp.setSelectedVertex(null);
        refreshGraphUI(true);

        if (startBox != null && endBox != null) fill(startBox, endBox);
        rp.show("Node removed: " + id);
    }

    public void edge(EdgeOp op) {
        VertexPair pair = askEdgeVertices("From node ID:", "To node ID:");
        if (pair == null) return;

        var from = pair.from();
        var to   = pair.to();

        try {
            switch (op) {
                case REMOVE -> {
                    g.removeUndirectedEdge(from, to);
                    blockedEdges.remove(Graph.edgeKey(from, to));
                    refreshGraphUI(false);
                }
                case ADD -> {
                    double w = Double.parseDouble(req("Weight (minutes):"));
                    g.addUndirectedEdge(from, to, w);
                    refreshGraphUI(false);
                }
                case EDIT_WEIGHT -> {
                    double w = Double.parseDouble(req("New weight (minutes):"));
                    g.setUndirectedEdgeData(from, to, w);
                    refreshGraphUI(false);
                }
            }
        } catch (NumberFormatException ex) {
            rp.show("Invalid number.");
            return;
        }

        rp.show("Edge (" + from.key() + "-" + to.key() + ") updated.");
    }

    public void blockTemporary() {
        VertexPair pair = askEdgeVertices("Block road FROM node:", "Block road TO node:");
        if (pair == null) return;

        if (Dijkstra.findEdge(g, pair.from(), pair.to()) == null) {
            rp.show("This road does not exist: " + pair.from().key() + "-" + pair.to().key());
            return;
        }

        blockedEdges.add(Graph.edgeKey(pair.from(), pair.to()));
        refreshGraphUI(false);
        rp.show("Blocked: " + pair.from().key() + "-" + pair.to().key()
                + "\nAll blocked roads: " + blockedEdges);
    }

    public void unblock() {
        String s = JOptionPane.showInputDialog(parent,
                "Unblock road (format A-B). Leave empty to clear ALL blocks:");
        if (s == null) return;
        s = s.trim();

        if (s.isEmpty()) {
            blockedEdges.clear();
            refreshGraphUI(false);
            rp.show("All blocked roads cleared.");
            return;
        }

        String[] p = s.split("-");
        if (p.length != 2) { rp.show("Invalid format. Use A-B (example: k-s)."); return; }

        var a = getExistingVertex(p[0].trim(), "Node not found in: " + s);
        var b = getExistingVertex(p[1].trim(), "Node not found in: " + s);
        if (a == null || b == null) return;

        blockedEdges.remove(Graph.edgeKey(a, b));
        refreshGraphUI(false);
        rp.show("Unblocked: " + s + "\nAll blocked roads: " + blockedEdges);
    }

    public void loadFromFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load edges.csv");
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (fc.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        try {
            GraphFileIO.loadEdgesCsv(g, f);
            refreshGraphUI(false);
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

    private void refreshGraphUI(boolean clearPath) {
        gp.setBlockedEdges(blockedEdges);
        if (clearPath) gp.highlightPath(List.of());
        gp.repaint();
    }

    private record VertexPair(Graph<String, Point, Double>.Vertex from,
                              Graph<String, Point, Double>.Vertex to) {}

    private VertexPair askEdgeVertices(String qFrom, String qTo) {
        String fromId = ask(qFrom);
        String toId   = ask(qTo);
        if (fromId == null || toId == null) return null;

        var from = getExistingVertex(fromId, "Node not found: " + fromId);
        var to   = getExistingVertex(toId,   "Node not found: " + toId);
        if (from == null || to == null) return null;

        return new VertexPair(from, to);
    }

    private Graph<String, Point, Double>.Vertex getExistingVertex(String id, String errorMsg) {
        if (id == null) return null;
        var v = g.getVertex(id);
        if (v == null) rp.show(errorMsg);
        return v;
    }

    private void appendRouteBlock(StringBuilder sb, List<Graph<String, Point, Double>.Vertex> path, double dist) {
        sb.append("Path:\n").append(pathToString(path)).append("\n\n");
        sb.append("Calculation:\n").append(calculationString(path))
                .append(" = ").append((int) dist).append("\n\n");
    }

    private void appendDiffBlock(StringBuilder sb, String title, double a, double b) {
        sb.append(title).append("\n")
                .append((int) a).append(" - ").append((int) b)
                .append(" = ").append((int)(a - b)).append(" min\n\n");
    }

    private String calculationString(List<Graph<String, Point, Double>.Vertex> path) {
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            var e = Dijkstra.findEdge(g, path.get(i), path.get(i + 1));
            if (e != null) weights.add((int) Math.round(e.data()));
        }
        return joinWithPlus(weights);
    }

    private static String joinWithPlus(List<Integer> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i));
            if (i < items.size() - 1) sb.append(" + ");
        }
        return sb.toString();
    }

    private static String pathToDelimited(List<Graph<String, Point, Double>.Vertex> path, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).key());
            if (i < path.size() - 1) sb.append(delimiter);
        }
        return sb.toString();
    }

    private String pathToString(List<Graph<String, Point, Double>.Vertex> path) {
        return pathToDelimited(path, " -> ");
    }

    private static String pathKey(List<Graph<String, Point, Double>.Vertex> path) {
        return pathToDelimited(path, "->");
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