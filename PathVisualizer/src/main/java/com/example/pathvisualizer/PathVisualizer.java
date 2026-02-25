package com.example.pathvisualizer;

import com.example.pathvisualizer.globalpathing.DriveConstants;
import com.example.pathvisualizer.globalpathing.SplinePath;
import com.example.pathvisualizer.panels.InteractionLayer;
import com.example.pathvisualizer.panels.Points;
import com.example.pathvisualizer.panels.PointsPlottingPanel;
import com.example.pathvisualizer.panels.RobotPanel;
import com.example.pathvisualizer.panels.SegmentsOnSplinePlottingPanel;
import com.example.pathvisualizer.panels.SidePanel;
import com.example.pathvisualizer.panels.SplinesPlottingPanel;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PathVisualizer {
    Points points;
    SidePanel sidePanel;
    SplinePath splinePath;

    public  PathVisualizer() throws IOException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(VisualizerConstants.FIELD_WIDTH + VisualizerConstants.SIDE_PANEL_WIDTH, VisualizerConstants.FIELD_HEIGHT);
        frame.setResizable(false);

        JPanel root = new JPanel(null); // null gets rid of automatic layout formatting
        frame.setContentPane(root);

        // set bg
        ImageIcon bg = new ImageIcon(ImageIO.read(new File("PathVisualizer/src/main/java/com/example/pathvisualizer/images/DecodeField.png")).getScaledInstance(VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT, Image.SCALE_SMOOTH));
        JLabel field = new JLabel(bg);
        field.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);
        root.add(field);

        // add side panel
        sidePanel = new SidePanel();
        sidePanel.setBounds(
                VisualizerConstants.FIELD_WIDTH, 0, VisualizerConstants.SIDE_PANEL_WIDTH, VisualizerConstants.FIELD_HEIGHT / 2);
        root.add(sidePanel);

        double[] x = {10, 46, 100, 50, 40};
        double[] y = {10, 120, 50, 20, 80};
        double[] h = new double[5];

        points = new Points(x, y, h, true);
        splinePath = new SplinePath(points, inchesToPixels(DriveConstants.DEFAULT_SEGMENT_LENGTH), DriveConstants.DEFAULT_NEXT_POINTS_RANGE, inchesToPixels(DriveConstants.DEFAULT_CATCH_RANGE));

        RobotPanel robotPanel = getRobotPanel(points, splinePath, sidePanel);
        field.add(robotPanel);

        PointsPlottingPanel pointsPlot = getPointsPlottingPanel(points, sidePanel);
        field.add(pointsPlot);

        SegmentsOnSplinePlottingPanel segmentsPlot = getSegmentsOnSplinePlottingPanel(points, splinePath, sidePanel);
        field.add(segmentsPlot);

        SplinesPlottingPanel splinePlot = getSplinesPlottingPanel(points, splinePath, sidePanel);
        field.add(splinePlot);

        InteractionLayer interactionLayer = getInteractionLayer(points, splinePath, sidePanel, pointsPlot, segmentsPlot, splinePlot, robotPanel);
        field.add(interactionLayer);

        sidePanel.addChangeListener(() -> {
            pointsPlot.repaint();
            segmentsPlot.repaint();
            splinePlot.repaint();
            robotPanel.repaint();
        });

        frame.setVisible(true);
    }

    public void update() {

    }

    private static SplinesPlottingPanel getSplinesPlottingPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        SplinesPlottingPanel splinePlot = new SplinesPlottingPanel(points, splinePath, sidePanel);
        splinePlot.setOpaque(false);
        splinePlot.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);

        return splinePlot;
    }

    private static PointsPlottingPanel getPointsPlottingPanel(Points points, SidePanel sidePanel) {
        PointsPlottingPanel pointsPlot = new PointsPlottingPanel(points, 10, sidePanel);
        pointsPlot.setOpaque(false);
        pointsPlot.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);

        return pointsPlot;
    }

    private static SegmentsOnSplinePlottingPanel getSegmentsOnSplinePlottingPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        SegmentsOnSplinePlottingPanel segmentsPlot = new SegmentsOnSplinePlottingPanel(points, splinePath, sidePanel);
        segmentsPlot.setOpaque(false);
        segmentsPlot.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);

        return segmentsPlot;
    }

    private static RobotPanel getRobotPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        RobotPanel robotPanel = new RobotPanel(points, splinePath, sidePanel);
        robotPanel.setOpaque(false);
        robotPanel.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);

        return robotPanel;
    }

    private static InteractionLayer getInteractionLayer(Points points, SplinePath splinePath, SidePanel sidePanel, PointsPlottingPanel pointsPlottingPanel, SegmentsOnSplinePlottingPanel segmentsOnSplinePlottingPanel, SplinesPlottingPanel splinesPlottingPanel, RobotPanel robotPanel) {
        InteractionLayer interactionLayer = new InteractionLayer(points, splinePath, sidePanel, pointsPlottingPanel, segmentsOnSplinePlottingPanel, splinesPlottingPanel, robotPanel);
        interactionLayer.setOpaque(false);
        interactionLayer.setBounds(0, 0, VisualizerConstants.FIELD_WIDTH, VisualizerConstants.FIELD_HEIGHT);

        return interactionLayer;
    }

    private double inchesToPixels(double val) {
        return val * ((double) VisualizerConstants.FIELD_WIDTH/144);
    }
}