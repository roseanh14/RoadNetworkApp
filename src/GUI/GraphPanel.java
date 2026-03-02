package GUI;

import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    private final Graph<String, Integer, Double> graph;

    private List<Node<String, Integer>> highlightedPath = new ArrayList<>();
    private Node<String, Integer> selectedNode = null;
    private Set<String> blockedEdges = new HashSet<>();
    private String pendingAddNodeId = null;
    private Runnable onNodeAdded;                          // added

    private static final int NODE_RADIUS = 12;

    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = -40;

    public GraphPanel(Graph<String, Integer, Double> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int gx = e.getX() - OFFSET_X;
                int gy = e.getY() - OFFSET_Y;

                if (pendingAddNodeId != null && SwingUtilities.isLeftMouseButton(e)) {
                    String id = pendingAddNodeId;
                    pendingAddNodeId = null;

                    if (graph.getNodeById(id) != null) {
                        repaint();
                        return;
                    }

                    graph.addNode(new Node<>(id, gx, gy));
                    selectedNode = graph.getNodeById(id);
                    if (onNodeAdded != null) onNodeAdded.run();  // added
                    repaint();
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Node<String, Integer> clicked = findNodeAt(gx, gy);
                    setSelectedNode(clicked);
                }
            }
        });
    }

    public void setOnNodeAdded(Runnable callback) {     // added
        this.onNodeAdded = callback;
    }

    public void highlightPath(List<Node<String, Integer>> path) {
        this.highlightedPath = (path == null) ? new ArrayList<>() : new ArrayList<>(path);
        repaint();
    }

    public void setSelectedNode(Node<String, Integer> node) {
        this.selectedNode = node;
        repaint();
    }

    public Node<String, Integer> getSelectedNode() {
        return selectedNode;
    }

    public void setBlockedEdges(Set<String> keys) {
        this.blockedEdges = (keys == null) ? new HashSet<>() : new HashSet<>(keys);
        repaint();
    }

    public void beginAddNode(String nodeId) {
        this.pendingAddNodeId = nodeId;
        repaint();
    }

    @SuppressWarnings("unused")
    public void cancelAddNode() {
        this.pendingAddNodeId = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.translate(OFFSET_X, OFFSET_Y);

            drawEdges(g2);
            drawNodes(g2);

            g2.translate(-OFFSET_X, -OFFSET_Y);

            if (pendingAddNodeId != null) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString("Click to place node: " + pendingAddNodeId, 10, 20);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawEdges(Graphics2D g2) {
        Set<String> drawn = new HashSet<>();

        for (Edge<String, Integer, Double> e : graph.edges()) {
            Node<String, Integer> from = e.getFrom();
            Node<String, Integer> to   = e.getTo();

            String key = Graph.edgeKey(from, to);
            if (!drawn.add(key)) continue;

            boolean onPath    = isOnPath(from, to);
            boolean isBlocked = blockedEdges.contains(key);

            if (isBlocked) g2.setColor(Color.RED);
            else if (onPath) g2.setColor(new Color(46, 204, 113));
            else g2.setColor(Color.GRAY);

            float stroke = isBlocked ? 4f : (onPath ? 3f : 1f);
            g2.setStroke(new BasicStroke(stroke));

            int x1 = from.x();
            int y1 = from.y();
            int x2 = to.x();
            int y2 = to.y();

            g2.drawLine(x1, y1, x2, y2);

            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf((int) Math.round(e.getWeight())), midX + 2, midY - 2);
        }
    }

    private void drawNodes(Graphics2D g2) {
        int d = NODE_RADIUS * 2;

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (Node<String, Integer> node : graph.nodes()) {
            boolean onPath     = highlightedPath.contains(node);
            boolean isSelected = (selectedNode != null && selectedNode.equals(node));

            int cx = node.x();
            int cy = node.y();

            int x = cx - NODE_RADIUS, y = cy - NODE_RADIUS;

            g2.setColor(onPath ? new Color(46, 204, 113) : new Color(74, 144, 217));
            g2.fillOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, d, d);

            if (isSelected) {
                g2.setColor(new Color(241, 196, 15));
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(x - 3, y - 3, d + 6, d + 6);
            }

            String label = String.valueOf(node.id());
            g2.setColor(Color.WHITE);
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + 5);
        }
    }

    private boolean isOnPath(Node<String, Integer> from, Node<String, Integer> to) {
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            Node<String, Integer> a = highlightedPath.get(i);
            Node<String, Integer> b = highlightedPath.get(i + 1);
            if ((a.equals(from) && b.equals(to)) || (a.equals(to) && b.equals(from))) return true;
        }
        return false;
    }

    private Node<String, Integer> findNodeAt(int x, int y) {
        for (Node<String, Integer> node : graph.nodes()) {
            int dx = x - node.x();
            int dy = y - node.y();
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) return node;
        }
        return null;
    }
}