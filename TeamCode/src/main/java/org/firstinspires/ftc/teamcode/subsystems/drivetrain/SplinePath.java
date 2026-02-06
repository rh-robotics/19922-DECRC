package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

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
 *          influences how linearly it's connecting the dots).
 *      - and then both also take in the list of (x, y, heading) knot points that the spline is approximating
 *
 * variables:
 *      - all the constructor variables (splinePoints, segmentLength, nextPointsRange, catchRange)
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
}
