package GUI;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private final JTextArea area = new JTextArea();

    public ResultPanel() {
        setLayout(new BorderLayout());
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void show(String text) {
        area.setText(text == null ? "" : text);
        area.setCaretPosition(0);
    }

    public String getText() {
        return area.getText();
    }
}