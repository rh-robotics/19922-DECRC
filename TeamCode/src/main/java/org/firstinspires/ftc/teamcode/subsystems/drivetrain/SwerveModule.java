package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SwerveModule {
    private DcMotorEx motor;
    public Servo servo;
    final double ZERO_POSITION;
    private double setPos = 0;

    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, boolean isReversed) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        if (isReversed) {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        // set constants
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // allows us to scale in terms of velocity rather than voltage
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        ZERO_POSITION = 0;

        // allows us to scale in terms of velocity rather than voltage
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // Set direction of servo swerve module
    public double[] setDirection(double degrees) {
        setPos = degrees;

        double backwardChange = DegreesToServoPosition((degrees - servoPosToDegrees(servo.getPosition()) + 180 - ZERO_POSITION) % 180 - 180); // in degrees
        double forwardChange = DegreesToServoPosition(servoPosToDegrees(backwardChange) + 180);

        if (Math.abs(backwardChange) < forwardChange) {
//            if (Math.abs(backwardChange) < DriveConstants.SERVO_TOLERANCE) {
//                return new double[]{forwardChange, backwardChange};
//            }

            if (servo.getPosition() + backwardChange < 0) { // try to go the shorter way, unless its out of range
                servo.setPosition(servo.getPosition() + forwardChange);
            } else {
                servo.setPosition(servo.getPosition() + backwardChange);
            }
        } else {
//            if (forwardChange < DriveConstants.SERVO_TOLERANCE) {
//                return new double[]{forwardChange, backwardChange};
//            }

            if (servo.getPosition() + forwardChange > 1) {
                servo.setPosition(servo.getPosition() + backwardChange);
            } else {
                servo.setPosition(servo.getPosition() + forwardChange);
            }
        }

        return new double[]{forwardChange, backwardChange};
    }

    // ONLY for testing purposes
    public void setServoPos(double position) {
        servo.setPosition(position);
    }

    // Set power of motor
    public void setVelocity(double speed) {
        if (isReversed()) {
            motor.setPower(-speed);
        } else {
            motor.setPower(speed);
        }
    }

    public double getVelocity() {
        return motor.getPower();
    }

    public double getDirection() {
        return setPos;
    }

    public double DegreesToServoPosition(double degrees) {
        return (degrees + ZERO_POSITION)/DriveConstants.SERVO_MAX_ROTATION;
    }

    public double servoPosToDegrees(double servoPos) {
        return (DriveConstants.SERVO_MAX_ROTATION * servoPos) - ZERO_POSITION;
    }

    public boolean isReversed() {
        return Math.abs((servoPosToDegrees(servo.getPosition()) % 360) - (setPos % 360)) < 90; // 90 is just a really big tolerance that's less than 180 lol
    }

    public double getMotorEncoterValue() {
        return motor.getCurrentPosition();
    }
}
