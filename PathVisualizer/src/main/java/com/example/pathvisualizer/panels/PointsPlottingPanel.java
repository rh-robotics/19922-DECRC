package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.VisualizerConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class PointsPlottingPanel extends JPanel {
    int pointWidth;

    int selectedPointIndex = -1;
    int[] selectedPoint = new int[2];

    private Points points;
    SidePanel sidePanel;

    public PointsPlottingPanel(Points points, int pointWidth, SidePanel sidePanel) {
        this.points = points; // pointer
        this.pointWidth = pointWidth;
        this.sidePanel = sidePanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (sidePanel.isEnabled("drawPathingPoints")) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.red);

            // plot
            for (int i = 0; i < points.getLength(); i++) {
                g2.fillOval((int) points.getX(i) - pointWidth / 2, (int) points.getY(i) - pointWidth / 2, pointWidth, pointWidth);
            }
        }
    }
}
