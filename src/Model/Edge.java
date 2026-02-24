package Model;

public class Edge {
    public Node from;
    public Node to;
    public double weight;       // travel time in minutes
    public boolean problematic; // is this road often blocked?

    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.problematic = false;
    }

    @Override
    public String toString() {
        return from.name + " - " + to.name + " (" + (int)weight + " min)" + (problematic ? " [!]" : "");
    }
}