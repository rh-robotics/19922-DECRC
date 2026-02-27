package com.example.pathvisualizer.globalpathing;

import com.example.pathvisualizer.VisualizerConstants;
import com.example.pathvisualizer.panels.Points;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.awt.Robot;
// THIS IS SO STUPID
// TODO: make this a seperate repo ig

/* A template path for autonomous pathing
 *
 * Contains the polynomials that make up the spline in the form of an apache PolynomialSplineFunction and made via apache SplineInterpolator,
 * a list of the actual points that the robot will aim for and check to see if they're within range to update the point its aiming for,
 * as well as an active index for the list of points for the sake of knowing which point is being aimed for and which points are being checked
 * to determine if they're in range to aim for a next one.
 *
 * Also holds the robot's state relative to the path via the active point
 *
 * Constructor params:
 *      - default one, builds it off DriveConstants
 *      - customizable ones for the distance length of the segments between the points
 *          actually aimed at (influences how closely it's going to follow the spline. also the ratio between this and the "catch" range will
 *          influence how linear and wobbly the path the actual robot follows is going to be), the number of points ahead the robot checks
 *          (determines the acceptable "skip ahead" range. if this is too low, it could cause the robot to spaz out since it will force it to
 *          be hitting every point), and the "catch" range of the robot–how far out is considered to be within range of a point to make it the new
 *          active point that we're aiming at (influences how closely it's following the spline and also the ratio between this and segment length
 *          influences how linearly it's connecting the dots. this and segment length should be high but if they're too high there could be problems
 *          with skipping ahead).
 *              - number of points originally plotted on the actual spline (influences, in combo with other variables, how closely
 *          the robot will follow the actual spline path and thus the original points) made into a drive constant (spline conformity)
 *      - and then both also take in the list of (x, y, heading) knot points that the spline is approximating
 *
 * variables:
 *      - all the constructor variables (splineConformity, segmentLength, nextPointsRange, catchRange)
 *      - polynomial splines: PolynomialSplineFunction approximations from points (polynomialSplineX, polynomialSplineY, polynomialSplineHeading)
 *      - !! and most importantly, what we actually use: aimablePoints; the list of (x, y, heading) points that can be aimed at
 *
 * functions:
 *      - all the constructor ones
 *      - getActivePoint() -> checks next points for hits and then returns the active point
 *             - checkNextPointsForHits(robotPosition) -> checks if next <nextPointsRange> points are in <catchRange> of robot and moves <activePoint> up if so
 *                     - note that this just checks if x and y are within range not the heading
 *      - drawToDashboard()
 *              - plotKnotPoints(), drawSpline()
 *              - plotAimablePoints()
 *              - drawRobot(robotPosition)
 *                     - drawActiveTrajectory() -> draws a line w/ arrow indicating the active trajectory
 *                     - colorAimablePoints() -> default points: white, passed points: grey, active point: green, points being checked: purple
 *      - reachedEnd() returns boolean indicating if we're aiming at the final point so we know to use a PID to get there and also at the end to run
 *              a separate PID to correct heading
 */
public class SplinePath {
    PolynomialSplineFunction xSpline, ySpline, headingSpline;
    double[] tList;
    double[][] aimablePoints;
    double[][] splinePoints; // global mostly for the sake of plotting
    double segmentLength;
    int nextPointsRange;
    double catchRange;
    Points points;
    RobotEntity robot;

    public SplinePath(Points points) {
        this.points = points;

        verifyEqualListLengths(points.getXList(), points.getYList(), points.getHList());
        tList = getSequentialListOfLength(points.getLength());

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, points.getXList());
        ySpline = splineInterpolator.interpolate(tList, points.getYList());
        headingSpline = splineInterpolator.interpolate(tList, points.getHList());

        segmentLength = DriveConstants.DEFAULT_SEGMENT_LENGTH;
        nextPointsRange = DriveConstants.DEFAULT_NEXT_POINTS_RANGE;
        catchRange = DriveConstants.DEFAULT_CATCH_RANGE;

        findAimablePoints();

