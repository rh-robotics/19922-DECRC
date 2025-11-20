package org.firstinspires.ftc.teamcode.teleop.demonstrationfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp OpMode for club fair, including strafe drive and launcher controls.
 */
@TeleOp(name = "Club Fair OpMode", group = "Events")
public class ClubFairOpMode extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    public DcMotorEx leftFront, rightFront, leftRear, rightRear;
    public DcMotorEx topLaunch, bottomLaunch;
    public Servo ballPusher;

    double pusherPosition = 0.5;
    double powerVal = 0.55;

    // init() Runs ONCE after the driver hits initialize
    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Do all init stuff
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");

        topLaunch = hardwareMap.get(DcMotorEx.class, "topLaunch");
        bottomLaunch = hardwareMap.get(DcMotorEx.class, "bottomLaunch");

        ballPusher = hardwareMap.get(Servo.class, "ballPusher");

        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);
        rightRear.setDirection(DcMotorEx.Direction.REVERSE);

        topLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        bottomLaunch.setDirection(DcMotorSimple.Direction.FORWARD);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    // loop() - Runs continuously while the OpMode is active
    @Override
    public void loop() {
        double leftFPower;
        double rightFPower;
        double leftBPower;
        double rightBPower;
        double drive = -gamepad1.left_stick_x * 0.8;
        double turn = gamepad1.left_stick_y * 0.6;
//        double strafe = -gamepad1.right_stick_x * 0.8;
        double strafe = 0;
        telemetry.addLine("Right Trigger: Launcher Motor");
        telemetry.addLine("Right Bumper: Intake");
        telemetry.addLine();
        telemetry.addData("Launch, Triangle", gamepad1.triangle);
        telemetry.addLine();

        // Calculate drive power
        if (drive != 0 || turn != 0) {
            leftFPower = Range.clip(drive + turn, -1.0, 1.0);
            rightFPower = Range.clip(drive - turn, -1.0, 1.0);
            leftBPower = Range.clip(drive + turn, -1.0, 1.0);
            rightBPower = Range.clip(drive - turn, -1.0, 1.0);
        } else if (strafe != 0) {
            // Strafing
            leftFPower = -strafe;
            rightFPower = strafe;
            leftBPower = strafe;
            rightBPower = -strafe;
        } else {
            leftFPower = 0;
            rightFPower = 0;
            leftBPower = 0;
            rightBPower = 0;
        }

        // Set power to values calculated above
        leftFront.setPower(leftFPower *0.75);
        leftRear.setPower(leftBPower *0.75);
        rightFront.setPower(rightFPower *0.75);
        rightRear.setPower(rightBPower *0.75);

        if (gamepad1.dpad_up) {
            powerVal += 0.001;
        } else if (gamepad1.dpad_down) {
            powerVal -= 0.001;
        }

        telemetry.addData("Power Value", powerVal);

        // Set launcher wheel powers. Currently consolidated onto one OpMode, later may be separated.
        if (gamepad1.right_trigger > 0.2) {
            topLaunch.setPower(powerVal);
            bottomLaunch.setPower(powerVal);

            telemetry.addData("Left/Right Launchers", powerVal);
        } else if (gamepad1.right_bumper) {
            topLaunch.setPower(-0.35);
            bottomLaunch.setPower(-0.35);

            telemetry.addData("Left/Right Launchers", -0.35);
        } else {
            topLaunch.setPower(0);
            bottomLaunch.setPower(0);

            telemetry.addData("Left/Right Launchers", 0);
        }

        if (gamepad1.triangle) {
            pusherPosition = 0.25;
        } else {
            pusherPosition = 0.5;
        }
        telemetry.addData("Ball pusher", pusherPosition);
        ballPusher.setPosition(pusherPosition);
    }
}