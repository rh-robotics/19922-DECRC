package org.firstinspires.ftc.teamcode.teleop;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

@TeleOp(name = "Launcher Assisted")
public class LauncherAssisted extends OpMode {

    /* =======================
     * TUNING VARIABLES
     * ======================= */

    // Close targets
    private final double closeTopTargetRpm = 1125;
    private final double closeBottomTargetRpm = 1125;

    // NEW: Medium targets
    private final double mediumTopTargetRpm = 1350;
    private final double mediumBottomTargetRpm = 1350;

    // Far targets
    private final double farTopTargetRpm = 1600;
    private final double farBottomTargetRpm = 1600;

    private final double rpmTolerance = 50;

    private final double feederFullSpeed = 1.0;
    private final double feederStopSpeed = 0.0;

    private final double kickerBackPosition = 0.0;
    private final double kickerForwardPosition = 0.75;

    // How long we hold the kicker forward (and also how long we ensure it's back)
    private final double servoKickback = 0.40; // seconds

    // Minimum time between starting shots (seconds)
    private final double minTimeBetweenShots = 0.35; // seconds

    private final int ballsPerLaunch = 3;

    private final double ticksPerRev = 28.0;

    /* =======================
     * HARDWARE
     * ======================= */

    private DcMotorEx topLauncher;
    private DcMotorEx bottomLauncher;
    private CRServo leftFeeder;
    private CRServo rightFeeder;
    private Servo kicker;

    /* =======================
     * STATE MACHINE
     * ======================= */

    private enum LaunchState {
        Idle,

        SpeedingClose,
        LaunchReadyClose,
        LaunchingClose,

        // NEW: Medium states
        SpeedingMedium,
        LaunchReadyMedium,
        LaunchingMedium,

        SpeedingFar,
        LaunchReadyFar,
        LaunchingFar
    }

    private LaunchState launchState = LaunchState.Idle;

    /* =======================
     * LAUNCH SEQUENCE CONTROL
     * ======================= */

    private int ballsShot = 0;

    private enum KickState {
        WaitingForRecovery,
        KickingForward,
        ReturningBack
    }

    private KickState kickState = KickState.WaitingForRecovery;

    // Reuse your servoTimer, plus add a separate timer for min time between shots
    private final ElapsedTime servoTimer = new ElapsedTime();
    private final ElapsedTime shotTimer = new ElapsedTime();

    /* =======================
     * GAMEPAD EDGE DETECT
     * ======================= */

    private final Gamepad previousGamepad1 = new Gamepad();
    private final Gamepad currentGamepad1  = new Gamepad();

    private DriveTrain drive;
    private IMU imu;
    private double RELATIVE_HEADING = 0; // TODO: set this to match the field
    double gamepadSpeed = 0, gamepadDirection = 0;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        topLauncher = hardwareMap.get(DcMotorEx.class, "topLaunch");
        bottomLauncher = hardwareMap.get(DcMotorEx.class, "bottomLaunch");

        leftFeeder  = hardwareMap.get(CRServo.class, "leftFeeder");
        rightFeeder = hardwareMap.get(CRServo.class, "rightFeeder");

        kicker = hardwareMap.get(Servo.class, "kicker");

        topLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        topLauncher.setZeroPowerBehavior(BRAKE);
        bottomLauncher.setZeroPowerBehavior(BRAKE);

        topLauncher.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFeeder.setDirection(CRServo.Direction.REVERSE);

        leftFeeder.setPower(feederStopSpeed);
        rightFeeder.setPower(feederStopSpeed);
        kicker.setPosition(kickerBackPosition);

        // Initialize the drive
        drive = new DriveTrain(hardwareMap, true);
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        // use this line to set relative heading to whatever it is upon init
        // don't use this line if you're using manual set
//        RELATIVE_HEADING = getHeading();

        currentGamepad1.copy(gamepad1);
        previousGamepad1.copy(gamepad1);

