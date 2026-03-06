package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SwerveModule {
    private DcMotorEx motor;
    public Servo servo;
    public CRServo crservo; // use one, this if absolutes are in use
    final double ZERO_POSITION; // in degrees, not a servo position
    private double setPos = 0;
    private final boolean usingAbsolutes = DriveConstants.USING_ABSOLUTES;
    AnalogInput absoluteEncoder;
    PIDController swervePIDController, drivePIDController;
    double swerveTargetPos;

    // CONSTRUCTORS
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, boolean isReversed) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);

        if (isReversed) {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (usingAbsolutes) { // implied use of PIDs if we're using absolutes, also determines use of CR vs servo
            crservo = hardwareMap.get(CRServo.class, servoName);
            double[] PIDConstants = DriveConstants.SWERVE_PIDs.get(motorName);
            assert PIDConstants != null;

            swervePIDController = new PIDController(PIDConstants[0], PIDConstants[1], PIDConstants[2]);
            absoluteEncoder = hardwareMap.get(AnalogInput.class, motorName + "Absolute");
        } else {
            servo = hardwareMap.get(Servo.class, servoName);
        }

        // set constants
        ZERO_POSITION = zeroPosition;

        swerveTargetPos = getServoPosition();
    }

    // initialize with default values for zeroPosition and isReversed
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);

        motor.setDirection(DcMotorSimple.Direction.FORWARD); // default to forward
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (usingAbsolutes) { // implied use of PIDs if we're using absolutes, also determines use of CR vs servo
            crservo = hardwareMap.get(CRServo.class, servoName);

            double[] PIDConstants = DriveConstants.SWERVE_PIDs.get(motorName);
            assert PIDConstants != null;

            swervePIDController = new PIDController(PIDConstants[0], PIDConstants[1], PIDConstants[2]);
            absoluteEncoder = hardwareMap.get(AnalogInput.class, "absoluteEncoder");
        } else {
            servo = hardwareMap.get(Servo.class, servoName);
        }

        // set constants
        ZERO_POSITION = 0; // default to 0

        swerveTargetPos = getServoPosition();
    }

    // SETTERS

    // Set direction of servo swerve module
    public void setDirection(double target) {
        target = -(Math.floorMod((int) target, 360) - 180); // reverse clockwise/counterclockwise
        setPos = target; // for checking for reversal, since the setPos isn't going to be the actual direction we're pointing at sometimes since being off by 180 is equivalent if you're going backwards

//        if (!usingAbsolutes) { // implied use of PIDs (and thus CR servos) if we're using absolutes
//            double backwardChange = DegreesToServoPosition((target - servoPosToDegrees(servo.getPosition()) + 180) % 180 - 180); // in degrees
//            double forwardChange = DegreesToServoPosition(servoPosToDegrees(backwardChange) + 180);
//
//            // figure out which one is closer
//            if (Math.abs(backwardChange) < forwardChange) {
//                // make sure it isn't out of range
//                if (servo.getPosition() + backwardChange < 0) { // try to go the shorter way, unless its out of range
//                    servo.setPosition(servo.getPosition() + forwardChange);
//                } else {
//                    servo.setPosition(servo.getPosition() + backwardChange);
//                }
//            } else {
//                // make sure it isn't out of range
//                if (servo.getPosition() + forwardChange > 1) {
//                    servo.setPosition(servo.getPosition() + backwardChange);
//                } else {
//                    servo.setPosition(servo.getPosition() + forwardChange);
//                }
//            }
//        } else {
        double servoPos = getServoPosition(); // 2.2 volts converts to 360 degrees

        double backwardChange = Math.floorMod((int) (target - servoPos + 180), 180) - 180;
        double forwardChange = backwardChange + 180;

        double smallestChange = forwardChange;
        if (Math.abs(backwardChange) < forwardChange) {
            smallestChange = backwardChange;
        }

        // if change is positive, target > pos
        double shiftedServoPos = 180 - (smallestChange / 2);
        double shiftedTargetPos = 180 + (smallestChange / 2);

        double power = swervePIDController.calculate(shiftedServoPos, shiftedTargetPos);

        if (Math.abs(power) > DriveConstants.SWERVE_TOLERANCE) {
            crservo.setPower(power);
        } else {
            crservo.setPower(0);
        }
//        }
    }

    // ONLY for testing purposes
    public void setServoPos(double position) {
        servo.setPosition(position);
    }

    public void setCRServoPower(double power) {
        crservo.setPower(power);
    }

    // Set power of motor
    public void setVelocity(double speed) {
        if (isReversed()) {
            motor.setPower(-speed); // TODO: Check if this works
        } else {
            motor.setPower(speed);
        }
    }

    // GETTERS

    public double getVelocity() {
        return motor.getVelocity();
    }
    public double getCRPower() {
        return crservo.getPower();
    }
    public void setSwervePIDController(double p, double i, double d) {
        swervePIDController.setPID(p, i, d);
    }

    // gets the degrees that it's set to, not the servo position. ONLY for use without absolutes
    public double getDirection() {
        return setPos;
    }

    public double getMotorEncoderValue() {
        return motor.getCurrentPosition();
    }

    // PRIVATE FUNCTIONS

    // converts degrees to a position that a servo can be set to. ONLY for use without absolutes
    private double DegreesToServoPosition(double degrees) {
        return (degrees + ZERO_POSITION)/DriveConstants.SERVO_MAX_ROTATION;
    }

    // converts a servo position to degrees. ONLY for use without absolutes
    private double servoPosToDegrees(double servoPos) {
        if (usingAbsolutes) {
            return servoPos;
        } else {
            return (DriveConstants.SERVO_MAX_ROTATION * servoPos) - ZERO_POSITION;
        }
    }

    // determines if the servo is reversed (off by 180 degrees) by checking if the "set position" (position the servo was physically set to) matches the position it's in if we do the math backwards
    private boolean isReversed() {
        return Math.abs((servoPosToDegrees(Math.floorMod((int) getServoPosition(), 360))) - Math.floorMod((int) setPos, 360)) < 90; // 90 is just a really big tolerance that's less than 180 lol
    }

    // gets servo's actual position
    public double getServoPosition() {
        if (!usingAbsolutes) {
            return servo.getPosition();
        }
        return ((absoluteEncoder.getVoltage() / 2.2) * 360.0) - ZERO_POSITION; // if using absolutes
    }

    public double getSetPos() {
        return setPos;
    }
}
