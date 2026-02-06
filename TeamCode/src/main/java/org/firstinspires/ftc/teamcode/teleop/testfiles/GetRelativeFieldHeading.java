package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Get Relative Field Heading")
public class GetRelativeFieldHeading extends OpMode {
    DriveTrain drive;
    public static double direction = 0;
    double gamepadSpeed = 0;
    double gamepadDirection = 0;
    IMU imu;
    public static double RELATIVE_HEADING;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the module
        drive = new DriveTrain(hardwareMap, true, false);
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        gamepadSpeed = Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        gamepadDirection = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) * 180 / Math.PI;
        gamepadDirection = -(gamepadDirection - getHeading() + RELATIVE_HEADING);

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

        // upon pressing a on gamepad 1, relative heading will be set to the current heading.
        if (gamepad1.a) {
            RELATIVE_HEADING = getHeading();
        }

        telemetry.addLine("Press A to set current heading to relative heading.");
        telemetry.addLine();

        telemetry.addLine("Transfer the relative heading value to the LauncherAssisted file.");
        telemetry.addLine("The line to update is flagged in LauncherAssisted.java with a \"TODO\".");
        telemetry.addLine("Good luck! You guys got this. You can text me if you have questions :)");


        // print current relative heading
        telemetry.addData("Relative Heading", RELATIVE_HEADING);
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
