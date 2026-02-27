package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Node<ID> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ID id;
    private int x;
    private int y;

    public Node(ID id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public ID id() { return id; }
    public int x() { return x; }
    public int y() { return y; }

    @SuppressWarnings("unused")
    public void setX(int x) { this.x = x; }

    @SuppressWarnings("unused")
    public void setY(int y) { this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node<?> node)) return false;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}