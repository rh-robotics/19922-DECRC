package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.acmerobotics.dashboard.config.Config;

@Config
public class DriveConstants {
    public final static double TRACK_WIDTH = 16.0; // inches
    public static double SERVO_MAX_ROTATION = 640; // degrees
    public final static double ROTATION_SPEED_DAMPENER = 0.1; // turn dampener

    public final static double PATH_FOLLOWING_TOLERANCE = 5; // inches

    public static final String LEFTFRONT_NAME = "leftFront";
    public static final String RIGHTFRONT_NAME = "rightFront";
    public static final String LEFTREAR_NAME = "leftRear";
    public static final String RIGHTREAR_NAME = "rightRear";

    public static final String LEFTFRONT_SERVO_NAME = "leftFrontServo";
    public static final String RIGHTFRONT_SERVO_NAME = "rightFrontServo";
    public static final String LEFTREAR_SERVO_NAME = "leftRearServo";
    public static final String RIGHTREAR_SERVO_NAME = "rightRearServo";

    public final static double LEFTFRONT_ZERO = 5;
    public final static double RIGHTFRONT_ZERO = 10;
    public final static double LEFTREAR_ZERO = 5;
    public final static double RIGHTREAR_ZERO = 5;

    public final static boolean LEFTFRONT_ISREVERSED = true;
    public final static boolean RIGHTFRONT_ISREVERSED = true;
    public final static boolean LEFTREAR_ISREVERSED = false;
    public final static boolean RIGHTREAR_ISREVERSED = false;

    // Kp, Ki, Kd, Kf
    public final static double[] SWERVE_TURN_PID = new double[] {0.004, 0.05, 0.0001};
    public final static double[] SWERVE_MOTOR_PID = new double[] {0, 0, 0};

    public final static boolean USING_ABSOLUTES = true; // also controls use of PID; if you're using absolutes, you need a PID to control the CR servo
    public final static boolean USING_DRIVE_PID = false;

    public final static double TURN_MAX_SPEED = 0.5;
    public final static double TELEOP_MAX_SPEED = 0.5;
    public final static double TELEOP_SLOW_SPEED = 0.2;
    public final static double AUTON_MAX_SPEED = 0.2;


    // Spline path default values
    public static final double DEFAULT_SEGMENT_LENGTH = 4; // in
    public static final int DEFAULT_NEXT_POINTS_RANGE = 3;
    public static final double DEFAULT_CATCH_RANGE = 6; // in
    public static final double AUTON_PATH_FINISHED_TOLERANCE = 3;

    public static final double SPLINE_CONFORMITY = 2; // higher = higher conformity to the spline. doesn't go negative but can go to decimals
}
