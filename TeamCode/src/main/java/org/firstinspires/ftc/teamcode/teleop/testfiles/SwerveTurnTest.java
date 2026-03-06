package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.SwerveModule;

@Config
@TeleOp(name = "Swerve Turn Test", group = "testing")
public class SwerveTurnTest extends LinearOpMode {
    SwerveModule swerveModule;
    public static double direction = 0;
    public static double velocity = 0;

    public static String motorName = "testMotor";
    public static String servoName = "testServo";
    public static double zeroPosition = 256;

    @Override
    public void runOpMode() throws InterruptedException {
        swerveModule = new SwerveModule(hardwareMap, motorName, servoName, zeroPosition, false);

        waitForStart();
        while (opModeIsActive()) {
            swerveModule.setDirection(direction);
            swerveModule.setVelocity(velocity);

            telemetry.addData("Direction", swerveModule.getDirection());
            telemetry.addData("Velocity", swerveModule.getVelocity());
        }
    }
}