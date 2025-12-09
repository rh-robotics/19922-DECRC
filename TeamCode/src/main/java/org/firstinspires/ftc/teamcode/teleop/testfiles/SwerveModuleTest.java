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
        swerveModule = new SwerveModule(hardwareMap, "testMotor", "testServo", 195, false);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");

        swerveModule.setDirection(0);
    }

    @Override
    public void loop() {
        telemetry.addData("Speed", swerveModule.getVelocity());
        telemetry.addData("Direction", swerveModule.getDirection());
        telemetry.addLine();
        
        double[] positions = new double[2];

        if (gamepad1.triangle) {
            positions = swerveModule.setDirection(0);
        } else if (gamepad1.circle){
            positions = swerveModule.setDirection(45);
        } else if (gamepad1.cross) {
            positions = swerveModule.setDirection(180);
        } else if (gamepad1.square) {
            positions = swerveModule.setDirection(90);
        } else if (Math.abs(gamepad1.left_stick_x) > 0.05){
            positions = swerveModule.setDirection((gamepad1.left_stick_x + 1) * 360);
        }

        telemetry.addLine(String.valueOf(positions[0]));
        telemetry.addLine(String.valueOf(positions[1]));

        if (gamepad1.left_bumper) {
            swerveModule.setVelocity(0.3);
        } else {
            swerveModule.setVelocity(0);
        }

        telemetry.addLine(String.valueOf(swerveModule.isReversed()));
    }
}
