package GUI;

import Model.Edge;
import Model.Graph;
import Model.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JPanel {

    private static final Color DARK = new Color(30, 40, 60), DARK2 = new Color(40, 52, 75);
    private final JComboBox<String> start = new JComboBox<>(), end = new JComboBox<>();
    private final ControlActions a;

    public ControlPanel(Graph<Node<String>, Edge<Node<String>>> g, GraphPanel gp, ResultPanel rp) {
        this.a = new ControlActions(g, gp, rp, this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(DARK);
        setPreferredSize(new Dimension(220, 600));

        a.fill(start, end);
        start.setSelectedItem("z");
        end.setSelectedItem("w");

        add(section("1. Calculate Routes",
                label("Start node:"), start,
                label("End node:"), end,
                button("Calculate Routes", () -> a.routes(val(start), val(end)), new Color(52,152,219))
        ));

        add(section("2. Manage Nodes",
                button("Add Node", a::addNode),
                button("Find Node", a::findNode)
        ));

        add(section("3. Manage Roads",
                button("Edit Weight",  () -> a.edge(ControlActions.EdgeOp.EDIT_WEIGHT)),
                button("Add Road",     () -> a.edge(ControlActions.EdgeOp.ADD)),
                button("Remove Road",  () -> a.edge(ControlActions.EdgeOp.REMOVE)),
                button("Block road (temporarily)", a::blockTemporary, new Color(231, 76, 60)),
                button("Unblock road", a::unblock, new Color(46, 204, 113))
        ));

        add(section("4. File (Load / Save)",
                button("Load edges.csv", a::loadFromFile),
                button("Save result.txt", a::saveResult)
        ));
    }

    private static String val(JComboBox<String> b){ Object v=b.getSelectedItem(); return v==null?null:v.toString(); }

    private JPanel section(String title, JComponent... items) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DARK2);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(80,100,130)),
                        title,0,0,new Font("Arial",Font.BOLD,11),Color.LIGHT_GRAY),
                new EmptyBorder(6,8,8,8)
        ));
        p.setMaximumSize(new Dimension(220, 380));
        for (int i=0;i<items.length;i++){ p.add(items[i]); if(i<items.length-1) p.add(Box.createVerticalStrut(6)); }
        return p;
    }

    private JLabel label(String t){ JLabel l=new JLabel(t); l.setForeground(Color.LIGHT_GRAY); l.setFont(new Font("Arial",Font.PLAIN,11)); return l; }

    private JButton button(String t, Runnable r){ return button(t,r,new Color(55,70,95)); }
    private JButton button(String t, Runnable r, Color bg){
        JButton b=new JButton(t);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(200,28));
        b.addActionListener(e->r.run());
        return b;
    }
}