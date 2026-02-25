package Model;

import java.util.Objects;

public record Node<ID>(ID id, int x, int y) {

    public Node {
        Objects.requireNonNull(id, "id");
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}