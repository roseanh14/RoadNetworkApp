package GUI;

import Model.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    private final Graph<String, Point, Double> graph;

    private List<Graph<String, Point, Double>.Vertex> highlightedPath = new ArrayList<>();
    private Graph<String, Point, Double>.Vertex selectedVertex = null;

    private Set<String> blockedEdges = new HashSet<>();
    private String pendingAddVertexKey = null;
    private Runnable onVertexAdded;

    private static final int NODE_RADIUS = 12;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = -40;

    public GraphPanel(Graph<String, Point, Double> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int gx = e.getX() - OFFSET_X;
                int gy = e.getY() - OFFSET_Y;

                if (pendingAddVertexKey != null && SwingUtilities.isLeftMouseButton(e)) {
                    String key = pendingAddVertexKey;
                    pendingAddVertexKey = null;

                    if (graph.getVertex(key) != null) {
                        repaint();
                        return;
                    }

                    graph.addVertex(key, new Point(gx, gy));
                    selectedVertex = graph.getVertex(key);
                    if (onVertexAdded != null) onVertexAdded.run();
                    repaint();
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    var clicked = findVertexAt(gx, gy);
                    setSelectedVertex(clicked);
                }
            }
        });
    }

    public void setOnVertexAdded(Runnable callback) {
        this.onVertexAdded = callback;
    }

    public void highlightPath(List<Graph<String, Point, Double>.Vertex> path) {
        this.highlightedPath = (path == null) ? new ArrayList<>() : new ArrayList<>(path);
        repaint();
    }

    public void setSelectedVertex(Graph<String, Point, Double>.Vertex v) {
        this.selectedVertex = v;
        repaint();
    }

    public Graph<String, Point, Double>.Vertex getSelectedVertex() {
        return selectedVertex;
    }

    public void setBlockedEdges(Set<String> keys) {
        this.blockedEdges = (keys == null) ? new HashSet<>() : new HashSet<>(keys);
        repaint();
    }

    public void beginAddVertex(String key) {
        this.pendingAddVertexKey = key;
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
            drawVertices(g2);

            g2.translate(-OFFSET_X, -OFFSET_Y);

            if (pendingAddVertexKey != null) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString("Click to place node: " + pendingAddVertexKey, 10, 20);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawEdges(Graphics2D g2) {
        Set<String> drawn = new HashSet<>();

        for (var e : graph.edges()) {
            var from = e.from();
            var to   = e.to();

            String key = Graph.edgeKey(from, to);
            if (!drawn.add(key)) continue;

            boolean onPath    = isOnPath(from, to);
            boolean isBlocked = blockedEdges.contains(key);

            if (isBlocked) g2.setColor(Color.RED);
            else if (onPath) g2.setColor(new Color(46, 204, 113));
            else g2.setColor(Color.GRAY);

            float stroke = isBlocked ? 4f : (onPath ? 3f : 1f);
            g2.setStroke(new BasicStroke(stroke));

            Point p1 = from.data();
            Point p2 = to.data();

            g2.drawLine(p1.x, p1.y, p2.x, p2.y);

            int midX = (p1.x + p2.x) / 2;
            int midY = (p1.y + p2.y) / 2;

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf((int) Math.round((Double) e.data())), midX + 2, midY - 2);
        }
    }

    private void drawVertices(Graphics2D g2) {
        int d = NODE_RADIUS * 2;

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (var v : graph.vertices()) {
            boolean onPath     = highlightedPath.contains(v);
            boolean isSelected = (selectedVertex != null && selectedVertex.equals(v));

            Point p = v.data();
            int cx = p.x;
            int cy = p.y;

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

            String label = String.valueOf(v.key());
            g2.setColor(Color.WHITE);
            g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + 5);
        }
    }

    private boolean isOnPath(Graph<String, Point, Double>.Vertex from,
                             Graph<String, Point, Double>.Vertex to) {
        for (int i = 0; i < highlightedPath.size() - 1; i++) {
            var a = highlightedPath.get(i);
            var b = highlightedPath.get(i + 1);
            if ((a.equals(from) && b.equals(to)) || (a.equals(to) && b.equals(from))) return true;
        }
        return false;
    }

    private Graph<String, Point, Double>.Vertex findVertexAt(int x, int y) {
        for (var v : graph.vertices()) {
            Point p = v.data();
            int dx = x - p.x;
            int dy = y - p.y;
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) return v;
        }
        return null;
    }
}