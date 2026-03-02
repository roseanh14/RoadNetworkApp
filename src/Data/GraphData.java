package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphData {

    public static Graph<String, Integer, Double> buildGraph() {
        Graph<String, Integer, Double> graph = new Graph<>(Edge::new);
        addNodes(graph);
        addEdges(graph);
        return graph;
    }

    private static void addNodes(Graph<String, Integer, Double> graph) {
        // core 15 nodes — pixel positions traced directly from the reference diagram
        graph.addNode(new Node<>("z",  100,  80));
        graph.addNode(new Node<>("k",  190, 145));
        graph.addNode(new Node<>("s",  500, 148));
        graph.addNode(new Node<>("i",  560, 225));
        graph.addNode(new Node<>("a",  270, 295));
        graph.addNode(new Node<>("x",  390, 335));
        graph.addNode(new Node<>("m",  645, 340));
        graph.addNode(new Node<>("g",  620, 425));
        graph.addNode(new Node<>("u",  190, 475));
        graph.addNode(new Node<>("r",  375, 520));
        graph.addNode(new Node<>("f",  510, 515));
        graph.addNode(new Node<>("p",  260, 575));
        graph.addNode(new Node<>("w",  230, 650));
        graph.addNode(new Node<>("n",  405, 640));
        graph.addNode(new Node<>("t",  610, 640));
        // extra 7 nodes — placed around the edges so they don't clutter the core layout
        graph.addNode(new Node<>("b",  345, 100));
        graph.addNode(new Node<>("c",  720, 110));
        graph.addNode(new Node<>("d",  760, 265));
        graph.addNode(new Node<>("e",   95, 295));
        graph.addNode(new Node<>("h",  380, 740));
        graph.addNode(new Node<>("j",  640, 735));
        graph.addNode(new Node<>("l",  740, 530));
    }

    private static void addEdges(Graph<String, Integer, Double> graph) {
        Node<String, Integer> z = req(graph, "z");
        Node<String, Integer> k = req(graph, "k");
        Node<String, Integer> s = req(graph, "s");
        Node<String, Integer> i = req(graph, "i");
        Node<String, Integer> a = req(graph, "a");
        Node<String, Integer> x = req(graph, "x");
        Node<String, Integer> u = req(graph, "u");
        Node<String, Integer> m = req(graph, "m");
        Node<String, Integer> g = req(graph, "g");
        Node<String, Integer> t = req(graph, "t");
        Node<String, Integer> r = req(graph, "r");
        Node<String, Integer> f = req(graph, "f");
        Node<String, Integer> n = req(graph, "n");
        Node<String, Integer> p = req(graph, "p");
        Node<String, Integer> w = req(graph, "w");
        Node<String, Integer> b = req(graph, "b");
        Node<String, Integer> c = req(graph, "c");
        Node<String, Integer> d = req(graph, "d");
        Node<String, Integer> e = req(graph, "e");
        Node<String, Integer> h = req(graph, "h");
        Node<String, Integer> j = req(graph, "j");
        Node<String, Integer> l = req(graph, "l");

        // exact 19 edges visible in the reference diagram
        graph.addUndirectedEdge(z, k,  2.0);
        graph.addUndirectedEdge(k, s,  8.0);
        graph.addUndirectedEdge(k, a,  4.0);
        graph.addUndirectedEdge(a, s, 12.0);
        graph.addUndirectedEdge(s, i,  3.0);
        graph.addUndirectedEdge(i, x,  3.0);
        graph.addUndirectedEdge(a, x,  5.0);
        graph.addUndirectedEdge(s, x, 10.0);
        graph.addUndirectedEdge(x, m,  8.0);
        graph.addUndirectedEdge(x, g, 12.0);
        graph.addUndirectedEdge(x, u,  8.0);
        graph.addUndirectedEdge(m, g,  2.0);
        graph.addUndirectedEdge(u, g, 13.0);
        graph.addUndirectedEdge(g, t,  4.0);
        graph.addUndirectedEdge(t, n,  2.0);
        graph.addUndirectedEdge(f, n,  3.0);
        graph.addUndirectedEdge(r, n,  2.0);
        graph.addUndirectedEdge(n, p,  5.0);
        graph.addUndirectedEdge(p, w,  1.0);
        // extra edges using the outer nodes to reach 30+
        graph.addUndirectedEdge(z, e,  3.0);
        graph.addUndirectedEdge(e, a,  6.0);
        graph.addUndirectedEdge(e, p,  9.0);
        graph.addUndirectedEdge(k, b,  3.0);
        graph.addUndirectedEdge(b, s,  5.0);
        graph.addUndirectedEdge(s, c,  4.0);
        graph.addUndirectedEdge(c, i,  6.0);
        graph.addUndirectedEdge(c, d,  5.0);
        graph.addUndirectedEdge(d, m,  4.0);
        graph.addUndirectedEdge(d, l,  3.0);
        graph.addUndirectedEdge(l, g,  5.0);
        graph.addUndirectedEdge(l, j,  4.0);
        graph.addUndirectedEdge(j, t,  3.0);
        graph.addUndirectedEdge(t, h,  6.0);
        graph.addUndirectedEdge(h, w,  2.0);
        graph.addUndirectedEdge(r, f,  2.0);
    }

    /**
     * Generates a random connected graph.
     * @param nodeCount  number of nodes (min 2)
     * @param edgeCount  total edges wanted (min nodeCount - 1)
     * @param seed       fixed seed for repeatable results; -1 = truly random
     */
    public static Graph<String, Integer, Double> buildRandom(int nodeCount, int edgeCount, long seed) {
        Graph<String, Integer, Double> graph = new Graph<>(Edge::new);
        Random rng = (seed == -1) ? new Random() : new Random(seed);

        List<Node<String, Integer>> nodes = new ArrayList<>();
        int canvasW = 700, canvasH = 680, margin = 60;

        for (int idx = 0; idx < nodeCount; idx++) {
            int px = margin + rng.nextInt(canvasW - margin * 2);
            int py = margin + rng.nextInt(canvasH - margin * 2);
            Node<String, Integer> node = new Node<>("v" + idx, px, py);
            graph.addNode(node);
            nodes.add(node);
        }

        // spanning chain — guarantees the graph is always connected
        List<Node<String, Integer>> shuffled = new ArrayList<>(nodes);
        java.util.Collections.shuffle(shuffled, rng);
        for (int idx = 0; idx < shuffled.size() - 1; idx++) {
            double w = Math.round((1 + rng.nextDouble() * 19) * 10.0) / 10.0;
            graph.addUndirectedEdge(shuffled.get(idx), shuffled.get(idx + 1), w);
        }

        // extra random edges until edgeCount is reached
        int current  = nodeCount - 1;
        int attempts = 0;
        while (current < edgeCount && attempts < edgeCount * 10) {
            Node<String, Integer> from = nodes.get(rng.nextInt(nodeCount));
            Node<String, Integer> to   = nodes.get(rng.nextInt(nodeCount));
            attempts++;
            if (from == to) continue;
            boolean exists = graph.edgesFrom(from).stream().anyMatch(edge -> edge.getTo().equals(to));
            if (exists) continue;
            double w = Math.round((1 + rng.nextDouble() * 19) * 10.0) / 10.0;
            graph.addUndirectedEdge(from, to, w);
            current++;
        }

        return graph;
    }

    private static Node<String, Integer> req(Graph<String, Integer, Double> graph, String id) {
        Node<String, Integer> node = graph.getNodeById(id);
        if (node == null) throw new IllegalStateException("Missing node: '" + id + "'");
        return node;
    }
}