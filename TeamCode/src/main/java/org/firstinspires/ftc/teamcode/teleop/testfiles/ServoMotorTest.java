package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name = "Servo Motor Test", group = "testing")
public class ServoMotorTest extends OpMode {
    CRServo testCRServo;
    DcMotorEx testMotor;

    public static String motorName = "leftFront", CRServoName = "rightFrontServo", servoName = "leftRearServo";

    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    public static double servoPos = 0, CRServoSpeed = 0, motorSpeed = 0;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the servo
        testCRServo = hardwareMap.get(CRServo.class, CRServoName);
        testMotor = hardwareMap.get(DcMotorEx.class, motorName);

        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        testCRServo.setPower(CRServoSpeed);
        testMotor.setVelocity(motorSpeed);

        telemetry.addData("Motor Speed", testMotor.getVelocity());
    }
}
