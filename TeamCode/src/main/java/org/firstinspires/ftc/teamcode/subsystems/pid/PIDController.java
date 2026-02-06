package org.firstinspires.ftc.teamcode.subsystems.pid;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private final double Kp, Ki, Kd, Kf;
    private double integralSum = 0;
    private double lastError = 0;
    private ElapsedTime timer = new ElapsedTime();

    // credit to thermal equilibrium for this structure
    // use Kf for velocity PIDS but not position ones
    public PIDController(double Kp, double Ki, double Kd, double Kf) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.Kf = Kf;
    }

    public PIDController(double Kp, double Ki, double Kd) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.Kf = 0;
    }

    // reference = goal, state = current
    public double getPower(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        timer.reset();

        double output = (error * Kp) + (derivative * Kd) + (integralSum * Ki) + (reference * Kf);
        return output;
    }
}
