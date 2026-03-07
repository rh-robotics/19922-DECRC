package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@Config
@TeleOp(name = "Simple Swerve")
public class SimpleSwerve extends OpMode {
    DriveTrain drive;
    public static double direction = 0;
    public static double speed = 0;
    double gamepadSpeed = 0;
    double gamepadDirection = 0;
    public static boolean usingGamepad = false;
    public static boolean changeConstantly = false;
    public static boolean turn = false;
    public static double changeConstant = 1;
    public static boolean paused = false;

    public static int PIDIndex = 3;
    public static int index = 2;
    public static double[] PID = new double[] {0.0065, 0.07, 0.0001};
    public static boolean changePID = false;
    public static double MAX_SPEED = 0.5;

    ElapsedTime timer;
    private IMU imu;
    private double INIT_HEADING;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the module
        drive = new DriveTrain(hardwareMap, true);
        timer = new ElapsedTime();

        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        imu.initialize(parameters);

        INIT_HEADING = getHeading();

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        gamepadSpeed = MAX_SPEED * Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        gamepadDirection = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) * 180 / Math.PI + 90;
        gamepadDirection = (gamepadDirection + getHeading() - INIT_HEADING);

        if (!paused) {
            if (usingGamepad) {
                if (gamepad1.left_trigger >= 0.05 || gamepad1.right_trigger >= 0.05) { // turn
                    drive.setModulesToTurn((gamepad1.right_trigger - gamepad1.left_trigger)*MAX_SPEED);
                } else if (Math.abs(gamepad1.right_stick_x) > 0.05) {
                    drive.setModulesToTurn(gamepad1.right_stick_x * MAX_SPEED) ;
                } else {
                    if (Math.abs(gamepadSpeed) > 0.05) { // drive
                        drive.setModules(gamepadSpeed, gamepadDirection);
                    } else {
                        drive.setModules(0);
                    }
                }
            } else if (turn) {
                drive.setModulesToTurn(speed);
            } else {
                drive.setModules(speed, direction);
            }
        }

//        drive.setModules(speed, direction);

        telemetry.addData("Speed", gamepadSpeed);
        telemetry.addData("Direction", gamepadDirection);

        telemetry.addLine();

        telemetry.addData("Left Front Power", drive.modules[0].getCRPower());
        telemetry.addData("Right Front Power", drive.modules[1].getCRPower());
        telemetry.addData("Left Rear Power", drive.modules[2].getCRPower());
        telemetry.addData("Right Rear Power", drive.modules[3].getCRPower());

        telemetry.addData("Left Rear Velocity", drive.modules[index].getVelocity());
        telemetry.addData("Left Rear Set Pos", drive.modules[index].getSetPos());
        telemetry.addData("Left Rear Absolute Pos", drive.modules[index].getServoPosition());

        if (changeConstantly) {
            direction += (timer.milliseconds() / 100) * changeConstant;
            direction %= 360;
            timer.reset();
        }

        if (changePID) {
            changePID = false;
            drive.modules[PIDIndex].setSwervePIDController(PID[0], PID[1], PID[2]);
        }

        telemetry.addData("IMU", getHeading());
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
