package Model;

import java.util.Objects;

public class Node<ID> {
    private final ID id;
    private final double x;
    private final double y;

    public Node(ID id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public ID getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() { return String.valueOf(id); }

    // Equality based on ID (two nodes with same ID are considered the same node)
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node<?> other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}