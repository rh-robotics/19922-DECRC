package com.example.pathvisualizer.globalpathing;

public class DriveConstants {
    public final static double TRACK_WIDTH = 16.0; // inches
    public static double SERVO_MAX_ROTATION = 640; // degrees
    public final static double ROTATION_SPEED_DAMPENER = 0.1; // turn dampener
    public final static double AUTON_CHANGE_CONSTANT = 0.01; // increment between runs
    public final static double TELEOP_CHANGE_CONSTANT = 0.0001; // increment to solve for derivative

    public final static double PATH_FOLLOWING_TOLERANCE = 5; // inches
    public final static double SERVO_TOLERANCE = 0; // fraction of 1

    public final static double LEFTFRONT_ZERO = 5;
    public final static double RIGHTFRONT_ZERO = 10;
    public final static double LEFTREAR_ZERO = 5;
    public final static double RIGHTREAR_ZERO = 5;

    public final static boolean LEFTFRONT_ISREVERSED = true;
    public final static boolean RIGHTFRONT_ISREVERSED = true;
    public final static boolean LEFTREAR_ISREVERSED = false;
    public final static boolean RIGHTREAR_ISREVERSED = false;

    // Kp, Ki, Kd, Kf
    public final static double[] LEFT_FRONT_PID = new double[] {0, 0, 0, 0};
    public final static double[] RIGHT_FRONT_PID = new double[] {0, 0, 0, 0};
    public final static double[] LEFT_REAR_PID = new double[] {0, 0, 0, 0};
    public final static double[] RIGHT_REAR_PID = new double[] {0, 0, 0, 0};

    public final static double TURN_MAX_SPEED = 0.5;

    // Spline path default values
    public static final int DEFAULT_SPLINE_POINTS = 3;
    public static final double DEFAULT_SEGMENT_LENGTH = 5; // in
    public static final int DEFAULT_NEXT_POINTS_RANGE = 5;
    public static final double DEFAULT_CATCH_RANGE = 24; // in
    public static final double SPLINE_CONFORMITY = 2; // higher = higher conformity to the spline. doesn't go negative but can go to decimals
}
