package Data;

import Model.Graph;
import Model.Node;

public class GraphData {

    public static Graph buildGraph() {
        Graph graph = new Graph();
        addNodes(graph);
        addEdges(graph);
        markProblematicEdges(graph);
        return graph;
    }

    private static void addNodes(Graph graph) {
        // Each node has a name + x,y coordinates for drawing on screen
        graph.addNode(new Node("z",  80,  260));
        graph.addNode(new Node("k", 190,  260));
        graph.addNode(new Node("a", 300,  180));
        graph.addNode(new Node("x", 410,  180));
        graph.addNode(new Node("g", 520,  180));
        graph.addNode(new Node("t", 630,  180));
        graph.addNode(new Node("n", 740,  180));
        graph.addNode(new Node("p", 850,  180));
        graph.addNode(new Node("w", 920,  260));
        graph.addNode(new Node("m", 460,  330));
        graph.addNode(new Node("u", 570,  330));
        graph.addNode(new Node("b", 190,  370));
        graph.addNode(new Node("c", 300,  330));
        graph.addNode(new Node("d", 410,  330));
        graph.addNode(new Node("e", 790,  330));
        graph.addNode(new Node("f", 300,   90));
        graph.addNode(new Node("h", 350,  420));
        graph.addNode(new Node("i", 490,   90));
        graph.addNode(new Node("j", 630,   90));
        graph.addNode(new Node("l", 790,   90));
    }

    private static void addEdges(Graph graph) {
        Node z=graph.getNode("z"), k=graph.getNode("k"), a=graph.getNode("a");
        Node x=graph.getNode("x"), g=graph.getNode("g"), t=graph.getNode("t");
        Node n=graph.getNode("n"), p=graph.getNode("p"), m=graph.getNode("m");
        Node u=graph.getNode("u"), w=graph.getNode("w"), b=graph.getNode("b");
        Node c=graph.getNode("c"), d=graph.getNode("d"), e=graph.getNode("e");
        Node f=graph.getNode("f"), h=graph.getNode("h"), i=graph.getNode("i");
        Node j=graph.getNode("j"), l=graph.getNode("l");

        // Main route: z -> k -> a -> x -> g -> t -> n -> p -> w
        graph.addEdge(z, k, 5);
        graph.addEdge(k, a, 4);
        graph.addEdge(a, x, 6);
        graph.addEdge(x, g, 3);   // <- problematic edge from assignment
        graph.addEdge(g, t, 5);
        graph.addEdge(t, n, 4);
        graph.addEdge(n, p, 3);
        graph.addEdge(p, w, 6);

        // Alternative roads
        graph.addEdge(z, b, 7);   graph.addEdge(b, a, 4);
        graph.addEdge(x, m, 5);   graph.addEdge(m, u, 4);
        graph.addEdge(u, t, 6);   graph.addEdge(g, u, 4);
        graph.addEdge(m, w, 7);   graph.addEdge(k, c, 3);
        graph.addEdge(c, d, 4);   graph.addEdge(d, g, 5);
        graph.addEdge(n, e, 3);   graph.addEdge(e, w, 4);
        graph.addEdge(a, f, 5);   graph.addEdge(f, t, 6);
        graph.addEdge(b, h, 4);   graph.addEdge(h, m, 5);
        graph.addEdge(c, i, 3);   graph.addEdge(i, n, 4);
        graph.addEdge(d, j, 5);   graph.addEdge(j, p, 4);
        graph.addEdge(u, l, 3);   graph.addEdge(l, w, 5);
        graph.addEdge(z, f, 9);   graph.addEdge(b, c, 6);
        graph.addEdge(h, i, 5);   graph.addEdge(j, l, 4);
    }

    private static void markProblematicEdges(Graph graph) {
        // Edge x-g is marked problematic as shown in the assignment
        graph.setProblematic(graph.getNode("x"), graph.getNode("g"), true);
    }
}