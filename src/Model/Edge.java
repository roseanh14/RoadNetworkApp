package Model;

import java.io.Serial;
import java.io.Serializable;

public class Edge<ID, C extends Number, W extends Number> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Node<ID, C> from;
    private final Node<ID, C> to;
    private W weight;

    public Edge(Node<ID, C> from, Node<ID, C> to, W weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Node<ID, C> getFrom() { return from; }
    public Node<ID, C> getTo() { return to; }

    public W getWeight() { return weight; }
    public void setWeight(W weight) { this.weight = weight; }
}