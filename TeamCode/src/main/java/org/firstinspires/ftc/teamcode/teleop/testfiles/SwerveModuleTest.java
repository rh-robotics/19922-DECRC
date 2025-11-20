package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.SwerveModule;

@TeleOp(name = "Swerve Module Test", group = "testing")
public class SwerveModuleTest extends OpMode {
    SwerveModule swerveModule;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the module
        swerveModule = new SwerveModule(hardwareMap, "testMotor", "testServo", 42);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        telemetry.addData("Speed", swerveModule.getVelocity());
        telemetry.addData("Direction", swerveModule.getDirection());
        telemetry.addLine();

        telemetry.addData("Left x", gamepad1.left_stick_x);
        telemetry.addData("Left y", gamepad1.left_stick_y);

        if (gamepad1.triangle) {
            swerveModule.setDirection(90);
        } else {
            swerveModule.setDirection(gamepad1.left_stick_x * 360);
        }

        swerveModule.setVelocity(gamepad1.left_stick_y);
    }
}
