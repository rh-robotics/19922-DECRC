package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Swerve Drive")
public class SwerveDrive extends OpMode {
    DriveTrain drive;
    public static double direction = 0;
    public static double speed = 0;
    double gamepadSpeed = 0;
    double gamepadDirection = 0;
    public static boolean usingGamepad = true;
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
                // to keep the wheels from turning without input
                if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0 || gamepad1.right_stick_x != 0) {
                    drive.setModulesWithGamepad(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x/10);
                } else {
                    drive.setModules(0);
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
