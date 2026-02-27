package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Node<ID, C extends Number> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ID id;
    private C x;
    private C y;

    public Node(ID id, C x, C y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public ID id() { return id; }
    public C x() { return x; }
    public C y() { return y; }

    @SuppressWarnings("unused")
    public void setX(C x) { this.x = x; }

    @SuppressWarnings("unused")
    public void setY(C y) { this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node<?, ?> node)) return false;
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