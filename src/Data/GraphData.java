package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

public class GraphData {

    public static Graph<String, Integer, Double> buildGraph() {
        // (from, to, w) -> new Edge<>(from, to, w)
        // nahradí se method reference:
        Graph<String, Integer, Double> graph = new Graph<>(Edge::new);

        addNodes(graph);
        addEdges(graph);
        return graph;
    }

    private static void addNodes(Graph<String, Integer, Double> graph) {
        graph.addNode(new Node<>("z",  80,  90));
        graph.addNode(new Node<>("k", 140, 150));
        graph.addNode(new Node<>("s", 460, 180));
        graph.addNode(new Node<>("i", 540, 260));

        graph.addNode(new Node<>("a", 230, 350));
        graph.addNode(new Node<>("x", 360, 430));

        graph.addNode(new Node<>("u", 200, 590));

        graph.addNode(new Node<>("m", 650, 480));
        graph.addNode(new Node<>("g", 620, 560));
        graph.addNode(new Node<>("t", 620, 690));

        graph.addNode(new Node<>("r", 430, 600));
        graph.addNode(new Node<>("f", 520, 600));
        graph.addNode(new Node<>("n", 470, 690));

        graph.addNode(new Node<>("p", 240, 660));
        graph.addNode(new Node<>("w", 210, 720));
    }

    private static void addEdges(Graph<String, Integer, Double> graph) {
        Node<String, Integer> z = requireNode(graph, "z");
        Node<String, Integer> k = requireNode(graph, "k");
        Node<String, Integer> s = requireNode(graph, "s");
        Node<String, Integer> i = requireNode(graph, "i");
        Node<String, Integer> a = requireNode(graph, "a");
        Node<String, Integer> x = requireNode(graph, "x");
        Node<String, Integer> u = requireNode(graph, "u");
        Node<String, Integer> m = requireNode(graph, "m");
        Node<String, Integer> g = requireNode(graph, "g");
        Node<String, Integer> t = requireNode(graph, "t");
        Node<String, Integer> n = requireNode(graph, "n");
        Node<String, Integer> p = requireNode(graph, "p");
        Node<String, Integer> w = requireNode(graph, "w");
        Node<String, Integer> r = requireNode(graph, "r");
        Node<String, Integer> f = requireNode(graph, "f");

        graph.addUndirectedEdge(z, k, 2.0);
        graph.addUndirectedEdge(k, s, 8.0);
        graph.addUndirectedEdge(k, a, 4.0);
        graph.addUndirectedEdge(a, s, 12.0);
        graph.addUndirectedEdge(s, i, 3.0);
        graph.addUndirectedEdge(a, x, 5.0);
        graph.addUndirectedEdge(i, x, 3.0);
        graph.addUndirectedEdge(s, x, 10.0);
        graph.addUndirectedEdge(x, u, 8.0);
        graph.addUndirectedEdge(u, g, 13.0);
        graph.addUndirectedEdge(x, g, 12.0);
        graph.addUndirectedEdge(x, m, 8.0);
        graph.addUndirectedEdge(m, g, 2.0);
        graph.addUndirectedEdge(g, t, 4.0);
        graph.addUndirectedEdge(t, n, 2.0);
        graph.addUndirectedEdge(n, p, 5.0);
        graph.addUndirectedEdge(p, w, 1.0);
        graph.addUndirectedEdge(r, n, 2.0);
        graph.addUndirectedEdge(f, n, 3.0);
    }

    private static Node<String, Integer> requireNode(Graph<String, Integer, Double> graph, String id) {
        Node<String, Integer> node = graph.getNodeById(id);
        if (node == null) throw new IllegalStateException("Missing node in GraphData: '" + id + "'");
        return node;
    }
}