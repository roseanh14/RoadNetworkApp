package Main;

import Data.GraphData;
import GUI.MainFrame;
import Model.Graph;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Graph graph = GraphData.buildGraph();
        SwingUtilities.invokeLater(() -> new MainFrame(graph));
    }
}