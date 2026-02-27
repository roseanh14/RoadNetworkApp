package Model;

import java.io.Serial;
import java.io.Serializable;

public class Edge<ID> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Node<ID> from;
    private final Node<ID> to;
    private double weight;

    public Edge(Node<ID> from, Node<ID> to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Node<ID> getFrom() { return from; }
    public Node<ID> getTo() { return to; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}