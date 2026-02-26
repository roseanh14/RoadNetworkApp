package Data;

import Model.Edge;
import Model.Graph;
import Model.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
do teto tridy bych pridal staticke metody pro nacteni celeho graphu (nody,hrany, proste vse co reprezentuje objekt graph)
loadGraphFromFile a exportovani celeho graphu do souboru saveGraphToFile. Pro serializaci celeho graphu se CSV moc nehodi.
Dal bych to v json nebo xml.
Metodu loadEdgesCsv, pokud je zamysleno ji pouzivat pro pridani dalsich hran ze souboru do jiz nacteneho grafu, bych nechal
tak jak je. Stejne tak metodu saveText.

Dale pridat metody pro reseni souborove cache.
Pridal bych metody loadCache a saveCache.
saveChache ulozi soubor ktery byl nacten pomoci loadGrapchFromFile do stejneho adresare ve kterem se spousti tato aplikace.
saveCache se zavola vzdy kdyz dojde ke zmene graphu.
loadCache nacte cache soubor ktery se pokusi najit ve svem pracovnim adresari. Pokud jej nenacte, vrati null.
 */
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