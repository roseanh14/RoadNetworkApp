package Data;

import Model.Graph;

import java.awt.Point;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class GraphFileIO {

    private static final String CACHE_FILE_NAME = "graph.cache";

    public static void loadEdgesCsv(Graph<String, Point, Double> graph, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine(); // header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 3) continue;

                String fromId = p[0].trim();
                String toId   = p[1].trim();
                double w      = Double.parseDouble(p[2].trim());

                var from = graph.getVertex(fromId);
                var to   = graph.getVertex(toId);
                if (from == null || to == null) continue;

                graph.setUndirectedEdgeData(from, to, w);
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
    public static Graph<String, Point, Double> loadCache() {
        File f = new File(CACHE_FILE_NAME);
        if (!f.exists() || !f.isFile()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            return (Graph<String, Point, Double>) obj;
        } catch (Exception ex) {
            return null;
        }
    }

}