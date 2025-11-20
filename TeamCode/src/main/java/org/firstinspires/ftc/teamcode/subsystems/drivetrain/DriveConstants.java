package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

public class DriveConstants {
    public final static double TRACK_WIDTH = 16.0; // inches
    public final static double SERVO_MAX_ROTATION = 325; // degrees
    public final static double ROTATION_SPEED_CONSTANT = 20; // degrees/unit forward at max turn
    public final static double TELEOP_CHANGE_CONSTANT = 0.001; // used to approx derivatives
    public final static double AUTON_CHANGE_CONSTANT = 0.01; // increment between runs
    public final static double PATH_FOLLOWING_TOLERANCE = 5; // inches
}
