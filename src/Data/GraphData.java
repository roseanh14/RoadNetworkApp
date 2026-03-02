package Data;

import Model.Graph;

import java.awt.Point;
import java.util.*;

public class GraphData {

    public static Graph<String, Point, Double> buildGraph() {
        Graph<String, Point, Double> graph = new Graph<>();
        addVertices(graph);
        addEdges(graph);
        return graph;
    }

    private static void addVertices(Graph<String, Point, Double> graph) {
        graph.addVertex("z", new Point(100, 80));
        graph.addVertex("k", new Point(190, 145));
        graph.addVertex("s", new Point(500, 148));
        graph.addVertex("i", new Point(560, 225));
        graph.addVertex("a", new Point(270, 295));
        graph.addVertex("x", new Point(390, 335));
        graph.addVertex("m", new Point(645, 340));
        graph.addVertex("g", new Point(620, 425));
        graph.addVertex("u", new Point(190, 475));
        graph.addVertex("r", new Point(375, 520));
        graph.addVertex("f", new Point(510, 515));
        graph.addVertex("p", new Point(260, 575));
        graph.addVertex("w", new Point(230, 650));
        graph.addVertex("n", new Point(405, 640));
        graph.addVertex("t", new Point(610, 640));

        graph.addVertex("b", new Point(345, 100));
        graph.addVertex("c", new Point(720, 110));
        graph.addVertex("d", new Point(760, 265));
        graph.addVertex("e", new Point(95, 295));
        graph.addVertex("h", new Point(380, 740));
        graph.addVertex("j", new Point(640, 735));
        graph.addVertex("l", new Point(740, 530));
    }

    private static void addEdges(Graph<String, Point, Double> graph) {
        var z = req(graph, "z");
        var k = req(graph, "k");
        var s = req(graph, "s");
        var i = req(graph, "i");
        var a = req(graph, "a");
        var x = req(graph, "x");
        var u = req(graph, "u");
        var m = req(graph, "m");
        var g = req(graph, "g");
        var t = req(graph, "t");
        var r = req(graph, "r");
        var f = req(graph, "f");
        var n = req(graph, "n");
        var p = req(graph, "p");
        var w = req(graph, "w");
        var b = req(graph, "b");
        var c = req(graph, "c");
        var d = req(graph, "d");
        var e = req(graph, "e");
        var h = req(graph, "h");
        var j = req(graph, "j");
        var l = req(graph, "l");

        graph.addUndirectedEdge(z, k, 2.0);
        graph.addUndirectedEdge(k, s, 8.0);
        graph.addUndirectedEdge(k, a, 4.0);
        graph.addUndirectedEdge(a, s, 12.0);
        graph.addUndirectedEdge(s, i, 3.0);
        graph.addUndirectedEdge(i, x, 3.0);
        graph.addUndirectedEdge(a, x, 5.0);
        graph.addUndirectedEdge(s, x, 10.0);
        graph.addUndirectedEdge(x, m, 8.0);
        graph.addUndirectedEdge(x, g, 12.0);
        graph.addUndirectedEdge(x, u, 8.0);
        graph.addUndirectedEdge(m, g, 2.0);
        graph.addUndirectedEdge(u, g, 13.0);
        graph.addUndirectedEdge(g, t, 4.0);
        graph.addUndirectedEdge(t, n, 2.0);
        graph.addUndirectedEdge(f, n, 3.0);
        graph.addUndirectedEdge(r, n, 2.0);
        graph.addUndirectedEdge(n, p, 5.0);
        graph.addUndirectedEdge(p, w, 1.0);

        graph.addUndirectedEdge(z, e, 3.0);
        graph.addUndirectedEdge(e, a, 6.0);
        graph.addUndirectedEdge(e, p, 9.0);
        graph.addUndirectedEdge(k, b, 3.0);
        graph.addUndirectedEdge(b, s, 5.0);
        graph.addUndirectedEdge(s, c, 4.0);
        graph.addUndirectedEdge(c, i, 6.0);
        graph.addUndirectedEdge(c, d, 5.0);
        graph.addUndirectedEdge(d, m, 4.0);
        graph.addUndirectedEdge(d, l, 3.0);
        graph.addUndirectedEdge(l, g, 5.0);
        graph.addUndirectedEdge(l, j, 4.0);
        graph.addUndirectedEdge(j, t, 3.0);
        graph.addUndirectedEdge(t, h, 6.0);
        graph.addUndirectedEdge(h, w, 2.0);
        graph.addUndirectedEdge(r, f, 2.0);
    }

    private static Graph<String, Point, Double>.Vertex req(Graph<String, Point, Double> graph, String key) {
        var v = graph.getVertex(key);
        if (v == null) throw new IllegalStateException("Missing vertex: '" + key + "'");
        return v;
    }
}