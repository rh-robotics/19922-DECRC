package org.firstinspires.ftc.teamcode.teleop.msdrivetests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "MS Forward Drive", group = "MS Drive Tests")
public class ForwardDrive extends OpMode {
    private final ElapsedTime time = new ElapsedTime();
    public DcMotorEx leftFront, rightFront, leftRear, rightRear;

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

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    // loop() - Runs continuously while the OpMode is active
    @Override
    public void loop() {
        double drive = -gamepad1.left_stick_y * 0.8;
        double turn = gamepad1.left_stick_x * 0.6;
        double strafe = -gamepad1.right_stick_x * 0.8;

        // Set power to values calculated above
        leftFront.setPower(drive);
        leftRear.setPower(drive);
        rightFront.setPower(drive);
        rightRear.setPower(drive);
    }
}