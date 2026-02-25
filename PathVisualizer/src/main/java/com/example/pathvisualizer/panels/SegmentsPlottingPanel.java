package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.VisualizerConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class SegmentsPlottingPanel extends JPanel {
    Points points;
    SidePanel sidePanel;
    public SegmentsPlottingPanel(Points points, SidePanel sidePanel) {
        this.points = points;
        this.sidePanel = sidePanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.CYAN);

        for (int i = 1; i < points.getLength(); i++) {
            g2.drawLine((int) points.getX(i-1), (int) points.getY(i-1), (int) points.getX(i), (int) points.getY(i));
        }
    }
}
