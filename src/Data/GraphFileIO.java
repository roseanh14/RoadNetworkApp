package Data;

import Model.Graph;
import Model.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class GraphFileIO {

    private static final String CACHE_FILE_NAME = "graph.cache";

    public static void loadEdgesCsv(Graph<String> graph, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine(); // header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 3) continue;

                String fromId = p[0].trim();
                String toId = p[1].trim();
                double w = Double.parseDouble(p[2].trim());

                Node<String> from = graph.getNodeById(fromId);
                Node<String> to = graph.getNodeById(toId);
                if (from == null || to == null) continue;

                graph.setUndirectedEdgeWeight(from, to, w);
            }
        }
    }

    public static void saveText(File file, String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(text == null ? "" : text);
        }
    }

    @SuppressWarnings("unchecked")
    public static Graph<String> loadCache() {
        File f = new File(CACHE_FILE_NAME);
        if (!f.exists() || !f.isFile()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            return (Graph<String>) obj;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void saveCache(Graph<String> graph) throws IOException {
        File f = new File(CACHE_FILE_NAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(graph);
        }
    }

    @SuppressWarnings("unchecked")
    public static Graph<String> loadGraphFromFile(File f) throws IOException {
        if (f == null || !f.exists() || !f.isFile()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            return (Graph<String>) obj;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static void saveGraphToFile(Graph<String> graph, File f) throws IOException {
        if (f == null) throw new IllegalArgumentException("File is null");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(graph);
        }
    }
}