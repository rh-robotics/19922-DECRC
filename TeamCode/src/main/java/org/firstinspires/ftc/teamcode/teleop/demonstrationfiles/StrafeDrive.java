package org.firstinspires.ftc.teamcode.teleop.demonstrationfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp OpMode for simply driving with strafing wheels
 */
@TeleOp(name = "Basic Strafe Drive", group = "Iterative OpMode")
public class StrafeDrive extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    DcMotorEx leftFront, rightFront, rightRear, leftRear;

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

        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);
        rightRear.setDirection(DcMotorEx.Direction.REVERSE);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    // init_loop() - Runs continuously until the driver hits play
    @Override
    public void init_loop() {
    }

    // Start() - Runs ONCE when the driver presses play
    @Override
    public void start() {
        time.reset();
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
        double strafe = -gamepad1.right_stick_x * 0.8;

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
        leftFront.setPower(leftFPower);
        leftRear.setPower(leftBPower);
        rightFront.setPower(rightFPower);
        rightRear.setPower(rightBPower);
    }
}