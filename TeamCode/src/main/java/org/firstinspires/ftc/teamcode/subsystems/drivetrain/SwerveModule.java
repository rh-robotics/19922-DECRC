package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.pid.PIDController;

public class SwerveModule {
    private DcMotorEx motor;
    public Servo servo;
    public CRServo crservo; // use one, this if absolutes are in use
    final double ZERO_POSITION; // in degrees, not a servo position
    private double setPos = 0;
    private boolean usingPID = false, usingAbsolutes = false;
    AnalogInput absoluteEncoder;
    PIDController WheelPIDControl, CRServoPIDControl;
    double crServoTargetPos;

    // CONSTRUCTORS

    // initialize with specific values for zeroPosition and isReversed assumiing use of PIDs
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, boolean isReversed, double[] WheelPIDValues) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        if (isReversed) {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        // set constants
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // set up PID
        this.usingPID = true;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        WheelPIDControl = new PIDController(WheelPIDValues[0], WheelPIDValues[1], WheelPIDValues[2], WheelPIDValues[3]);
    }

    // Constructor for using absolute, using this assumes use of an absolute
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, boolean isReversed, double[] WheelPIDValues, double[] CRServoPIDValues) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        crservo = hardwareMap.get(CRServo.class, servoName);

        if (isReversed) {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        // set constants
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // set up PID
        this.usingPID = true;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        WheelPIDControl = new PIDController(WheelPIDValues[0], WheelPIDValues[1], WheelPIDValues[2], WheelPIDValues[3]);

        // set up absolutes
//        this.usingAbsolutes = true;
        absoluteEncoder = hardwareMap.get(AnalogInput.class, "absoluteEncoder");
        crServoTargetPos = getServoPosition();

        CRServoPIDControl = new PIDController(CRServoPIDValues[0], CRServoPIDValues[1], CRServoPIDValues[2], CRServoPIDValues[3]);
    }

    // initialize with specific values for zeroPosition and isReversed assuming no use of PIDs
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName, double zeroPosition, boolean isReversed) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        if (isReversed) {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        // set constants
        ZERO_POSITION = zeroPosition; // converts to a fraction of rotation that can be set on the servo

        // set up PID
        this.usingPID = false;

        // assume not using PID, disadvantage is auto cap at ~80% with internal PIDs
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // initialize with default values for zeroPosition and isReversed
    public SwerveModule(HardwareMap hardwareMap, String motorName, String servoName) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        servo = hardwareMap.get(Servo.class, servoName);

        // set constants
        ZERO_POSITION = 0;

        this.usingPID = false;

        // assume not using PID, disadvantage is auto cap at ~80% with internal PIDs
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // SETTERS

    // Set direction of servo swerve module
    public double[] setDirection(double degrees) {
        setPos = degrees;

//        if (!usingAbsolutes) {
            double backwardChange = DegreesToServoPosition((degrees - servoPosToDegrees(servo.getPosition()) + 180 - ZERO_POSITION) % 180 - 180); // in degrees
            double forwardChange = DegreesToServoPosition(servoPosToDegrees(backwardChange) + 180);

            // figure out which one is closer
            if (Math.abs(backwardChange) < forwardChange) {
                // make sure it isn't out of range
                if (servo.getPosition() + backwardChange < 0) { // try to go the shorter way, unless its out of range
                    servo.setPosition(servo.getPosition() + forwardChange);
                } else {
                    servo.setPosition(servo.getPosition() + backwardChange);
                }
            } else {
                // make sure it isn't out of range
                if (servo.getPosition() + forwardChange > 1) {
                    servo.setPosition(servo.getPosition() + backwardChange);
                } else {
                    servo.setPosition(servo.getPosition() + forwardChange);
                }
            }

            // for testing and debugging
            return new double[]{forwardChange, backwardChange};
//        } else {
//            crservo.setPower(CRServoPIDControl.getPower(getServoPosition(), degrees));
//        }
//
//        return null;
    }

    // ONLY for testing purposes
    public void setServoPos(double position) {
        servo.setPosition(position);
    }

    // Set power of motor
    public void setVelocity(double speed) {
        if (usingPID) {
            if (isReversed()) {
                motor.setPower(WheelPIDControl.getPower(-speed, motor.getVelocity()));
            } else {
                motor.setPower(WheelPIDControl.getPower(speed, motor.getVelocity()));
            }
        } else {
            if (isReversed()) {
                motor.setPower(-speed);
            } else {
                motor.setPower(speed);
            }
        }
    }

    // GETTERS

    public double getVelocity() {
        return motor.getPower();
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
        return (DriveConstants.SERVO_MAX_ROTATION * servoPos) - ZERO_POSITION;
    }

    // determines if the servo is reversed (off by 180 degrees) by checking if the "set position" (position the servo was physically set to) matches the position it's in if we do the math backwards
    private boolean isReversed() {
        return Math.abs((servoPosToDegrees(getServoPosition()) % 360) - (setPos % 360)) < 90; // 90 is just a really big tolerance that's less than 180 lol
    }

    // gets servo's actual position
    private double getServoPosition() {
//        if (!usingAbsolutes) {
            return servo.getPosition();
//        }
//        return (absoluteEncoder.getVoltage() / 2.2) * 360.0; // if using absolutes
    }
}
