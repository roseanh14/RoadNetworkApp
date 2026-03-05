package Data;

import Model.Graph;

import java.awt.Point;

public class GraphData {

    public static Graph<String, Point, Double> buildGraph() {
        Graph<String, Point, Double> graph = new Graph<>();
        addVertices(graph);
        addEdges(graph);
        return graph;
    }

    private static void addVertices(Graph<String, Point, Double> graph) {
        graph.putVertex("z", new Point(100, 80));
        graph.putVertex("k", new Point(190, 145));
        graph.putVertex("s", new Point(500, 148));
        graph.putVertex("i", new Point(560, 225));
        graph.putVertex("a", new Point(270, 295));
        graph.putVertex("x", new Point(390, 335));
        graph.putVertex("m", new Point(645, 340));
        graph.putVertex("g", new Point(620, 425));
        graph.putVertex("u", new Point(190, 475));
        graph.putVertex("r", new Point(375, 520));
        graph.putVertex("f", new Point(510, 515));
        graph.putVertex("p", new Point(260, 575));
        graph.putVertex("w", new Point(230, 650));
        graph.putVertex("n", new Point(405, 640));
        graph.putVertex("t", new Point(610, 640));

        graph.putVertex("b", new Point(345, 100));
        graph.putVertex("c", new Point(720, 110));
        graph.putVertex("d", new Point(760, 265));
        graph.putVertex("e", new Point(95, 295));
        graph.putVertex("h", new Point(380, 740));
        graph.putVertex("j", new Point(640, 735));
        graph.putVertex("l", new Point(740, 530));
    }

    private static void addEdges(Graph<String, Point, Double> graph) {
        graph.addUndirectedEdge("z", "k", 2.0);
        graph.addUndirectedEdge("k", "s", 8.0);
        graph.addUndirectedEdge("k", "a", 4.0);
        graph.addUndirectedEdge("a", "s", 12.0);
        graph.addUndirectedEdge("s", "i", 3.0);
        graph.addUndirectedEdge("i", "x", 3.0);
        graph.addUndirectedEdge("a", "x", 5.0);
        graph.addUndirectedEdge("s", "x", 10.0);
        graph.addUndirectedEdge("x", "m", 8.0);
        graph.addUndirectedEdge("x", "g", 12.0);
        graph.addUndirectedEdge("x", "u", 8.0);
        graph.addUndirectedEdge("m", "g", 2.0);
        graph.addUndirectedEdge("u", "g", 13.0);
        graph.addUndirectedEdge("g", "t", 4.0);
        graph.addUndirectedEdge("t", "n", 2.0);
        graph.addUndirectedEdge("f", "n", 3.0);
        graph.addUndirectedEdge("r", "n", 2.0);
        graph.addUndirectedEdge("n", "p", 5.0);
        graph.addUndirectedEdge("p", "w", 1.0);

        graph.addUndirectedEdge("z", "e", 3.0);
        graph.addUndirectedEdge("e", "a", 6.0);
        graph.addUndirectedEdge("e", "p", 9.0);
        graph.addUndirectedEdge("k", "b", 3.0);
        graph.addUndirectedEdge("b", "s", 5.0);
        graph.addUndirectedEdge("s", "c", 4.0);
        graph.addUndirectedEdge("c", "i", 6.0);
        graph.addUndirectedEdge("c", "d", 5.0);
        graph.addUndirectedEdge("d", "m", 4.0);
        graph.addUndirectedEdge("d", "l", 3.0);
        graph.addUndirectedEdge("l", "g", 5.0);
        graph.addUndirectedEdge("l", "j", 4.0);
        graph.addUndirectedEdge("j", "t", 3.0);
        graph.addUndirectedEdge("t", "h", 6.0);
        graph.addUndirectedEdge("h", "w", 2.0);
        graph.addUndirectedEdge("r", "f", 2.0);
    }
}