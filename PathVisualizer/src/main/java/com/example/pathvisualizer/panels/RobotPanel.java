package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.VisualizerConstants;
import com.example.pathvisualizer.globalpathing.DriveConstants;
import com.example.pathvisualizer.globalpathing.SplinePath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class RobotPanel extends JPanel {
    Points points;
    SidePanel sidePanel;
    SplinePath splinePath;
    boolean robotSelected = false;
    int[] relativeMousePos;

    public RobotPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        this.points = points;
        this.sidePanel = sidePanel;
        this.splinePath = splinePath;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(10));

        double[] robotPos = splinePath.getRobotPosition(); // already in inches

        if (sidePanel.isEnabled("showCatchRange")) {
            g2.setColor(new Color(255, 0, 0, 50)); 

            // catch range is the radius
            g2.fillOval((int) (robotPos[0] - splinePath.getCatchRange()),
                    (int) (robotPos[1] - splinePath.getCatchRange()),
                    (int) (splinePath.getCatchRange() * 2), (int) (splinePath.getCatchRange() * 2));
        }

        if (sidePanel.isEnabled("showRobot")) {
            g2.setColor(new Color(0,0,255,50));
            g2.fillRect((int) (robotPos[0] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)),
                    (int) (robotPos[1] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)),
                    (int) inchesToPixels(DriveConstants.TRACK_WIDTH),
                    (int) inchesToPixels(DriveConstants.TRACK_WIDTH));

            g2.setColor(new Color(0,0,255));
            g2.drawRect((int) (robotPos[0] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)),
                    (int) (robotPos[1] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)),
                    (int) inchesToPixels(DriveConstants.TRACK_WIDTH),
                    (int) inchesToPixels(DriveConstants.TRACK_WIDTH));
        }
    }

    private double inchesToPixels(double val) {
        return val * ((double) VisualizerConstants.FIELD_WIDTH/144);
    }
}
