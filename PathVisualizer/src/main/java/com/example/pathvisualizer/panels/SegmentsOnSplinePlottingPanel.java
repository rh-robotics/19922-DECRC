package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.globalpathing.SplinePath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class SegmentsOnSplinePlottingPanel extends JPanel {
    Points aimablePoints;
    Points pathingPoints;
    SplinePath splinePath;
    double pointWidth = 5;
    SidePanel sidePanel;

    public SegmentsOnSplinePlottingPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        this.pathingPoints = points;
        this.sidePanel = sidePanel;
        this.splinePath = splinePath;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        pointWidth = 5;

        aimablePoints = splinePath.getAimablePoints();

        if (sidePanel.isEnabled("drawSegments")) {
            g.setColor(Color.BLUE);
            for (int i = 1; i < aimablePoints.getLength(); i++) {
                g2.drawLine((int) aimablePoints.getX(i - 1), (int) aimablePoints.getY(i - 1), (int) aimablePoints.getX(i), (int) aimablePoints.getY(i));
            }
        }
        if (sidePanel.isEnabled("drawAimablePoints")) {
            for (int i = 0; i < aimablePoints.getLength(); i++) {
                if (sidePanel.isEnabled("showRobotView")) {
                    pointWidth = 15;

                    if (i < splinePath.getActivePointIndex()) {
                        g.setColor(Color.green);
                    } else if (i == splinePath.getActivePointIndex()) {
                        g.setColor(Color.yellow);
                    } else if (i <= splinePath.getActivePointIndex() + splinePath.getNextPointsRange()) {
                        g.setColor(new Color(100, 0, 255));
                    } else {
                        pointWidth = 5;
                        g.setColor(Color.lightGray);
                    }
                } else {
                    pointWidth = 5;
                    g.setColor(Color.lightGray);
                }

                g2.fillOval((int) (aimablePoints.getX(i) - pointWidth / 2), (int) (aimablePoints.getY(i) - pointWidth / 2), (int) pointWidth, (int) pointWidth);
            }
        }
    }
}
