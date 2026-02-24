package GUI;

import Algorithm.Dijkstra;
import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ControlPanel extends JPanel {

    private final Graph graph;
    private final GraphPanel graphPanel;
    private final ResultPanel resultPanel;
    private final JComboBox<String> startBox;
    private final JComboBox<String> endBox;

    private static final Color DARK  = new Color(30, 40, 60);
    private static final Color DARK2 = new Color(40, 52, 75);

    public ControlPanel(Graph graph, GraphPanel graphPanel, ResultPanel resultPanel) {
        this.graph       = graph;
        this.graphPanel  = graphPanel;
        this.resultPanel = resultPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(DARK);
        setPreferredSize(new Dimension(220, 600));

        // Dropdowns with all nodes
        startBox = new JComboBox<>();
        endBox   = new JComboBox<>();
        for (Node n : graph.getNodes()) {
            startBox.addItem(n.name);
            endBox.addItem(n.name);
        }
        startBox.setSelectedItem("z");
        endBox.setSelectedItem("w");

        add(buildRouteSection());
        add(buildNodeSection());
        add(buildEdgeSection());
    }

    // Section 1: route calculation
    private JPanel buildRouteSection() {
        JPanel p = darkPanel("1. Calculate Alternative Routes");

        p.add(whiteLabel("Start node:"));
        p.add(startBox);
        p.add(Box.createVerticalStrut(5));
        p.add(whiteLabel("End node:"));
        p.add(endBox);
        p.add(Box.createVerticalStrut(10));

        JButton btn = new JButton("Calculate Routes");
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> calculateRoutes());
        p.add(btn);

        return p;
    }

    // Section 2: node management
    private JPanel buildNodeSection() {
        JPanel p = darkPanel("2. Manage Nodes (Villages)");
        p.add(darkButton("Add Node",  () -> addNode()));
        p.add(Box.createVerticalStrut(5));
        p.add(darkButton("Find Node", () -> findNode()));
        return p;
    }

    // Section 3: edge management
    private JPanel buildEdgeSection() {
        JPanel p = darkPanel("3. Manage Roads (Edges)");
        p.add(darkButton("Mark as Problematic", () -> markEdge(true)));
        p.add(Box.createVerticalStrut(5));
        p.add(darkButton("Unmark Problematic",  () -> markEdge(false)));
        p.add(Box.createVerticalStrut(5));
        p.add(darkButton("Edit Weight",         () -> editWeight()));
        p.add(Box.createVerticalStrut(5));
        p.add(darkButton("Add Road",            () -> addEdge()));
        p.add(Box.createVerticalStrut(5));
        p.add(darkButton("Remove Road",         () -> removeEdge()));
        return p;
    }

    // Main calculation: finds main path + alternatives
    private void calculateRoutes() {
        Node start = graph.getNode((String) startBox.getSelectedItem());
        Node end   = graph.getNode((String) endBox.getSelectedItem());

        List<Node> mainPath = Dijkstra.shortestPath(graph, start, end, null);
        if (mainPath.isEmpty()) { resultPanel.show("No path found."); return; }

        graphPanel.highlightPath(mainPath);
        double mainDist = Dijkstra.pathDistance(graph, mainPath);

        StringBuilder sb = new StringBuilder();
        sb.append("=== MAIN SHORTEST ROUTE ===\n");
        sb.append(pathToString(mainPath)).append("\n");
        sb.append("Total time: ").append((int) mainDist).append(" min\n\n");
        sb.append("=== ALTERNATIVE ROUTES ===\n\n");

        boolean found = false;
        for (int i = 0; i < mainPath.size() - 1; i++) {
            Node from = mainPath.get(i), to = mainPath.get(i + 1);
            Edge edge = Dijkstra.findEdge(graph, from, to);
            if (edge != null && edge.problematic) {
                found = true;
                sb.append("Blocking edge (").append(from.name).append("-").append(to.name).append("):\n");
                List<Node> alt = Dijkstra.shortestPath(graph, start, end, edge);
                if (alt.isEmpty()) { sb.append("  -> No alternative exists!\n\n"); continue; }
                double altDist = Dijkstra.pathDistance(graph, alt);
                sb.append("  -> ").append(pathToString(alt)).append("\n");
                sb.append("  -> Time: ").append((int) altDist).append(" min (+").append((int)(altDist - mainDist)).append(" min)\n\n");
            }
        }
        if (!found) sb.append("No problematic edges on main route.\n");
        resultPanel.show(sb.toString());
    }

    private void addNode() {
        String name = ask("Name of new node:");
        if (name == null) return;
        graph.addNode(new Node(name, 400, 250));
        startBox.addItem(name);
        endBox.addItem(name);
        resultPanel.show("Node '" + name + "' added.");
    }

    private void findNode() {
        String name = ask("Search for node:");
        if (name == null) return;
        Node n = graph.getNode(name);
        resultPanel.show(n != null ? "Node '" + name + "' found." : "Node '" + name + "' not found.");
    }

    private void markEdge(boolean value) {
        String from = ask("From node:"), to = ask("To node:");
        if (from == null || to == null) return;
        graph.setProblematic(graph.getNode(from), graph.getNode(to), value);
        graphPanel.repaint();
        resultPanel.show("Edge (" + from + "-" + to + ") " + (value ? "marked as problematic." : "unmarked."));
    }

    private void editWeight() {
        String from = ask("From node:"), to = ask("To node:"), w = ask("New weight (minutes):");
        if (from == null || to == null || w == null) return;
        try {
            graph.setEdgeWeight(graph.getNode(from), graph.getNode(to), Double.parseDouble(w));
            graphPanel.repaint();
            resultPanel.show("Edge (" + from + "-" + to + ") weight changed to " + w + " min.");
        } catch (NumberFormatException ex) { resultPanel.show("Invalid number."); }
    }

    private void addEdge() {
        String from = ask("From node:"), to = ask("To node:"), w = ask("Weight (minutes):");
        if (from == null || to == null || w == null) return;
        try {
            graph.addEdge(graph.getNode(from), graph.getNode(to), Double.parseDouble(w));
            graphPanel.repaint();
            resultPanel.show("Edge (" + from + "-" + to + ") added.");
        } catch (NumberFormatException ex) { resultPanel.show("Invalid number."); }
    }

    private void removeEdge() {
        String from = ask("From node:"), to = ask("To node:");
        if (from == null || to == null) return;
        graph.removeEdge(graph.getNode(from), graph.getNode(to));
        graphPanel.repaint();
        resultPanel.show("Edge (" + from + "-" + to + ") removed.");
    }

    private String pathToString(List<Node> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).name);
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private String ask(String question) {
        String s = JOptionPane.showInputDialog(this, question);
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private JPanel darkPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DARK2);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 100, 130)),
                        title, 0, 0, new Font("Arial", Font.BOLD, 11), Color.LIGHT_GRAY),
                new EmptyBorder(5, 8, 8, 8)
        ));
        p.setMaximumSize(new Dimension(220, 300));
        return p;
    }

    private JButton darkButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setBackground(new Color(55, 70, 95));
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 28));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JLabel whiteLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}