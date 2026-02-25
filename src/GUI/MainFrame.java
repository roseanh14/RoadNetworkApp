package GUI;

import Data.GraphData;
import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("RoadNetworkApp");

        Graph<Node<String>, Edge<Node<String>>> graph = GraphData.buildGraph();
        GraphPanel graphPanel = new GraphPanel(graph);
        ResultPanel resultPanel = new ResultPanel();
        ControlPanel controlPanel = new ControlPanel(graph, graphPanel, resultPanel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Network Visualization (Graph)", graphPanel);
        tabs.addTab("Results and Successor Vector", resultPanel);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}