package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.SwerveModule;

@Config
@TeleOp(name = "Swerve Turn Test", group = "testing")
public class SwerveTurnTest extends LinearOpMode {
    SwerveModule swerveModule;
    public static boolean testing = false;
    public static double direction = 0;
    public static double position = 0;

    public static String motorName = "testMotor";
    public static String servoName = "testServo";
    public static double zeroPosition = 195;

    @Override
    public void runOpMode() throws InterruptedException {
        swerveModule = new SwerveModule(hardwareMap, motorName, servoName, zeroPosition, false);

        waitForStart();
        while (opModeIsActive()) {
            if (testing) {
                swerveModule.setDirection(direction);
            } else {
                swerveModule.servo.setPosition(position);
            }

            telemetry.addData("Direction", swerveModule.getDirection());
            telemetry.addData("Reversed", swerveModule.isReversed());
            telemetry.addData("Position", swerveModule.servo.getPosition());
        }
    }
}