        launchState = LaunchState.Idle;
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);

        /* =======================
         * GLOBAL OVERRIDE
         * ======================= */

        if (pressed(currentGamepad1.right_bumper, previousGamepad1.right_bumper)) {
            setIdle();
        }

        /* =======================
         * MODE REQUESTS
         * ======================= */

        // Close: X (Cross)
        if (pressed(currentGamepad1.cross, previousGamepad1.cross)) {
            launchState = LaunchState.SpeedingClose;
        }

        // NEW: Medium: Triangle
        if (pressed(currentGamepad1.triangle, previousGamepad1.triangle)) {
            launchState = LaunchState.SpeedingMedium;
        }

        // Far: Square
        if (pressed(currentGamepad1.square, previousGamepad1.square)) {
            launchState = LaunchState.SpeedingFar;
        }

        // Shoot: Circle (only from LaunchReady states)
        if (pressed(currentGamepad1.circle, previousGamepad1.circle)) {
            if (launchState == LaunchState.LaunchReadyClose) {
                beginLaunching();
                launchState = LaunchState.LaunchingClose;
            } else if (launchState == LaunchState.LaunchReadyMedium) {
                beginLaunching();
                launchState = LaunchState.LaunchingMedium;
            } else if (launchState == LaunchState.LaunchReadyFar) {
                beginLaunching();
                launchState = LaunchState.LaunchingFar;
            }
        }

        /* =======================
         * STATE MACHINE OUTPUTS
         * ======================= */

        switch (launchState) {

            case Idle:
                setIdleOutputs();
                break;

            case SpeedingClose:
                runFlywheels(closeTopTargetRpm, closeBottomTargetRpm);
                if (flywheelsAtSpeed(closeTopTargetRpm, closeBottomTargetRpm)) {
                    launchState = LaunchState.LaunchReadyClose;
                }
                break;

            case LaunchReadyClose:
                runFlywheels(closeTopTargetRpm, closeBottomTargetRpm);
                runFeeders(true);
                break;

            case LaunchingClose:
                runLaunching(closeTopTargetRpm, closeBottomTargetRpm);
                break;

            case SpeedingMedium:
                runFlywheels(mediumTopTargetRpm, mediumBottomTargetRpm);
                if (flywheelsAtSpeed(mediumTopTargetRpm, mediumBottomTargetRpm)) {
                    launchState = LaunchState.LaunchReadyMedium;
                }
                break;

            case LaunchReadyMedium:
                runFlywheels(mediumTopTargetRpm, mediumBottomTargetRpm);
                runFeeders(true);
                break;

            case LaunchingMedium:
                runLaunching(mediumTopTargetRpm, mediumBottomTargetRpm);
                break;

            case SpeedingFar:
                runFlywheels(farTopTargetRpm, farBottomTargetRpm);
                if (flywheelsAtSpeed(farTopTargetRpm, farBottomTargetRpm)) {
                    launchState = LaunchState.LaunchReadyFar;
                }
                break;

            case LaunchReadyFar:
                runFlywheels(farTopTargetRpm, farBottomTargetRpm);
                runFeeders(true);
                break;

            case LaunchingFar:
                runLaunching(farTopTargetRpm, farBottomTargetRpm);
                break;
        }

        // handle drive
        gamepadSpeed = Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        gamepadDirection = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) * 180 / Math.PI;
        gamepadDirection = -(gamepadDirection - getHeading() + RELATIVE_HEADING);

        if (gamepad1.left_trigger >= 0.05 || gamepad1.right_trigger >= 0.05) { // turn
            drive.setModulesToTurn(gamepad1.right_trigger - gamepad1.left_trigger);
        } else if (Math.abs(gamepad1.right_stick_x) > 0.05) {
            drive.setModulesToTurn(gamepad1.right_stick_x) ;
        } else {
            if (!(gamepad1.left_stick_y == 0 && gamepad1.left_stick_x == 0)) { // drive
                drive.setModules(gamepadSpeed, gamepadDirection);
            } else {
                drive.setModules(gamepadSpeed);
            }
        }


        /* =======================
         * TELEMETRY
         * ======================= */

        telemetry.addData("State", launchState);
        telemetry.addData("Top RPM", getMotorRpm(topLauncher));
        telemetry.addData("Bottom RPM", getMotorRpm(bottomLauncher));
        telemetry.addData("Balls Shot", ballsShot);
        telemetry.addLine();
        telemetry.addLine("Controls:");
        telemetry.addLine("  X (Cross): Speed up Close");
        telemetry.addLine("  Triangle: Speed up Medium");
        telemetry.addLine("  Square: Speed up Far");
        telemetry.addLine("  Circle: Shoot (from LaunchReady)");
        telemetry.addLine("  Right Bumper: Idle/Stop All");
        telemetry.update();
    }

    /* =======================
     * HELPERS
     * ======================= */

    private void setIdle() {
        launchState = LaunchState.Idle;
        ballsShot = 0;
        kickState = KickState.WaitingForRecovery;
    }

    private void setIdleOutputs() {
        topLauncher.setVelocity(0);
        bottomLauncher.setVelocity(0);
        runFeeders(false);
        kicker.setPosition(kickerBackPosition);
    }

    // separate top/bottom targets
    private void runFlywheels(double topTargetRpm, double bottomTargetRpm) {
        double topTicksPerSecond = topTargetRpm * ticksPerRev / 60.0;
        double bottomTicksPerSecond = bottomTargetRpm * ticksPerRev / 60.0;

        topLauncher.setVelocity(-topTicksPerSecond);
        bottomLauncher.setVelocity(-bottomTicksPerSecond);
    }

    private void runFeeders(boolean on) {
        double power = on ? feederFullSpeed : feederStopSpeed;
        leftFeeder.setPower(power);
        rightFeeder.setPower(power);
    }

    private void beginLaunching() {
        ballsShot = 0;
        kickState = KickState.WaitingForRecovery;
        servoTimer.reset();
        shotTimer.reset();
    }

    private void runLaunching(double topTargetRpm, double bottomTargetRpm) {

        runFlywheels(topTargetRpm, bottomTargetRpm);
        runFeeders(true);

        if (ballsShot >= ballsPerLaunch) {
            setIdle();
            return;
        }

        switch (kickState) {

            case WaitingForRecovery:
                kicker.setPosition(kickerBackPosition);

                if (flywheelsAtSpeed(topTargetRpm, bottomTargetRpm) &&
                        shotTimer.seconds() >= minTimeBetweenShots) {
                    kickState = KickState.KickingForward;
                    servoTimer.reset();
                }
                break;

            case KickingForward:
                kicker.setPosition(kickerForwardPosition);

                if (servoTimer.seconds() >= servoKickback) {
                    kickState = KickState.ReturningBack;
                    servoTimer.reset();
                }
                break;

            case ReturningBack:
                kicker.setPosition(kickerBackPosition);

                if (servoTimer.seconds() >= servoKickback) {
                    ballsShot++;
                    shotTimer.reset();
                    kickState = KickState.WaitingForRecovery;
                }
                break;
        }
    }

    private boolean flywheelsAtSpeed(double topTargetRpm, double bottomTargetRpm) {
        return Math.abs(getMotorRpm(topLauncher) - topTargetRpm) <= rpmTolerance &&
                Math.abs(getMotorRpm(bottomLauncher) - bottomTargetRpm) <= rpmTolerance;
    }

    private double getMotorRpm(DcMotorEx motor) {
        return Math.abs(motor.getVelocity()) * 60.0 / ticksPerRev;
    }

    private boolean pressed(boolean now, boolean before) {
        return now && !before;
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}