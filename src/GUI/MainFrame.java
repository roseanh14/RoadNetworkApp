package GUI;

import Model.Graph;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Graph graph) {
        setTitle("Alternative Route Planning");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GraphPanel   graphPanel   = new GraphPanel(graph);
        ResultPanel  resultPanel  = new ResultPanel();
        ControlPanel controlPanel = new ControlPanel(graph, graphPanel, resultPanel);

        // Two tabs in the center
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Network Visualization (Graph)", graphPanel);
        tabs.addTab("Results and Successor Vector",  resultPanel);

        add(controlPanel, BorderLayout.WEST);
        add(tabs,         BorderLayout.CENTER);

        setVisible(true);
    }
}