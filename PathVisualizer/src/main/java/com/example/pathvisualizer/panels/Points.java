package com.example.pathvisualizer.panels;

import com.example.pathvisualizer.VisualizerConstants;

public class Points {
    double[] x; // have to be double[] to use apache spline interpolator
    double[] y;
    double[] h;

    double px = (double) VisualizerConstants.FIELD_WIDTH / 144; // ratios to convert inches to pixels (144 inches in a field)
    double py = (double) VisualizerConstants.FIELD_HEIGHT / 144;

    boolean isAdjustable;
    public Points(double[] x, double[] y, double[] h, boolean isAdjustable) {
        if (x.length != y.length || y.length != h.length) {
            throw new RuntimeException("Points list lengths don't match.");
        }

        this.x = new double[x.length];
        this.y = new double[x.length];
        this.h = h;

        // shift 0,0 from top left to bottom left and convert pixels to inches
        for (int i = 0; i < x.length; i++) {
            this.x[i] = (int) (px * x[i]);
            this.y[i] = (int) (py * (144 - y[i]));
        }

        this.isAdjustable = isAdjustable;
    }

    public Points(double[][] xy) {
        x = new double[xy.length];
        y = new double[xy.length];
        h = new double[xy.length];

        for (int i = 0; i < xy.length; i++) {
            this.x[i] = xy[i][0]; // already converted once. this is only used by segment plotting.
            this.y[i] = xy[i][1];
        }

        this.isAdjustable = false;
    }

    public void updatePoint(int i, int x, int y, int h) {
        this.x[i] = x;
        this.y[i] = y;
        this.h[i] = h;
    }

    public void updatePoint(int i, int x, int y) {
        this.x[i] = x;
        this.y[i] = y;
    }

    public int getLength() {
        return x.length;
    }

    public double getH(int i) {
        return h[i];
    }

    public double getX(int i) {
        return x[i];
    }

    public double getY(int i) {
        return y[i];
    }

    public double[] getXList() {
        return x;
    }

    public double[] getYList() {
        return y;
    }

    public double[] getHList() {
        return h;
    }
}
