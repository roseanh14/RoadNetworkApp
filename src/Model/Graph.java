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
        private DV location;
        private final List<Edge> edges = new ArrayList<>();

        private Vertex(KV key, DV location) {
            this.key = key;
            this.location = location;
        }

        private KV getKey() {
            return key;
        }
        
        private DV getLocation() {
            return location;
        }

        private void setLocation(DV location) {
            this.location = location;
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
        private DE weight;
        private boolean active;

        private Edge(Vertex from, Vertex to, DE weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.active = true;
        }

        private Vertex getFrom() {
            return from;
        }

        private Vertex getTo() {
            return to;
        }

        private DE getWeight() {
            return weight;
        }

        private void setWeight(DE weight) {
            this.weight = weight;
        }

        private boolean isActive() {
            return active;
        }

        private void changeActive(boolean active) {
            this.active = active;
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

    public DV getVertexLocation(KV key) {
        Vertex v = vertices.get(key);
        return (v == null) ? null : v.getLocation();
    }

    public void putVertex(KV key, DV location) {
        if (key == null) return;

        Vertex v = vertices.get(key);
        if (v == null) {
            vertices.put(key, new Vertex(key, location));
        } else {
            v.setLocation(location);
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
            if (e.isActive()) {
                out.put(e.getTo().getKey(), e.getWeight());
            }
        }
        return Collections.unmodifiableMap(out);
    }

    public DE getEdgeWeight(KV fromKey, KV toKey) {
        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return null;

        for (Edge e : from.edges) {
            if (e.getTo().equals(to) && e.isActive()) {
                return e.getWeight();
            }
        }
        return null;
    }

    public Map<String, DE> allEdges() {
        Map<String, DE> out = new LinkedHashMap<>();

        for (Vertex v : vertices.values()) {
            for (Edge e : v.edges) {
                String key = edgeKey(e.getFrom().getKey(), e.getTo().getKey());
                out.put(key, e.getWeight());
            }
        }

        return Collections.unmodifiableMap(out);
    }

    public void addDirectedEdge(KV fromKey, KV toKey, DE weight) {
        if (fromKey == null || toKey == null) return;

        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return;

        for (Edge e : from.edges) {
            if (e.getTo().equals(to)) {
                e.setWeight(weight);
                e.changeActive(true);
                return;
            }
        }

        from.edges.add(new Edge(from, to, weight));
    }

    public void addUndirectedEdge(KV aKey, KV bKey, DE weight) {
        addDirectedEdge(aKey, bKey, weight);
        addDirectedEdge(bKey, aKey, weight);
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

    public void setUndirectedEdgeWeight(KV aKey, KV bKey, DE weight) {
        addUndirectedEdge(aKey, bKey, weight);
    }

    public void changeDirectedEdgeActive(KV fromKey, KV toKey, boolean active) {
        Vertex from = vertices.get(fromKey);
        Vertex to = vertices.get(toKey);

        if (from == null || to == null) return;

        for (Edge e : from.edges) {
            if (e.getTo().equals(to)) {
                e.changeActive(active);
                return;
            }
        }
    }

    @SuppressWarnings("unused")
    public void changeUndirectedEdgeActive(KV aKey, KV bKey, boolean active) {
        changeDirectedEdgeActive(aKey, bKey, active);
        changeDirectedEdgeActive(bKey, aKey, active);
    }

    public static <KV> String edgeKey(KV a, KV b) {
        String x = String.valueOf(a);
        String y = String.valueOf(b);
        return (x.compareTo(y) <= 0) ? x + "-" + y : y + "-" + x;
    }
}