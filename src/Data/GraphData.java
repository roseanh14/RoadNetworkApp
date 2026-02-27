package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

public class GraphData {

    public static Graph<String> buildGraph() {
        Graph<String> graph = new Graph<>(Edge::new);

        addNodes(graph);
        addEdges(graph);

        return graph;
    }

    private static void addNodes(Graph<String> graph) {
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

    private static void addEdges(Graph<String> graph) {
        Node<String> z = requireNode(graph, "z");
        Node<String> k = requireNode(graph, "k");
        Node<String> s = requireNode(graph, "s");
        Node<String> i = requireNode(graph, "i");
        Node<String> a = requireNode(graph, "a");
        Node<String> x = requireNode(graph, "x");
        Node<String> u = requireNode(graph, "u");
        Node<String> m = requireNode(graph, "m");
        Node<String> g = requireNode(graph, "g");
        Node<String> t = requireNode(graph, "t");
        Node<String> n = requireNode(graph, "n");
        Node<String> p = requireNode(graph, "p");
        Node<String> w = requireNode(graph, "w");
        Node<String> r = requireNode(graph, "r");
        Node<String> f = requireNode(graph, "f");

        graph.addUndirectedEdge(z, k, 2);
        graph.addUndirectedEdge(k, s, 8);
        graph.addUndirectedEdge(k, a, 4);
        graph.addUndirectedEdge(a, s, 12);
        graph.addUndirectedEdge(s, i, 3);
        graph.addUndirectedEdge(a, x, 5);
        graph.addUndirectedEdge(i, x, 3);
        graph.addUndirectedEdge(s, x, 10);
        graph.addUndirectedEdge(x, u, 8);
        graph.addUndirectedEdge(u, g, 13);
        graph.addUndirectedEdge(x, g, 12);
        graph.addUndirectedEdge(x, m, 8);
        graph.addUndirectedEdge(m, g, 2);
        graph.addUndirectedEdge(g, t, 4);
        graph.addUndirectedEdge(t, n, 2);
        graph.addUndirectedEdge(n, p, 5);
        graph.addUndirectedEdge(p, w, 1);
        graph.addUndirectedEdge(r, n, 2);
        graph.addUndirectedEdge(f, n, 3);
    }

    private static Node<String> requireNode(Graph<String> graph, String id) {
        Node<String> node = graph.getNodeById(id);
        if (node == null) throw new IllegalStateException("Missing node: " + id);
        return node;
    }
}