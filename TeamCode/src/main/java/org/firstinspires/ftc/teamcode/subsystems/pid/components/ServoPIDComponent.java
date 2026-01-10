//package org.firstinspires.ftc.teamcode.subsystems.pid.components;
//
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//
//public class ServoPIDComponent {
//    private final double ticks_per_degree;
//    private final double F;
//    private double target;
//    private final PIDController controller;
//    private final CRServo servo;
//    private AnalogInput analogInput;
//
//    public ServoPIDComponent(CRServo servo, AnalogInput analogInput, double ticks_per_rotation, double p, double i, double d, double f) {
//        this.servo = servo;
//        this.F = f;
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
//        double servoPos = analogInput.getVoltage() / 3.3 * 360;
//        double pid = controller.calculate(servoPos, target);
//        double ff = Math.cos(Math.toRadians(target / ticks_per_degree)) * F;
//        double power = pid + ff;
//
//        servo.setPower(power);
//    }
//
//    public boolean closeEnough(int range) {
//        return (target - range <= analogInput.getVoltage()) && (target + range >= (analogInput.getVoltage() / 3.3 * 360));
//    }
//}