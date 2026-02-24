package Model;

public class Node {
    public String name;
    public int x, y; // position on screen for drawing

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        return this.name.equals(((Node) obj).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }
}