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
import java.util.*;
import java.util.List;

public class ControlActions {

    public enum EdgeOp { EDIT_WEIGHT, ADD, REMOVE }

    private static final int ALT_LIMIT = 3;

    private final Graph<Node<String>, Edge<Node<String>>> g;
    private final GraphPanel gp;
    private final ResultPanel rp;
    private final JComponent parent;

    private final Set<String> blockedEdges = new HashSet<>();

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
        Node<String> start = getExistingNode(startId, "Start node not found.");
        Node<String> end = getExistingNode(endId, "End node not found.");
        if (start == null || end == null) return;

        List<Node<String>> base = Dijkstra.shortestPath(g, start, end);
        if (base.isEmpty()) {
            rp.show("No path found.");
            return;
        }
        double baseDist = Dijkstra.pathDistance(g, base);

        List<Node<String>> current = shortestPathWithBlocks(start, end, blockedEdges);
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
        Map<Node<String>, Node<String>> successor = SuccessorVector.build(base);
        sb.append(SuccessorVector.toTableString(successor)).append("\n");

        if (!blockedEdges.isEmpty()) {
            sb.append("\n=== CURRENT ROUTE (with blocked roads: ").append(blockedEdges).append(") ===\n");
            appendRouteBlock(sb, current, currentDist);
            appendDiffBlock(sb, "Difference from main:", currentDist, baseDist);
        }

        sb.append("\n=== ALTERNATIVE ROUTES (TOP ").append(ALT_LIMIT).append(") ===\n\n");

        List<Alt> alts = topAlternativesWithBlocks(start, end, blockedEdges);
        if (alts.isEmpty()) {
            sb.append("No alternative routes found.\n");
        } else {
            appendAlternatives(sb, alts, currentDist);
        }

