package org.firstinspires.ftc.teamcode.subsystems.drivetrain.paths;

public interface DrivePath {
    public double[] getPosition(double t);
    public double getEnd();
}
