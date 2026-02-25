package com.example.pathvisualizer.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SidePanel extends JPanel {
    private final Map<String, JCheckBox> checkBoxes = new HashMap<>();

    public SidePanel() {
        setLayout(new GridLayout(0, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("View Options"));

        // drawing toggles
        addCheckbox("drawSpline", "Draw spline", false);
        addCheckbox("drawSegments", "Draw segments", true);
        addCheckbox("drawAimablePoints", "Draw aimable points", true);
        addCheckbox("drawPathingPoints", "Draw pathing points", true);
        addCheckbox("showRobot", "Show robot", true);
        addCheckbox("showCatchRange", "Show catch range", false);
        addCheckbox("showRobotView", "Show robot view", false);

        addSeparator();

        // interaction toggles
        addCheckbox("robotDraggable", "Robot draggable", true);
        addCheckbox("pointsDraggable", "Points draggable", true);
    }

    private void addCheckbox(String key, String label, boolean defaultValue) {
        JCheckBox box = new JCheckBox(label, defaultValue);
        checkBoxes.put(key, box);
        add(box);
    }

    private void addSeparator() {
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(1, 10));
        add(spacer);
    }

    public boolean isEnabled(String key) {
        JCheckBox box = checkBoxes.get(key);
        return box != null && box.isSelected();
    }

    public void addChangeListener(Runnable onChange) {
        checkBoxes.values().forEach(box ->
                box.addActionListener(e ->
                        SwingUtilities.invokeLater(onChange)
                )
        );
    }
}
