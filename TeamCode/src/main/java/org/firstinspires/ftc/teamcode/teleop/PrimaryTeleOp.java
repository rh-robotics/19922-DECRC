package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Primary TeleOp")
public class PrimaryTeleOp extends OpMode {
    DriveTrain drive;
    private DcMotorEx launcher;
    private CRServo leftFeeder;
    private CRServo rightFeeder;

    double gamepadSpeed = 0;
    double gamepadDirection = 0;
    public static double LAUNCHER_VELOCITY = 0.5;
    boolean launcherOn = false;
    Gamepad previousGamepad = new Gamepad(), currentGamepad = new Gamepad();
    double[] initialGamepadValues; // left x, left y, right x, right y

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        previousGamepad.copy(gamepad1);
        currentGamepad.copy(gamepad1);

        // Initialize the drive train
        drive = new DriveTrain(hardwareMap, true);

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        leftFeeder = hardwareMap.get(CRServo.class, "leftFeeder");
        rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFeeder.setPower(0);
        rightFeeder.setPower(0);

        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        initialGamepadValues = new double[] {gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_stick_y};

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad.copy(currentGamepad);
        currentGamepad.copy(gamepad1);
        double[] currentJoystickValues = new double[] {};

        gamepadSpeed = Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        gamepadDirection = 180 - (Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) * 180 / Math.PI + 90);

        if (gamepad1.left_trigger >= 0.05 || gamepad1.right_trigger >= 0.05) { // turn
            drive.setModulesToTurn(gamepad1.right_trigger - gamepad1.left_trigger);
        } else if (Math.abs(gamepad1.right_stick_x) > 0.05) {
            drive.setModulesToTurn(gamepad1.right_stick_x);
        } else {
            if (!(gamepad1.left_stick_y == 0 && gamepad1.left_stick_x == 0)) { // drive
                drive.setModules(gamepadSpeed, gamepadDirection);
            } else {
                drive.setModules(gamepadSpeed);
            }
        }

        if (currentGamepad.triangle && !previousGamepad.triangle) { // launcher toggle
            launcherOn = !launcherOn;
        }

        if (launcherOn) {
            launcher.setPower(LAUNCHER_VELOCITY);
        } else {
            launcher.setPower(0);
        }

        if (gamepad1.circle){ // feeders
            leftFeeder.setPower(1);
            rightFeeder.setPower(1);
        } else {
            leftFeeder.setPower(0);
            rightFeeder.setPower(0);
        }

        telemetry.addLine("Driving: Left Joystick");
        telemetry.addLine("Turning: Right Joystick, Triggers");
        telemetry.addLine();
        telemetry.addLine("Triangle: Launch Motor Toggle");
        telemetry.addLine("Circle: Launch");
    }
}