        rp.show(sb.toString());
    }

    private record Alt(String blockedKey, List<Node<String>> path, double distance) {}

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

    private List<Node<String>> shortestPathWithBlocks(Node<String> start, Node<String> end, Set<String> blocks) {
        return Dijkstra.shortestPath(g, start, end, (a, b) -> blocks.contains(GraphPanel.edgeKey(a, b)));
    }

    private List<Alt> topAlternativesWithBlocks(Node<String> start, Node<String> end, Set<String> globalBlocks) {
        List<Node<String>> cur = shortestPathWithBlocks(start, end, globalBlocks);
        if (cur.isEmpty()) return List.of();

        String curKey = pathKey(cur);

        List<Alt> out = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>();

        for (int i = 0; i < cur.size() - 1; i++) {
            String extraBlock = GraphPanel.edgeKey(cur.get(i), cur.get(i + 1));
            if (globalBlocks.contains(extraBlock)) continue;

            Set<String> twoBlocks = new HashSet<>(globalBlocks);
            twoBlocks.add(extraBlock);

            List<Node<String>> altPath = shortestPathWithBlocks(start, end, twoBlocks);
            if (altPath.isEmpty()) continue;

            String k = pathKey(altPath);
            if (k.equals(curKey)) continue;
            if (!seenPaths.add(k)) continue;

            double dist = Dijkstra.pathDistance(g, altPath);
            out.add(new Alt(extraBlock, altPath, dist));
        }

        out.sort(Comparator.comparingDouble(Alt::distance));
        return out.size() > ALT_LIMIT ? out.subList(0, ALT_LIMIT) : out;
    }


    public void addNode() {
        String id = ask("ID of new node:");
        if (id == null) return;

        if (g.getNodeById(id) != null) {
            rp.show("Node '" + id + "' already exists.");
            return;
        }

        gp.beginAddNode(id);
        rp.show("Click in the graph to place node '" + id + "'.");
    }

    public void findNode() {
        String id = ask("Search for node ID:");
        if (id == null) return;

        Node<String> n = g.getNodeById(id);
        if (n == null) {
            gp.setSelectedNode(null);
            rp.show("Node not found: " + id);
            return;
        }

        gp.setSelectedNode(n);
        rp.show("Node found and selected: " + id);
    }

    public void removeNode() {
        Node<String> sel = gp.getSelectedNode();
        String id = (sel != null) ? sel.id() : ask("ID of node to remove:");
        if (id == null) return;

        Node<String> n = getExistingNode(id, "Node not found: " + id);
        if (n == null) return;

        boolean ok = g.removeNode(n);
        if (!ok) {
            rp.show("Remove failed: " + id);
            return;
        }

        blockedEdges.removeIf(k -> k.startsWith(id + "-") || k.endsWith("-" + id));
        gp.setSelectedNode(null);
        refreshGraphUI(true);

        rp.show("Node removed: " + id);
    }


    public void edge(EdgeOp op) {
        NodePair pair = askEdgeNodes("From node ID:", "To node ID:");
        if (pair == null) return;

        Node<String> from = pair.from();
        Node<String> to = pair.to();

        try {
            switch (op) {
                case REMOVE -> {
                    g.removeUndirectedEdge(from, to);
                    blockedEdges.remove(GraphPanel.edgeKey(from, to));
                    refreshGraphUI(false);
                }
                case ADD -> {
                    double w = Double.parseDouble(req("Weight (minutes):"));
                    g.addUndirectedEdge(from, to, w);
                    refreshGraphUI(false);
                }
                case EDIT_WEIGHT -> {
                    double w = Double.parseDouble(req("New weight (minutes):"));
                    g.setUndirectedEdgeWeight(from, to, w);
                    refreshGraphUI(false);
                }
            }
        } catch (NumberFormatException ex) {
            rp.show("Invalid number.");
            return;
        }

        rp.show("Edge (" + from.id() + "-" + to.id() + ") updated.");
    }


    public void blockTemporary() {
        NodePair pair = askEdgeNodes("Block road FROM node:", "Block road TO node:");
        if (pair == null) return;

        if (Dijkstra.findEdge(g, pair.from(), pair.to()) == null) {
            rp.show("This road does not exist: " + pair.from().id() + "-" + pair.to().id());
            return;
        }

        blockedEdges.add(GraphPanel.edgeKey(pair.from(), pair.to()));
        refreshGraphUI(false);

        rp.show("Blocked: " + pair.from().id() + "-" + pair.to().id() + "\nAll blocked roads: " + blockedEdges);
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
        if (p.length != 2) {
            rp.show("Invalid format. Use A-B (example: k-s).");
            return;
        }

        Node<String> a = getExistingNode(p[0].trim(), "Node not found in: " + s);
        Node<String> b = getExistingNode(p[1].trim(), "Node not found in: " + s);
        if (a == null || b == null) return;

        blockedEdges.remove(GraphPanel.edgeKey(a, b));
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

    private record NodePair(Node<String> from, Node<String> to) {}

    private NodePair askEdgeNodes(String qFrom, String qTo) {
        String fromId = ask(qFrom);
        String toId = ask(qTo);
        if (fromId == null || toId == null) return null;

        Node<String> from = getExistingNode(fromId, "Node not found: " + fromId);
        Node<String> to = getExistingNode(toId, "Node not found: " + toId);
        if (from == null || to == null) return null;

        return new NodePair(from, to);
    }

    private Node<String> getExistingNode(String id, String errorMsg) {
        if (id == null) return null;
        Node<String> n = g.getNodeById(id);
        if (n == null) rp.show(errorMsg);
        return n;
    }

    private void appendRouteBlock(StringBuilder sb, List<Node<String>> path, double dist) {
        sb.append("Path:\n").append(pathToString(path)).append("\n\n");
        sb.append("Calculation:\n").append(calculationString(path))
                .append(" = ").append((int) dist).append("\n\n");
    }

    private void appendDiffBlock(StringBuilder sb, String title, double a, double b) {
        sb.append(title).append("\n")
                .append((int) a).append(" - ").append((int) b)
                .append(" = ").append((int) (a - b)).append(" min\n\n");
    }

    private String calculationString(List<Node<String>> path) {
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Edge<Node<String>> e = Dijkstra.findEdge(g, path.get(i), path.get(i + 1));
            if (e != null) weights.add((int) e.getWeight());
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

    private static String pathToDelimited(List<Node<String>> path, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).id());
            if (i < path.size() - 1) sb.append(delimiter);
        }
        return sb.toString();
    }

    private String pathToString(List<Node<String>> path) {
        return pathToDelimited(path, " -> ");
    }

    private static String pathKey(List<Node<String>> path) {
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