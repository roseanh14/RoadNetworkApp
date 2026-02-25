package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GraphFileIO {

    public static void loadEdgesCsv(Graph<Node<String>, Edge<Node<String>>> graph, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 3) continue;

                String fromId = p[0].trim();
                String toId   = p[1].trim();
                double w      = Double.parseDouble(p[2].trim());

                Node<String> from = graph.getNodeById(fromId);
                Node<String> to   = graph.getNodeById(toId);
                if (from == null || to == null) continue;

                graph.setUndirectedEdgeWeight(from, to, w);
            }
        }
    }

    public static void saveText(File file, String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(text == null ? "" : text);
        }
    }
}