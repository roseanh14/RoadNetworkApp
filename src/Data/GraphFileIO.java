package Data;

import Model.Graph;

import java.awt.Point;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class GraphFileIO {

    private static final String CACHE_FILE_NAME = "graph.cache";

    public static void loadGraphCsv(Graph<String, Point, Double> graph, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine();
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 1) continue;

                String type = p[0].trim();
                if (type.equalsIgnoreCase("V")) {
                    if (p.length < 4) continue;

                    String id = p[1].trim();
                    if (id.isEmpty()) continue;

                    int x, y;
                    try {
                        x = Integer.parseInt(p[2].trim());
                        y = Integer.parseInt(p[3].trim());
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    graph.putVertex(id, new Point(x, y));
                }
                else if (type.equalsIgnoreCase("E")) {
                    if (p.length < 7) continue;

                    String from = p[4].trim();
                    String to   = p[5].trim();

                    if (from.isEmpty() || to.isEmpty()) continue;

                    double w;
                    try {
                        w = Double.parseDouble(p[6].trim());
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    if (graph.getVertex(from) == null || graph.getVertex(to) == null) continue;

                    graph.setUndirectedEdgeData(from, to, w);
                }
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