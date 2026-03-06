package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import static java.util.Map.entry;

import com.acmerobotics.dashboard.config.Config;

import java.util.Map;

@Config
public class DriveConstants {
    public final static double TRACK_WIDTH = 16.0; // inches
    public static double SERVO_MAX_ROTATION = 640; // degrees
    public final static double ROTATION_SPEED_DAMPENER = 0.1; // turn dampener

    public final static double PATH_FOLLOWING_TOLERANCE = 5; // inches

    public final static double LEFTFRONT_ZERO = 75.4; // absolute encoder values
    public final static double RIGHTFRONT_ZERO = 52.3;
    public final static double LEFTREAR_ZERO = 13.5;
    public final static double RIGHTREAR_ZERO = 63.7;

    public final static boolean LEFTFRONT_ISREVERSED = true;
    public final static boolean RIGHTFRONT_ISREVERSED = false;
    public final static boolean LEFTREAR_ISREVERSED = false;
    public final static boolean RIGHTREAR_ISREVERSED = false;

    // Kp, Ki, Kd, Kf
    public final static double[] LEFTFRONT_SWERVE_PID = new double[] {0.005, 0.12, 0.00015};
    public final static double[] RIGHTFRONT_SWERVE_PID = new double[] {0.005, 0.12, 0.00015};
    public final static double[] LEFTREAR_SWERVE_PID = new double[] {0.006, 0.11, 0.0001};
    public final static double[] RIGHTREAR_SWERVE_PID = new double[] {0.006, 0.11, 0.0001};
    public static final Map<String, double[]> SWERVE_PIDs = Map.ofEntries(
            entry("leftFront", LEFTFRONT_SWERVE_PID),
            entry("rightFront", RIGHTFRONT_SWERVE_PID),
            entry("leftRear", LEFTREAR_SWERVE_PID),
            entry("rightRear", RIGHTREAR_SWERVE_PID)
    );

    public static double SWERVE_TOLERANCE = 0.05; // smallest accepted power

    public final static double[] SWERVE_MOTOR_PID = new double[] {0, 0, 0};

    public final static boolean USING_ABSOLUTES = true; // also controls use of PID; if you're using absolutes, you need a PID to control the CR servo
    public final static boolean USING_DRIVE_PID = false;

    public final static double TURN_MAX_SPEED = 0.5;
    public final static double TELEOP_MAX_SPEED = 1500;
    public final static double TELEOP_SLOW_SPEED = 500;
    public final static double AUTON_MAX_SPEED = 0.2;


    // Spline path default values
    public static final double DEFAULT_SEGMENT_LENGTH = 4; // in
    public static final int DEFAULT_NEXT_POINTS_RANGE = 3;
    public static final double DEFAULT_CATCH_RANGE = 6; // in
    public static final double AUTON_PATH_FINISHED_TOLERANCE = 3;

    public static final double SPLINE_CONFORMITY = 2; // higher = higher conformity to the spline. doesn't go negative but can go to decimals
}
