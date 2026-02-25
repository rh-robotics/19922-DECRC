package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.VisualizerConstants;
import com.example.pathvisualizer.globalpathing.SplinePath;

import javax.swing.JPanel;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class SplinesPlottingPanel extends JPanel {
    Points points;
    PolynomialSplineFunction xSpline, ySpline, headingSpline;
    SidePanel sidePanel; // pointer to global
    SplinePath splinePath;

    public SplinesPlottingPanel(Points points, SplinePath splinePath, SidePanel sidePanel) {
        this.points = points;
        this.sidePanel = sidePanel;
        this.splinePath = splinePath;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (sidePanel.isEnabled("drawSpline")) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g.setColor(Color.GREEN);

            xSpline = splinePath.getXSpline();
            ySpline = splinePath.getYSpline();

            int[][] splineSamples = new int[VisualizerConstants.SPLINE_PLOTTING_SAMPLE_COUNT][2];

            double tMax = xSpline.getKnots()[xSpline.getKnots().length - 1];

            double t = 0;
            for (int i = 0; i < VisualizerConstants.SPLINE_PLOTTING_SAMPLE_COUNT; i++) {
                splineSamples[i][0] = (int) xSpline.value(t);
                splineSamples[i][1] = (int) ySpline.value(t);

                t += tMax / (VisualizerConstants.SPLINE_PLOTTING_SAMPLE_COUNT - 1); // -1 so we get the last point too
            }

            // plot
            for (int i = 1; i < splineSamples.length; i++) {
                g2.drawLine(splineSamples[i - 1][0], splineSamples[i - 1][1], splineSamples[i][0], splineSamples[i][1]);
            }
        }
    }
}
