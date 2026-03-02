package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Graph<KV, DV, DE> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public class Vertex implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final KV key;
        private final DV data;

        private final List<Edge> edges = new ArrayList<>();

        private Vertex(KV key, DV data) {
            this.key = key;
            this.data = data;
        }

        public KV key() { return key; }
        public DV data() { return data; }

        public List<Edge> edges() { return Collections.unmodifiableList(edges); }

        @Override public String toString() { return String.valueOf(key); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Graph<?, ?, ?>.Vertex v)) return false;
            return Objects.equals(key, v.key());
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    public class Edge implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final Vertex from;
        private final Vertex to;
        private DE data;

        private Edge(Vertex from, Vertex to, DE data) {
            this.from = from;
            this.to = to;
            this.data = data;
        }

        public Vertex from() { return from; }
        public Vertex to() { return to; }

        public DE data() { return data; }
        public void setData(DE data) { this.data = data; }

        @Override public String toString() {
            return from + "->" + to + " (" + data + ")";
        }
    }

    private final Map<KV, Vertex> vertices = new LinkedHashMap<>();

    public Collection<Vertex> vertices() {
        return vertices.values();
    }

    public Vertex getVertex(KV key) {
        return vertices.get(key);
    }

    public void addVertex(KV key, DV data) {
        if (key == null) return;
        Vertex v = vertices.get(key);
        if (v != null) return;
        v = new Vertex(key, data);
        vertices.put(key, v);
    }

    public boolean removeVertex(Vertex v) {
        if (v == null) return false;
        if (!vertices.containsKey(v.key())) return false;

        for (Vertex other : vertices.values()) {
            other.edges.removeIf(e -> e.to.equals(v) || e.from.equals(v));
        }
        vertices.remove(v.key());
        return true;
    }

    public List<Edge> edgesFrom(Vertex v) {
        if (v == null) return List.of();
        return v.edges();
    }

    public Collection<Edge> edges() {
        List<Edge> all = new ArrayList<>();
        for (Vertex v : vertices.values()) {
            all.addAll(v.edges);
        }
        return all;
    }


    public void addDirectedEdge(Vertex from, Vertex to, DE data) {
        if (from == null || to == null) return;


        for (Edge e : from.edges) {
            if (e.to.equals(to)) {
                e.setData(data);
                return;
            }
        }
        from.edges.add(new Edge(from, to, data));
    }

    public void addUndirectedEdge(Vertex a, Vertex b, DE data) {
        addDirectedEdge(a, b, data);
        addDirectedEdge(b, a, data);
    }

    public void removeDirectedEdge(Vertex from, Vertex to) {
        if (from == null || to == null) return;
        from.edges.removeIf(e -> e.to.equals(to));
    }

    public void removeUndirectedEdge(Vertex a, Vertex b) {
        removeDirectedEdge(a, b);
        removeDirectedEdge(b, a);
    }

    public void setUndirectedEdgeData(Vertex a, Vertex b, DE data) {
        addUndirectedEdge(a, b, data);
    }


    public static <KV> String edgeKey(KV a, KV b) {
        String x = String.valueOf(a);
        String y = String.valueOf(b);
        return (x.compareTo(y) <= 0) ? x + "-" + y : y + "-" + x;
    }

    public static <KV, DV, DE> String edgeKey(Graph<KV, DV, DE>.Vertex a, Graph<KV, DV, DE>.Vertex b) {
        return edgeKey(a.key(), b.key());
    }
}