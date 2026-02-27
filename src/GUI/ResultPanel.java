package GUI;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private final JTextArea textArea;

    public ResultPanel() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void show(String text) {
        if (text == null || text.isEmpty()) return;

        if (!textArea.getText().isEmpty()) {
            textArea.append("\n----------------------------------------\n");
        }

        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public String getText() {
        return textArea.getText();
    }
}