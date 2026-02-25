package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name = "Servo Motor Test", group = "testing")
public class ServoMotorTest extends OpMode {
    Servo testServo;
    CRServo testCRServo;
    DcMotorEx testMotor;

    public static String motorName = "leftFront", CRServoName = "rightFrontServo", servoName = "leftRearServo";

    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    public static double servoPos = 0, CRServoSpeed = 0, motorSpeed = 0;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the servo
        testServo = hardwareMap.get(Servo.class, servoName);
        testCRServo = hardwareMap.get(CRServo.class, CRServoName);
        testMotor = hardwareMap.get(DcMotorEx.class, motorName);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        testServo.setPosition(servoPos);
        testCRServo.setPower(CRServoSpeed);
        testMotor.setPower(motorSpeed);
    }
}
