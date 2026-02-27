package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.SwerveModule;

@Config
@TeleOp(name = "Swerve Module Absolute Encoder Test", group = "testing")
public class SwerveAbsoluteTurnTest extends OpMode {
    SwerveModule swerveModule;
    public static String motorName = "leftFront";
    public static String servoName = "leftFrontServo";
    public static double zeroPosition = 0;
    public static double servoDirection = 0;
    public static double motorVelocity = 0;
    public static double[] motorPID = {0, 0, 0, 0};
    public static double[] servoPID = {0, 0, 0};

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the module
        swerveModule = new SwerveModule(hardwareMap, motorName, servoName, zeroPosition, false);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");

        swerveModule.setDirection(0);
    }

    @Override
    public void loop() {
        swerveModule.setDirection(servoDirection);
        swerveModule.setVelocity(motorVelocity);

        telemetry.addData("Absolute Position", swerveModule.getServoPosition());
    }
}
