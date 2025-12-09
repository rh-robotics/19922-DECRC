package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.acmerobotics.dashboard.config.Config;

@Config
public class DriveConstants {
    public final static double TRACK_WIDTH = 16.0; // inches
    public static double SERVO_MAX_ROTATION = 640; // degrees
    public final static double ROTATION_SPEED_CONSTANT = 20; // degrees/unit forward at max turn
    public final static double TELEOP_CHANGE_CONSTANT = 0.001; // used to approx derivatives
    public final static double AUTON_CHANGE_CONSTANT = 0.01; // increment between runs
    public final static double PATH_FOLLOWING_TOLERANCE = 5; // inches
    public final static double SERVO_TOLERANCE = 0; // fraction of 1

    public final static double LEFTFRONT_ZERO = 5;
    public final static double RIGHTFRONT_ZERO = 5;
    public final static double LEFTREAR_ZERO = 32;
    public final static double RIGHTREAR_ZERO = 5;

    public final static boolean LEFTFRONT_ISREVERSED = true;
    public final static boolean RIGHTFRONT_ISREVERSED = false;
    public final static boolean LEFTREAR_ISREVERSED = true;
    public final static boolean RIGHTREAR_ISREVERSED = true;

    public final static double TURN_MAX_SPEED = 0.5;
}
