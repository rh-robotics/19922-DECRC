package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Swerve Drive Test")
public class SwerveDriveTest extends OpMode {
    DriveTrain driveTrain;
    public static double speed = 0;
    public static double direction = 0;

    @Override
    public void init() {
        driveTrain = new DriveTrain(hardwareMap, true);
    }

    @Override
    public void loop() {
//        driveTrain.setModules(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        driveTrain.setModules(speed, direction);
    }
}
