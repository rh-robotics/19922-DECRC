package org.firstinspires.ftc.teamcode.subsystems.drivetrain.paths;

public class TestPath implements DrivePath {
    public double[] getPosition(double t) {
        return new double[]{t, t, t}; // x, y, angle (degrees)
    }
}
