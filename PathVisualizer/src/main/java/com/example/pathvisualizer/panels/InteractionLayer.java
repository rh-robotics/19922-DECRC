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

public class InteractionLayer extends JPanel {
    int pointWidth = 10;

    int selectedPointIndex = -1;
    boolean draggingPoint = false, draggingRobot = false;
    int[] relativeMousePos = new int[2];
    int[] selectedPoint = new int[2];

    private Points points;
    SidePanel sidePanel;
    SplinePath splinePath;

    PointsPlottingPanel pointsPlottingPanel;
    RobotPanel robotPanel;
    SegmentsOnSplinePlottingPanel segmentsOnSplinePlottingPanel;
    SplinesPlottingPanel splinesPlottingPanel;

    public InteractionLayer(Points points, SplinePath splinePath, SidePanel sidePanel, PointsPlottingPanel pointsPlottingPanel, SegmentsOnSplinePlottingPanel segmentsOnSplinePlottingPanel, SplinesPlottingPanel splinesPlottingPanel, RobotPanel robotPanel) {
        this.points = points; // pointer
        this.sidePanel = sidePanel;

        this.pointsPlottingPanel = pointsPlottingPanel;
        this.robotPanel = robotPanel;
        this.segmentsOnSplinePlottingPanel = segmentsOnSplinePlottingPanel;
        this.splinesPlottingPanel = splinesPlottingPanel;

       this.splinePath = splinePath; // pointer

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectPoint(e); }
            public void mouseReleased(MouseEvent e) { releasePoint(e); }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { dragPoint(e); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (draggingPoint) {
            g2.setColor(Color.magenta);
            g2.drawOval(selectedPoint[0] - pointWidth / 2, selectedPoint[1] - pointWidth / 2, pointWidth, pointWidth);
        }
    }

    private void selectPoint(MouseEvent e) {
        double[] robotPos = splinePath.getRobotPosition();
        if (sidePanel.isEnabled("showRobot") && sidePanel.isEnabled("robotDraggable") &&
                e.getX() < (robotPos[0] + inchesToPixels(DriveConstants.TRACK_WIDTH / 2)) &&
                e.getX() > (robotPos[0] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)) &&
                e.getY() < (robotPos[1] + inchesToPixels(DriveConstants.TRACK_WIDTH / 2)) &&
                e.getY() > (robotPos[1] - inchesToPixels(DriveConstants.TRACK_WIDTH / 2)) ||
                sidePanel.isEnabled("showCatchRange") && sidePanel.isEnabled("robotDraggable") &&
                Math.sqrt(Math.pow(e.getX() - robotPos[0], 2) + Math.pow(e.getY() - robotPos[1], 2)) < splinePath.getCatchRange()) {
            draggingRobot = true;
            relativeMousePos[0] = (int) (e.getX() - robotPos[0]);
            relativeMousePos[1] = (int) (e.getY() - robotPos[1]);

            update();
        } else if (sidePanel.isEnabled("drawPathingPoints") && sidePanel.isEnabled("pointsDraggable")) {
            if (points.isAdjustable) {
                for (int i = 0; i < points.getLength(); i++) {
                    if (Math.abs(e.getX() - points.getX(i)) < pointWidth && Math.abs(e.getY() - points.getY(i)) < pointWidth) {
                        selectedPointIndex = i;
                        draggingPoint = true;

                        update();
                        break;
                    }
                }
            }
        }
    }

    private void releasePoint(MouseEvent e) {
        if (draggingRobot) {
            draggingRobot = false;
        } else if (draggingPoint) {
            points.updatePoint(selectedPointIndex, selectedPoint[0], selectedPoint[1]);

            draggingPoint = false;
            selectedPointIndex = -1;

            update();
        }
    }

    private void dragPoint(MouseEvent e) {
        if (draggingRobot) {
            splinePath.setRobotPosition(e.getX() - relativeMousePos[0], e.getY() - relativeMousePos[1]);
            update();
        } else if (draggingPoint) {
            selectedPoint[0] = Math.max(0, Math.min(VisualizerConstants.FIELD_WIDTH, e.getX())); // clamps e.getX() and e.getY() to within the bounds
            selectedPoint[1] = Math.max(0, Math.min(VisualizerConstants.FIELD_HEIGHT, e.getY()));
            update();
        }
    }

    private double inchesToPixels(double val) {
        return val * ((double) VisualizerConstants.FIELD_WIDTH/144);
    }

    private void update() {
        splinePath.update(); // will also reset robot entity

        pointsPlottingPanel.repaint();
        robotPanel.repaint();
        segmentsOnSplinePlottingPanel.repaint();
        splinesPlottingPanel.repaint();
        repaint();
    }
}
