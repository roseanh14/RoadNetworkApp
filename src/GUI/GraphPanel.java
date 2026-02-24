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

    private Node<String> blockedFrom = null;
    private Node<String> blockedTo = null;

    private static final int BASE_NODE_RADIUS = 16;
    private static final int PADDING = 40;

    // transform
    private double minX, maxX, minY, maxY;
    private double scale = 1.0;
    private int offsetX = 0, offsetY = 0;

    public GraphPanel(Graph<Node<String>, Edge<Node<String>>> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    public void highlightPath(List<Node<String>> path) {
        this.highlightedPath = (path == null) ? new ArrayList<>() : new ArrayList<>(path);
        repaint();
    }

    // show blocked road in red
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

        computeTransform();
        drawEdges(g2);
        drawNodes(g2);
    }

    private void computeTransform() {
        minX = Double.POSITIVE_INFINITY; maxX = Double.NEGATIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY; maxY = Double.NEGATIVE_INFINITY;

        boolean any = false;
        for (Node<String> n : graph.nodes()) {
            any = true;
            minX = Math.min(minX, n.getX());
            maxX = Math.max(maxX, n.getX());
            minY = Math.min(minY, n.getY());
            maxY = Math.max(maxY, n.getY());
        }

        if (!any) { scale = 1.0; offsetX = offsetY = PADDING; return; }

        double w = Math.max(1.0, maxX - minX);
        double h = Math.max(1.0, maxY - minY);

        int panelW = Math.max(1, getWidth()  - 2 * PADDING);
        int panelH = Math.max(1, getHeight() - 2 * PADDING);

        scale = Math.min(panelW / w, panelH / h);
        scale = Math.min(scale, 1.8);

        offsetX = PADDING - (int) Math.round(minX * scale);
        offsetY = PADDING - (int) Math.round(minY * scale);
    }

    private int sx(Node<String> n) { return offsetX + (int) Math.round(n.getX() * scale); }
    private int sy(Node<String> n) { return offsetY + (int) Math.round(n.getY() * scale); }

    private int nodeRadius() {
        int r = (int) Math.round(BASE_NODE_RADIUS * scale);
        return Math.max(10, Math.min(22, r));
    }

    private void drawEdges(Graphics2D g2) {
        Set<String> drawn = new HashSet<>();

        for (Edge<Node<String>> e : graph.edges()) {
            Node<String> from = e.getFrom();
            Node<String> to   = e.getTo();

            String a = String.valueOf(from.getId());
            String b = String.valueOf(to.getId());

            String key = a + "-" + b;
            String rev = b + "-" + a;
            if (drawn.contains(key) || drawn.contains(rev)) continue;
            drawn.add(key);

            boolean onPath = isOnPath(from, to);
            boolean blocked = isBlocked(from, to);

            g2.setColor(blocked
                    ? Color.RED
                    : (onPath ? new Color(46, 204, 113) : Color.GRAY));

            float stroke = onPath ? 3f : 1f;
            stroke = (float) Math.max(1.0, stroke * scale);
            g2.setStroke(new BasicStroke(stroke));

            int x1 = sx(from), y1 = sy(from);
            int x2 = sx(to),   y2 = sy(to);

            g2.drawLine(x1, y1, x2, y2);

            // weight label
            int fontSize = Math.max(9, Math.min(12, (int) Math.round(10 * scale)));
            g2.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g2.setColor(Color.DARK_GRAY);

            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2 - 4;
            g2.drawString(String.valueOf((int) e.getWeight()), midX, midY);
        }
    }

    private void drawNodes(Graphics2D g2) {
        int r = nodeRadius();
        int d = r * 2;

        int fontSize = Math.max(10, Math.min(14, (int) Math.round(12 * scale)));
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();

        for (Node<String> node : graph.nodes()) {
            boolean onPath = highlightedPath.contains(node);

            int cx = sx(node), cy = sy(node);
            int x = cx - r, y = cy - r;

            g2.setColor(onPath ? new Color(46, 204, 113) : new Color(74, 144, 217));
            g2.fillOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(Math.max(1f, (float)(2f * scale))));
            g2.drawOval(x, y, d, d);

            String label = String.valueOf(node.getId());
            g2.setColor(Color.WHITE);
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + (fm.getAscent() / 3));
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