package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

public class GraphData {

    public static Graph<Node<String>, Edge<Node<String>>> buildGraph() {

        Graph<Node<String>, Edge<Node<String>>> graph =
                new Graph<>((Node<String> from, Node<String> to, double w) -> new Edge<>(from, to, w));

        addNodes(graph);
        addEdges(graph);

        return graph;
    }


    private static void addNodes(Graph<Node<String>, Edge<Node<String>>> graph) {

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

    private static void addEdges(Graph<Node<String>, Edge<Node<String>>> graph) {
        Node<String> z = graph.getNodeById("z");
        Node<String> k = graph.getNodeById("k");
        Node<String> s = graph.getNodeById("s");
        Node<String> i = graph.getNodeById("i");
        Node<String> a = graph.getNodeById("a");
        Node<String> x = graph.getNodeById("x");
        Node<String> u = graph.getNodeById("u");
        Node<String> m = graph.getNodeById("m");
        Node<String> g = graph.getNodeById("g");
        Node<String> t = graph.getNodeById("t");
        Node<String> n = graph.getNodeById("n");
        Node<String> p = graph.getNodeById("p");
        Node<String> w = graph.getNodeById("w");
        Node<String> r = graph.getNodeById("r");
        Node<String> f = graph.getNodeById("f");

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
}