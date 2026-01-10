//package org.firstinspires.ftc.teamcode.subsystems.pid.components;
//
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.util.Range;
//
//public class MotorPIDComponent {
//    private final double ticks_per_degree;
//    private final double power_clip_range;
//    private final double F;
//    private double target;
//    private final PIDController controller;
//    private final DcMotorEx motor;
//
//    public MotorPIDComponent(DcMotorEx motor, double power_clip_range, double ticks_per_rotation, double p, double i, double d, double f) {
//        this.motor = motor;
//        this.F = f;
//        this.power_clip_range = power_clip_range;
//        ticks_per_degree = ticks_per_rotation / 360.0;
//        target = 0;
//
//        controller = new PIDController(p, i, d);
//    }
//
//    public MotorPIDComponent(DcMotorEx motor, double ticks_per_rotation, double p, double i, double d, double f) {
//        this.motor = motor;
//        this.F = f;
//        this.power_clip_range = 1;
//        ticks_per_degree = ticks_per_rotation / 360.0;
//        target = 0;
//
//        controller = new PIDController(p, i, d);
//    }
//
//    public double getTicksPerDegree() {
//        return ticks_per_degree;
//    }
//
//    public double getTarget() {
//        return target;
//    }
//
//    public void setTarget(double newTarget) {
//        target = newTarget;
//    }
//
//    public void incrementTarget(double increment) {
//        target += increment;
//        moveUsingPID();
//    }
//
//    public void moveUsingPID() {
//        controller.reset();
//        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//        int motorPos = motor.getCurrentPosition();
//        double pid = controller.calculate(motorPos, target);
//        double ff = Math.cos(Math.toRadians(target / ticks_per_degree)) * F;
//        double power = Range.clip((pid + ff), -power_clip_range, power_clip_range);
//
//        motor.setPower(power);
//    }
//
//    public boolean closeEnough(int range) {
//        return (target - range <= motor.getCurrentPosition()) && (target + range >= motor.getCurrentPosition());
//    }
//}