        robot = new RobotEntity(points.getX(0), points.getY(0), points.getH(0), catchRange);
        updateRobot();
    }

    public SplinePath(Points points, double segmentLength, int nextPointsRange, double catchRange, double[] robotPosition) {
        this.points = points;

        verifyEqualListLengths(points.getXList(), points.getYList(), points.getHList());
        tList = getSequentialListOfLength(points.getLength());

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, points.getXList());
        ySpline = splineInterpolator.interpolate(tList, points.getYList());
        headingSpline = splineInterpolator.interpolate(tList, points.getHList());

        this.segmentLength = segmentLength;
        this.nextPointsRange = nextPointsRange;
        this.catchRange = catchRange;

        findAimablePoints();

        robot = new RobotEntity(robotPosition[0], robotPosition[1], robotPosition[2], catchRange);
        updateRobot();
    }

    public SplinePath(Points points, double segmentLength, int nextPointsRange, double catchRange) {
        this.points = points;

        verifyEqualListLengths(points.getXList(), points.getYList(), points.getHList());
        tList = getSequentialListOfLength(points.getLength());

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, points.getXList());
        ySpline = splineInterpolator.interpolate(tList, points.getYList());
        headingSpline = splineInterpolator.interpolate(tList, points.getHList());

        this.segmentLength = segmentLength;
        this.nextPointsRange = nextPointsRange;
        this.catchRange = catchRange;

        findAimablePoints();

        robot = new RobotEntity(points.getX(0), points.getY(0), points.getH(0), catchRange);
        updateRobot();
    }

    public void updateRobot() {
        boolean inRange = true;

        while (inRange) {
            inRange = false;
            for (int i = robot.activePointIndex + 1; i < Math.min(robot.activePointIndex + nextPointsRange + 1, aimablePoints.length); i++) {
                if (robot.inRange(aimablePoints[i])) {
                    inRange = true;
                    robot.setActivePointIndex(i);
                }
            }
        }
    }

    public void setRobotPosition(double x, double y, double heading) {
        robot.updatePosition(x, y, heading);
    }

    public void setRobotPosition(double x, double y) {
        robot.updatePosition(x, y);
    }

    public double[] getRobotPosition() {
        return new double[] {robot.x, robot.y, robot.heading};
    }

    private void findAimablePoints() {
        // find approx length of path by connecting splinePoints
        double len = 0;
        for (int i = 1; i < points.getLength(); i++) {
            len += Math.sqrt(Math.pow(points.getX(i) - points.getX(i-1), 2) + Math.pow(points.getY(i) - points.getY(i-1), 2)); // sqrt(x^2 + y^2)
        }

        // based on approx length and segmentLength, decently approximate the increment of t needed to plot enough points to have a decent approximation of the spline to make segments from
        double increment = tList[tList.length - 1] / (len / segmentLength * DriveConstants.SPLINE_CONFORMITY);
        splinePoints = new double[(int) (len / segmentLength * DriveConstants.SPLINE_CONFORMITY)][3];

        splinePoints[0][0] = xSpline.value(0);
        splinePoints[0][1] = ySpline.value(0);
        splinePoints[0][2] = headingSpline.value(0);

        double t = increment;
        for (int i = 1; i < splinePoints.length - 1; i++) { // first and last done manually
            splinePoints[i][0] = xSpline.value(t);
            splinePoints[i][1] = ySpline.value(t);
            splinePoints[i][2] = headingSpline.value(t);

            t += increment;
        }
        splinePoints[splinePoints.length - 1][0] = xSpline.value(tList[tList.length - 1]);
        splinePoints[splinePoints.length - 1][1] = ySpline.value(tList[tList.length - 1]);
        splinePoints[splinePoints.length - 1][2] = headingSpline.value(tList[tList.length - 1]);

        len = 0; // produces a more consistent segment length
        for (int i = 1; i < splinePoints.length; i++) {
            len += Math.sqrt(Math.pow(splinePoints[i][0] - splinePoints[i-1][0], 2) + Math.pow(splinePoints[i][1] - splinePoints[i-1][1], 2)); // sqrt(x^2 + y^2)
        }

        int numberOfSegments = (int) Math.ceil(len/segmentLength); // last segment should be less, not more
        aimablePoints = new double[numberOfSegments + 1][3]; // list of (x,y,heading) points

        // set point
        aimablePoints[0][0] = splinePoints[0][0];
        aimablePoints[0][1] = splinePoints[0][1];
        aimablePoints[0][2] = splinePoints[0][2];

        int nextSplinePointIndex = 1; // next index on splinePoints that hasn't been reached yet
        double[] lastPoint = new double[] {splinePoints[0][0], splinePoints[0][1], splinePoints[0][2]};
        for (int i = 1; i < numberOfSegments; i++) { // first and last need to be done manually, last since totalDist will be smaller than segmentLength
            double totalDist = 0;

            while (totalDist < segmentLength) {
                // if the distance between the last point and the next point (given the nextSplinePointIndex) is less than segmentLength
                // add it to dist and increase nextSplinePointIndex and lastPoint
                double nextDist = Math.sqrt(Math.pow(splinePoints[nextSplinePointIndex][0] - lastPoint[0], 2) +
                        Math.pow(splinePoints[nextSplinePointIndex][1] - lastPoint[1], 2));

                if (totalDist + nextDist < segmentLength) {
                    totalDist += nextDist;
                    lastPoint = new double[] {splinePoints[nextSplinePointIndex][0], splinePoints[nextSplinePointIndex][1], splinePoints[nextSplinePointIndex][2]};
                    nextSplinePointIndex ++;
                } else { // current segment exceeds segmentLength, find the point btw that makes the right segment length and update lastPoint
                    double ratio = (segmentLength - totalDist) / nextDist;

                    aimablePoints[i][0] = lastPoint[0] + (ratio * (splinePoints[nextSplinePointIndex][0] - lastPoint[0]));
                    aimablePoints[i][1] = lastPoint[1] + (ratio * (splinePoints[nextSplinePointIndex][1] - lastPoint[1]));
                    aimablePoints[i][2] = lastPoint[2] + (ratio * (splinePoints[nextSplinePointIndex][2] - lastPoint[2]));

                    lastPoint[0] = aimablePoints[i][0];
                    lastPoint[1] = aimablePoints[i][1];
                    lastPoint[2] = aimablePoints[i][2];

                    totalDist = segmentLength; // not adding bc adding could cause rounding errors
                }
            }
        }

        aimablePoints[numberOfSegments][0] = splinePoints[splinePoints.length - 1][0];
        aimablePoints[numberOfSegments][1] = splinePoints[splinePoints.length - 1][1];
        aimablePoints[numberOfSegments][2] = splinePoints[splinePoints.length - 1][2];
    }

    public void update() {
        verifyEqualListLengths(points.getXList(), points.getYList(), points.getHList());
        tList = getSequentialListOfLength(points.getLength());

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, points.getXList());
        ySpline = splineInterpolator.interpolate(tList, points.getYList());
        headingSpline = splineInterpolator.interpolate(tList, points.getHList());

        findAimablePoints();
        updateRobot();
    }

    public PolynomialSplineFunction getXSpline() {
        return xSpline;
    }

    public PolynomialSplineFunction getYSpline() {
        return ySpline;
    }

    public Points getAimablePoints() {
        return new Points(aimablePoints);
    }

    public double[] getActivePoint() {
        return aimablePoints[robot.getActivePointIndex()];
    }

    private double[] getSequentialListOfLength(int len) {
        double[] list = new double[len];

        for (int i = 0; i < len; i++) {
            list[i] = i;
        }

        return list;
    }

    private void verifyEqualListLengths(double[] x, double[] y, double[] h) {
        if (x.length != y.length || y.length != h.length) {
            throw new RuntimeException("SplinePath list lengths unequal");
        }
    }

    public double getCatchRange() {
        return catchRange;
    }

    public double getNextPointsRange() {
        return nextPointsRange;
    }

    public double getActivePointIndex() {
        return robot.activePointIndex;
    }
}
