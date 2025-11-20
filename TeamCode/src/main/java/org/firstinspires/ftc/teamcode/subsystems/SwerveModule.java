package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SwerveModule {
    DcMotorEx motor;
    Servo servo;
    final double MAX_ROTATION;
    final double MAX_VELOCITY;
    boolean isReversed = false;
    final double ZERO_POSITION;
    public SwerveModule(String motorName, String servoName, HardwareMap hardwareMap, double zeroPosition, double maxRotation, double maxVelocity) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        MAX_ROTATION = maxRotation;
        MAX_VELOCITY = maxVelocity;
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // allows us to scale in terms of velocity rather than voltage
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // Set direction of servo swerve module
    public void setDirection(double degrees) {
        double positionChange = (servo.getPosition() - ((ZERO_POSITION + degrees)/MAX_ROTATION)) % 0.5;

        if (servo.getPosition() + positionChange > 1) {
            servo.setPosition(servo.getPosition() - positionChange);
        } else {
            servo.setPosition(servo.getPosition() + positionChange);
        }
    }

    // Set power of motor
    public void setVelocity(double velocity) {
        motor.setPower(velocity/MAX_VELOCITY);
    }

    public double getVelocity() {
        return motor.getPower() * MAX_VELOCITY;
    }

    public double getDirection() {
        return MAX_ROTATION * (servo.getPosition() - ZERO_POSITION);
    }
}
