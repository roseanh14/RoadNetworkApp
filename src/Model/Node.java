package Model;

import java.util.Objects;

public class Node {
    private final String name;
    private final int x;
    private final int y;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node other)) return false;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}