package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SwerveModule {
    DcMotorEx motor;
    Servo servo;
    final double MAX_ROTATION;
    boolean isReversed = false;
    final double ZERO_POSITION;

    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, double maxRotation) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        MAX_ROTATION = maxRotation;
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // allows us to scale in terms of velocity rather than voltage
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        MAX_ROTATION = DriveConstants.SERVO_MAX_ROTATION;
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // allows us to scale in terms of velocity rather than voltage
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        MAX_ROTATION = DriveConstants.SERVO_MAX_ROTATION;
        ZERO_POSITION = 0;

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
    public void setVelocity(double speed) {
        motor.setPower(speed);
    }

    public double getVelocity() {
        return motor.getPower();
    }

    public double getDirection() {
        return MAX_ROTATION * (servo.getPosition() - ZERO_POSITION);
    }
}
