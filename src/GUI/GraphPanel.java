package GUI;

import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    private final Graph<Node<String>, Edge<Node<String>>> graph;

    private List<Node<String>> highlightedPath = new ArrayList<>();

    // temporary block (only for drawing)
    private Node<String> blockedFrom = null;
    private Node<String> blockedTo = null;

    private static final int NODE_RADIUS = 12;

    public GraphPanel(Graph<Node<String>, Edge<Node<String>>> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    public void highlightPath(List<Node<String>> path) {
        this.highlightedPath = (path == null) ? new ArrayList<>() : new ArrayList<>(path);
        repaint();
    }

    // this is what ControlActions calls
    public void setBlockedEdge(Node<String> from, Node<String> to) {
        this.blockedFrom = from;
        this.blockedTo = to;
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

        for (Edge<Node<String>> e : graph.edges()) {
            Node<String> from = e.getFrom();
            Node<String> to   = e.getTo();

            String key = from.id() + "-" + to.id();
            String rev = to.id() + "-" + from.id();
            if (drawn.contains(key) || drawn.contains(rev)) continue;
            drawn.add(key);

            boolean onPath = isOnPath(from, to);
            boolean isBlocked = isBlocked(from, to);

            // color
            if (isBlocked) g2.setColor(Color.RED);
            else if (onPath) g2.setColor(new Color(46, 204, 113));
            else g2.setColor(Color.GRAY);

            // stroke
            float stroke = isBlocked ? 4f : (onPath ? 3f : 1f);
            g2.setStroke(new BasicStroke(stroke));

            int x1 = from.x(), y1 = from.y();
            int x2 = to.x(),   y2 = to.y();

            g2.drawLine(x1, y1, x2, y2);

            // weight label in middle
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf((int) e.getWeight()), midX + 2, midY - 2);
        }
    }

    private void drawNodes(Graphics2D g2) {
        int d = NODE_RADIUS * 2;

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (Node<String> node : graph.nodes()) {
            boolean onPath = highlightedPath.contains(node);

            int cx = node.x(), cy = node.y();
            int x = cx - NODE_RADIUS, y = cy - NODE_RADIUS;

            g2.setColor(onPath ? new Color(46, 204, 113) : new Color(74, 144, 217));
            g2.fillOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, d, d);

            String label = String.valueOf(node.id());
            g2.setColor(Color.WHITE);
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + 5);
        }
    }

    private boolean isOnPath(Node<String> from, Node<String> to) {
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            Node<String> a = highlightedPath.get(i);
            Node<String> b = highlightedPath.get(i + 1);
            if ((a.equals(from) && b.equals(to)) || (a.equals(to) && b.equals(from))) return true;
        }
        return false;
    }

    private boolean isBlocked(Node<String> a, Node<String> b) {
        if (blockedFrom == null || blockedTo == null) return false;
        return (a.equals(blockedFrom) && b.equals(blockedTo)) ||
                (a.equals(blockedTo) && b.equals(blockedFrom));
    }
}