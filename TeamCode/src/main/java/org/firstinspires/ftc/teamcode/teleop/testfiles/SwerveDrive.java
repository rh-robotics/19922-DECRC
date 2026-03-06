package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.subsystems.launcher.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.sortingdrum.SortingDrum;

import java.util.Arrays;

@Config
@TeleOp(name = "Swerve Drive")
public class SwerveDrive extends OpMode {
    DriveTrain drive;

    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    Gamepad previousGamepad2 = new Gamepad(), currentGamepad2 = new Gamepad();

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        previousGamepad1.copy(gamepad1);
        currentGamepad1.copy(gamepad1);

        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(gamepad2);

        drive = new DriveTrain(hardwareMap, true);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);

        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(gamepad2);

        drive.teleopUpdate(currentGamepad1, previousGamepad1, currentGamepad2, previousGamepad2);
    }
}
