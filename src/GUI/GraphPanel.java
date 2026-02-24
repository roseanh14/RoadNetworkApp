package GUI;

import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// Draws the entire graph using Java2D - no external library needed
public class GraphPanel extends JPanel {

    private final Graph graph;
    private List<Node> highlightedPath = new ArrayList<>();
    private static final int NODE_RADIUS = 16;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    // Call this to highlight a path in green
    public void highlightPath(List<Node> path) {
        this.highlightedPath = path;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawEdges(g2);
        drawNodes(g2);
    }

    private void drawEdges(Graphics2D g2) {
        Set<String> drawn = new HashSet<>();
        for (Edge e : graph.getEdges()) {
            String key = e.from.name + "-" + e.to.name;
            String rev = e.to.name   + "-" + e.from.name;
            if (drawn.contains(key) || drawn.contains(rev)) continue;
            drawn.add(key);

            boolean onPath = isOnPath(e.from, e.to);
            g2.setColor(e.problematic ? Color.RED : onPath ? new Color(46, 204, 113) : Color.GRAY);
            g2.setStroke(new BasicStroke(onPath ? 3 : 1));
            g2.drawLine(e.from.x, e.from.y, e.to.x, e.to.y);

            // Weight label in the middle of the edge
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString((int) e.weight + "", (e.from.x + e.to.x) / 2, (e.from.y + e.to.y) / 2 - 3);
        }
    }

    private void drawNodes(Graphics2D g2) {
        for (Node node : graph.getNodes()) {
            boolean onPath = highlightedPath.contains(node);
            int x = node.x - NODE_RADIUS, y = node.y - NODE_RADIUS, d = NODE_RADIUS * 2;

            g2.setColor(onPath ? new Color(46, 204, 113) : new Color(74, 144, 217));
            g2.fillOval(x, y, d, d);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, d, d);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(node.name, node.x - fm.stringWidth(node.name) / 2, node.y + 5);
        }
    }

    private boolean isOnPath(Node from, Node to) {
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            Node a = highlightedPath.get(i), b = highlightedPath.get(i + 1);
            if ((a.equals(from) && b.equals(to)) || (a.equals(to) && b.equals(from))) return true;
        }
        return false;
    }
}