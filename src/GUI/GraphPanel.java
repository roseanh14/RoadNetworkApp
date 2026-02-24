package GUI;

import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    private final Graph<Node, Edge<Node>> graph;
    private List<Node> highlightedPath = new ArrayList<>();
    private static final int NODE_RADIUS = 16;

    public GraphPanel(Graph<Node, Edge<Node>> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
    }

    public void highlightPath(List<Node> path) {
        this.highlightedPath = (path == null) ? new ArrayList<>() : path;
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

        for (Edge<Node> e : graph.getEdgesView()) {
            Node from = e.getFrom();
            Node to = e.getTo();

            String key = from.getName() + "-" + to.getName();
            String rev = to.getName() + "-" + from.getName();
            if (drawn.contains(key) || drawn.contains(rev)) continue;
            drawn.add(key);

            boolean onPath = isOnPath(from, to);

            g2.setColor(e.isProblematic()
                    ? Color.RED
                    : (onPath ? new Color(46, 204, 113) : Color.GRAY));

            g2.setStroke(new BasicStroke(onPath ? 3 : 1));
            g2.drawLine(from.getX(), from.getY(), to.getX(), to.getY());

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);

            int midX = (from.getX() + to.getX()) / 2;
            int midY = (from.getY() + to.getY()) / 2 - 3;
            g2.drawString(String.valueOf((int) e.getWeight()), midX, midY);
        }
    }

    private void drawNodes(Graphics2D g2) {
        for (Node node : graph.getNodesView()) {
            boolean onPath = highlightedPath.contains(node);

            int x = node.getX() - NODE_RADIUS;
            int y = node.getY() - NODE_RADIUS;
            int d = NODE_RADIUS * 2;

            g2.setColor(onPath ? new Color(46, 204, 113) : new Color(74, 144, 217));
            g2.fillOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, d, d);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            String label = node.getName();
            g2.drawString(label, node.getX() - fm.stringWidth(label) / 2, node.getY() + 5);
        }
    }

    private boolean isOnPath(Node from, Node to) {
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            Node a = highlightedPath.get(i);
            Node b = highlightedPath.get(i + 1);

            if ((a.equals(from) && b.equals(to)) || (a.equals(to) && b.equals(from))) return true;
        }
        return false;
    }
}