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

    //upravit aby si pamatovalo i predchozi text, a pridavalo to novy text pod sebe
    public void show(String text) {
        textArea.setText(text == null ? "" : text);
        textArea.setCaretPosition(0);
    }

    public String getText() {
        return textArea.getText();
    }
}