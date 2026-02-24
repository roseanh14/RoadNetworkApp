package GUI;

import javax.swing.*;
import java.awt.*;

// Panel where calculation results are displayed as text
public class ResultPanel extends JPanel {

    private JTextArea textArea;

    public ResultPanel() {
        setLayout(new BorderLayout());
        textArea = new JTextArea("Calculation results will appear here...\n");
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea));
    }

    public void show(String text) {
        textArea.setText(text);
    }
}