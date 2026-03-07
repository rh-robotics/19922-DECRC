package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.subsystems.launcher.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.sortingdrum.SortingDrum;

@Config
@TeleOp(name = "Primary TeleOp")
public class PrimaryTeleOp extends OpMode {
    DriveTrain drive;
    SortingDrum sortingDrum;
    Intake intake;
    Launcher launcher;

    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    Gamepad previousGamepad2 = new Gamepad(), currentGamepad2 = new Gamepad();

    //John Stuff
    Robot robot;

    @Override
    public void init() {
        // John ================================
        robot = new Robot(this, false);
        robot.initGamepads();

        //======================================
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        previousGamepad1.copy(gamepad1);
        currentGamepad1.copy(gamepad1);

        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(gamepad2);

        drive = new DriveTrain(hardwareMap, true);
        intake = new Intake(hardwareMap, false);
        sortingDrum = new SortingDrum(hardwareMap);
        launcher = new Launcher(hardwareMap);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Driver 1 Controls:");
        telemetry.addLine("***********************");
        telemetry.addLine();
        telemetry.addLine("Drive Controls:");
        telemetry.addLine("Left Joystick: Drive Direction");
        telemetry.addLine("Right Joystick: Drive Turning");
        telemetry.addLine("Left/Right Triggers: Drive Turning");
        telemetry.addLine("Right Bumper: Slow Drive");
        telemetry.addLine();
        telemetry.addLine("Shooting Controls:");
        telemetry.addLine("Circle: Launcher On");
        telemetry.addLine("Cross: Launcher Shoot");
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine("Driver 2 Controls:");
        telemetry.addLine("***********************");
        telemetry.addLine();
        telemetry.addLine("Mosaic Index Controls:");
        telemetry.addLine("Circle: Reset Index to 0");
        telemetry.addLine("Square: Increase Index by 1");
        telemetry.addLine("Triangle: Decrease Index by 1");
        telemetry.addLine();
    }

    @Override
    public void loop() {
        robot.updateSubsystems();
    }
}
