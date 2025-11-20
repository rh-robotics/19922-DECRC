package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Independent Motor Speed Test", group = "testing")
public class IndependentMotorSpeedTest extends OpMode {
    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    double topMotorPower = 0;
    double bottomMotorPower = 0;
    final double[] LAUNCH_POSITIONS = {0.5, 0.35};

    public DcMotorEx leftFront, rightFront, leftRear, rightRear;
    public DcMotorEx topLaunch, bottomLaunch;
    public Servo ballPusher;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

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

        currentGamepad1.copy(gamepad1);
        previousGamepad1.copy(gamepad1);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);

        telemetry.addLine("Servo and Motor Test");
        telemetry.addLine();
        telemetry.addData("D-Pad Up/Down", "Top Motor Power");
        telemetry.addData("Triangle/Cross", "Bottom Motor Power");
        telemetry.addData("Right Bumpers", "Reset");
        telemetry.addData("Right Trigger", "Launch");
        telemetry.addLine();
        telemetry.addData("Top Motor power", topMotorPower);
        telemetry.addData("Bottom Motor power", bottomMotorPower);

        if (currentGamepad1.dpad_up && !previousGamepad1.dpad_up) {
            topMotorPower += 0.02;
        } else if (currentGamepad1.dpad_down && !previousGamepad1.dpad_down) {
            topMotorPower -= 0.02;
        }

        if (currentGamepad1.cross && !previousGamepad1.cross) {
            bottomMotorPower -= 0.02;
        } else if (currentGamepad1.triangle && !previousGamepad1.triangle) {
            bottomMotorPower += 0.02;
        }

        if (currentGamepad1.right_bumper && !previousGamepad1.right_bumper) {
            topMotorPower = 0;
            bottomMotorPower = 0;
        }

        if (currentGamepad1.right_trigger > 0.2) {
            ballPusher.setPosition(LAUNCH_POSITIONS[1]);
        } else {
            ballPusher.setPosition(LAUNCH_POSITIONS[0]);
        }

        if (currentGamepad1.circle && !previousGamepad1.circle) {
            bottomMotorPower += 0.05;
            topMotorPower += 0.05;
        } else if (currentGamepad1.square && !previousGamepad1.square) {
            bottomMotorPower -= 0.05;
            topMotorPower -= 0.05;
        }

        topLaunch.setPower(topMotorPower);
        bottomLaunch.setPower(bottomMotorPower);

        double leftFPower, leftBPower, rightFPower, rightBPower;

        double drive = -gamepad1.left_stick_x * 0.8;
        double turn = gamepad1.left_stick_y * 0.6;

        if (drive != 0 || turn != 0) {
            leftFPower = Range.clip(drive + turn, -1.0, 1.0);
            rightFPower = Range.clip(drive - turn, -1.0, 1.0);
            leftBPower = Range.clip(drive + turn, -1.0, 1.0);
            rightBPower = Range.clip(drive - turn, -1.0, 1.0);
        } else {
            leftFPower = 0;
            rightFPower = 0;
            leftBPower = 0;
            rightBPower = 0;
        }

        leftFront.setPower(leftFPower);
        leftRear.setPower(leftBPower);
        rightFront.setPower(rightFPower);
        rightRear.setPower(rightBPower);
    }
}
