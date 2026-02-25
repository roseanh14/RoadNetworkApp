package Model;

public class Edge<N extends Node<?>> {
    private final N from;
    private final N to;
    private double weight;

    public Edge(N from, N to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public N getFrom() { return from; }
    public N getTo() { return to; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return from + " -> " + to + " (" + (int)weight + ")";
    }
}