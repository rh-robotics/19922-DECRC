package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

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
 *      - customizable ones for number of points originally plotted on the actual spline (influences, in combo with other variables, how closely
 *          the robot will follow the actual spline path and thus the original points), the distance length of the segments between the points
 *          actually aimed at (influences how closely it's going to follow the spline. also the ratio between this and the "catch" range will
 *          influence how linear and wobbly the path the actual robot follows is going to be), the number of points ahead the robot checks
 *          (determines the acceptable "skip ahead" range. if this is too low, it could cause the robot to spaz out since it will force it to
 *          be hitting every point), and the "catch" range of the robot–how far out is considered to be within range of a point to make it the new
 *          active point that we're aiming at (influences how closely it's following the spline and also the ratio between this and segment length
 *          influences how linearly it's connecting the dots. this and segment length should be high but if they're too high there could be problems
 *          with skipping ahead).
 *      - and then both also take in the list of (x, y, heading) knot points that the spline is approximating
 *
 * variables:
 *      - all the constructor variables (numberOfSplinePoints, segmentLength, nextPointsRange, catchRange)
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
    int numberOfSplinePoints;
    double segmentLength;
    int nextPointsRange;
    double catchRange;

    // CONSTRUCTORS
    public SplinePath(double[] xValues, double[] yValues, double[] headingValues) {
        verifyEqualListLengths(xValues, yValues, headingValues);
        tList = getSequentialListOfLength(xValues);

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, xValues);
        ySpline = splineInterpolator.interpolate(tList, yValues);
        headingSpline = splineInterpolator.interpolate(tList, headingValues);

        numberOfSplinePoints = DriveConstants.DEFAULT_SPLINE_POINTS;
        segmentLength = DriveConstants.DEFAULT_SEGMENT_LENGTH;
        nextPointsRange = DriveConstants.DEFAULT_NEXT_POINTS_RANGE;
        catchRange = DriveConstants.DEFAULT_CATCH_RANGE;

        findAimablePoints();
    }

    public SplinePath(double[] xValues, double[] yValues, double[] headingValues, int numberOfSplinePoints, double segmentLength, int nextPointsRange, double catchRange) {
        verifyEqualListLengths(xValues, yValues, headingValues);
        tList = getSequentialListOfLength(xValues);

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        xSpline = splineInterpolator.interpolate(tList, xValues);
        ySpline = splineInterpolator.interpolate(tList, yValues);
        headingSpline = splineInterpolator.interpolate(tList, headingValues);

        this.numberOfSplinePoints = numberOfSplinePoints;
        this.segmentLength = segmentLength;
        this.nextPointsRange = nextPointsRange;
        this.catchRange = catchRange;

        findAimablePoints();
    }

    private void findAimablePoints() {
        // plot numberOfSplinePoints # of points on spline
        double increment = (double) (tList.length - 1) / (numberOfSplinePoints - 1); // increment to end up with numberOfSplinePoints # of points across tList.length amount of space
        splinePoints = new double[numberOfSplinePoints][3];

        // plot numberOfSplinePoints # of points from splines
        double t = 0;
        for (int i = 0; i < numberOfSplinePoints; i++) {
            splinePoints[i][0] = xSpline.value(t);
            splinePoints[i][1] = ySpline.value(t);
            splinePoints[i][2] = headingSpline.value(t);
            
            t += increment;
        }
        
        // find approx length of path by connecting splinePoints
        double len = 0;
        for (int i = 0; i < numberOfSplinePoints; i++) {
            len += Math.sqrt(Math.pow(splinePoints[i][0], 2) + Math.pow(splinePoints[i][1], 2)); // sqrt(x^2 + y^2)
        }

        int numberOfSegments = (int) Math.ceil(len/segmentLength); // last segment should be less, not more
        aimablePoints = new double[numberOfSegments][3]; // list of (x,y,heading) points

        // set point
        aimablePoints[0][0] = splinePoints[0][0];
        aimablePoints[0][1] = splinePoints[0][1];
        aimablePoints[0][2] = splinePoints[0][2];

        int nextSplinePointIndex = 1; // next index on splinePoints that hasn't been reached yet
        double[] lastPoint = new double[] {splinePoints[0][0], splinePoints[0][1]};
        for (int i = 1; i < numberOfSegments - 1; i++) { // first and last need to be done manually, last since totalDist will be smaller than segmentLength
            double totalDist = 0;

            while (totalDist < segmentLength) {
                // if the distance between the last point and the next point (given the nextSplinePointIndex) is less than segmentLength
                // add it to dist and increase nextSplinePointIndex and lastPoint
                double nextDist = Math.sqrt(Math.pow(splinePoints[nextSplinePointIndex][0] - lastPoint[0], 2) +
                        Math.pow(splinePoints[nextSplinePointIndex][1] - lastPoint[1], 2));

                if (totalDist + nextDist < segmentLength) {
                    totalDist += nextDist;
                    nextSplinePointIndex ++;
                    lastPoint = new double[] {splinePoints[nextSplinePointIndex][0], splinePoints[nextSplinePointIndex][1]};
                } else { // current segment exceeds segmentLength, find the point btw that makes the right segment length and update lastPoint
                    double ratio = segmentLength / nextDist;

                    aimablePoints[i][0] = lastPoint[0] + (ratio * (splinePoints[nextSplinePointIndex][0] - lastPoint[0]));
                    aimablePoints[i][1] = lastPoint[0] + (ratio * (splinePoints[nextSplinePointIndex][0] - lastPoint[0]));
                    aimablePoints[i][2] = lastPoint[0] + (ratio * (splinePoints[nextSplinePointIndex][0] - lastPoint[0]));

                    totalDist = segmentLength; // not adding bc adding could cause rounding errors
                }
            }
        }

        aimablePoints[numberOfSegments - 1][0] = splinePoints[numberOfSegments - 1][0];
        aimablePoints[numberOfSegments - 1][1] = splinePoints[numberOfSegments - 1][1];
        aimablePoints[numberOfSegments - 1][2] = splinePoints[numberOfSegments - 1][2];
    }

    private double[] getSequentialListOfLength(double[] ref) {
        int len = ref.length;
        double[] list = new double[len];

        for (int i = 0; i < len; i++) {
            list[i] = i;
        }

        return list;
    }

    private void verifyEqualListLengths(double[] l1, double[] l2, double[] l3) {
        if (l1.length != l2.length || l2.length != l3.length) {
            throw new RuntimeException("SplinePath list lengths unequal");
        }
    }
}
