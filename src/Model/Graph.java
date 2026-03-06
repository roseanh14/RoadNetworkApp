package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Graph<KV, DV, DE> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private class Vertex implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final KV key;
        private DV data;
        private final List<Edge> edges = new ArrayList<>();

        private Vertex(KV key, DV data) {
            this.key = key;
            this.data = data;
        }

        private KV getKey() {
            return key;
        }

        private DV getData() {
            return data;
        }

        private void setData(DV data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return String.valueOf(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Graph<?, ?, ?>.Vertex v)) return false;
            return Objects.equals(key, v.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    private class Edge implements Serializable {
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

        private Vertex getFrom() {
            return from;
        }

        private Vertex getTo() {
            return to;
        }

        private DE getData() {
            return data;
        }

        private void setData(DE data) {
            this.data = data;
        }
    }

    private final Map<KV, Vertex> vertices = new LinkedHashMap<>();

    public void clear() {
        vertices.clear();
    }

    public Set<KV> keys() {
        return Collections.unmodifiableSet(vertices.keySet());
    }

    public boolean containsVertex(KV key) {
        return vertices.containsKey(key);
    }

    public DV getVertexData(KV key) {
        Vertex v = vertices.get(key);
        return (v == null) ? null : v.getData();
    }

    public void putVertex(KV key, DV data) {
        if (key == null) return;

        Vertex v = vertices.get(key);
        if (v == null) {
            vertices.put(key, new Vertex(key, data));
        } else {
            v.setData(data);
        }
    }

    public boolean removeVertex(KV key) {
        if (key == null) return false;

        Vertex v = vertices.get(key);
        if (v == null) return false;

        for (Vertex other : vertices.values()) {
            other.edges.removeIf(e -> e.getTo().equals(v) || e.getFrom().equals(v));
        }

        vertices.remove(key);
        return true;
    }

    public Map<KV, DE> neighbors(KV fromKey) {
        Vertex from = vertices.get(fromKey);
        if (from == null) return Map.of();

        Map<KV, DE> out = new LinkedHashMap<>();
        for (Edge e : from.edges) {
            out.put(e.getTo().getKey(), e.getData());
        }
        return Collections.unmodifiableMap(out);
    }

    public DE getEdgeData(KV fromKey, KV toKey) {
        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return null;

        for (Edge e : from.edges) {
            if (e.getTo().equals(to)) {
                return e.getData();
            }
        }
        return null;
    }

    public Map<String, DE> allEdges() {
        Map<String, DE> out = new LinkedHashMap<>();

        for (Vertex v : vertices.values()) {
            for (Edge e : v.edges) {
                String key = edgeKey(e.getFrom().getKey(), e.getTo().getKey());
                out.put(key, e.getData());
            }
        }

        return Collections.unmodifiableMap(out);
    }

    public void addDirectedEdge(KV fromKey, KV toKey, DE data) {
        if (fromKey == null || toKey == null) return;

        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return;

        for (Edge e : from.edges) {
            if (e.getTo().equals(to)) {
                e.setData(data);
                return;
            }
        }

        from.edges.add(new Edge(from, to, data));
    }

    public void addUndirectedEdge(KV aKey, KV bKey, DE data) {
        addDirectedEdge(aKey, bKey, data);
        addDirectedEdge(bKey, aKey, data);
    }

    public void removeDirectedEdge(KV fromKey, KV toKey) {
        if (fromKey == null || toKey == null) return;

        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return;

        from.edges.removeIf(e -> e.getTo().equals(to));
    }

    public void removeUndirectedEdge(KV aKey, KV bKey) {
        removeDirectedEdge(aKey, bKey);
        removeDirectedEdge(bKey, aKey);
    }

    public void setUndirectedEdgeData(KV aKey, KV bKey, DE data) {
        addUndirectedEdge(aKey, bKey, data);
    }

    public static <KV> String edgeKey(KV a, KV b) {
        String x = String.valueOf(a);
        String y = String.valueOf(b);
        return (x.compareTo(y) <= 0) ? x + "-" + y : y + "-" + x;
    }
}