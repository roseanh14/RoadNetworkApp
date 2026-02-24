package Model;

public class Edge<N extends Node> {
    private final N from;
    private final N to;
    private double weight;
    private boolean problematic;

    public Edge(N from, N to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.problematic = false;
    }

    public N getFrom() { return from; }
    public N getTo() { return to; }
    public double getWeight() { return weight; }
    public boolean isProblematic() { return problematic; }

    public void setWeight(double weight) { this.weight = weight; }
    public void setProblematic(boolean problematic) { this.problematic = problematic; }

    @Override
    public String toString() {
        return from.getName() + " -> " + to.getName()
                + " (" + (int) weight + " min)"
                + (problematic ? " [!]" : "");
    }
}