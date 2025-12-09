package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Simple Swerve")
public class SimpleSwerve extends OpMode {
    DriveTrain drive;
    public static double direction = 0;
    public static double speed = 0;
    double gamepadSpeed = 0;
    double gamepadDirection = 0;
    public static boolean usingGamepad = false;
    public static boolean paused = false;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the module
        drive = new DriveTrain(hardwareMap, true);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        gamepadSpeed = Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        gamepadDirection = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) * 180 / Math.PI + 90;

        if (!paused) {
            if (usingGamepad) {
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
            } else {
                drive.setModules(speed, direction);
            }
        }

        double[] encoderValues = drive.getMotorEncoderValues();
        telemetry.addData("Speed", gamepadSpeed);
        telemetry.addData("Direction", gamepadDirection);

        telemetry.addLine();

        telemetry.addData("Left Front", encoderValues[0]);
        telemetry.addData("Right Front", encoderValues[1]);
        telemetry.addData("Left Rear", encoderValues[2]);
        telemetry.addData("Right Rear", encoderValues[3]);

        telemetry.addLine();

        telemetry.addData("Right Front - Left Front", encoderValues[1] - encoderValues[0]);
        telemetry.addData("Left Rear - Left Front", encoderValues[2] - encoderValues[0]);
        telemetry.addData("Right Rear - Left Front", encoderValues[3] - encoderValues[0]);
    }
}
