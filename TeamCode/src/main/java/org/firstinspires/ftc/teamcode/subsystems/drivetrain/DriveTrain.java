package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.pathing.Points;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.pathing.SplinePath;

public class DriveTrain implements Subsystem {
    private SwerveModule[] modules;
    private DcMotorEx leftFront, rightFront, rightRear, leftRear;
    private boolean inPathTolerance = true;
    private IMU imu;
    private double INIT_HEADING;
    public SplinePath activePath;

    // CONSTRUCTORS
    public DriveTrain(HardwareMap hardwareMap, boolean isSwerve, boolean isAuton, Points pathingPoints) {
        // allows handling of swerve and strafe drive
        if (isSwerve) {
            configureSwerve(hardwareMap);
        } else {
            configureStrafe(hardwareMap);
        }

        if (isAuton) {
            activePath = new SplinePath(pathingPoints, hardwareMap);
        }
    }

    // defaults to not auton
    public DriveTrain(HardwareMap hardwareMap, boolean isSwerve) {
        // allows handling of swerve and strafe drive
        if (isSwerve) {
            configureSwerve(hardwareMap);
        } else {
            configureStrafe(hardwareMap);
        }
    }

    // CONSTRUCTOR FUNCTIONS
    private void configureSwerve(HardwareMap hardwareMap) {
        modules = new SwerveModule[]{
                new SwerveModule(hardwareMap, "leftFront", "leftFrontServo", DriveConstants.LEFTFRONT_ZERO, DriveConstants.LEFTFRONT_ISREVERSED),
                new SwerveModule(hardwareMap, "rightFront", "rightFrontServo", DriveConstants.RIGHTFRONT_ZERO, DriveConstants.RIGHTFRONT_ISREVERSED),
                new SwerveModule(hardwareMap, "leftRear", "leftRearServo", DriveConstants.LEFTREAR_ZERO, DriveConstants.LEFTREAR_ISREVERSED),
                new SwerveModule(hardwareMap, "rightRear", "rightRearServo", DriveConstants.RIGHTREAR_ZERO, DriveConstants.RIGHTREAR_ISREVERSED)};

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
        INIT_HEADING = getHeading(AngleUnit.DEGREES);
    }

    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        if (currentGamepad1.left_trigger >= 0.05 || currentGamepad1.right_trigger >= 0.05) { // turn
            setModulesToTurn(currentGamepad1.right_trigger - currentGamepad1.left_trigger);
        } else if (Math.abs(currentGamepad1.right_stick_x) > 0.05) {
            setModulesToTurn(currentGamepad1.right_stick_x);
        } else {
            if (currentGamepad1.left_stick_y != 0 || currentGamepad1.left_stick_x != 0 || currentGamepad1.right_stick_x != 0) {
                setModulesWithGamepad(currentGamepad1.left_stick_x, currentGamepad1.left_stick_y, currentGamepad1.right_stick_x, currentGamepad1.right_bumper);
            } else {
                setModules(0);
            }
        }
    }

    // SETTERS

    public void setModulesWithGamepad(double gamepadX, double gamepadY, double turn, boolean isSlow) { // in ratio to each other, turning clockwise
        // basically just creating a ghost point based on the values to head towards
        double[] pathConstants = teleopPath(-gamepadX, gamepadY, turn); // x, y, angle pathing to be plugged into getPoints()
        
        double relHeading = getRelativeHeading(AngleUnit.RADIANS); // relative to start heading

        double[][] currentPositions = getPoints(0, 0, relHeading); // calculates positions of each wheel
        double[][] nextPositions = getPoints(pathConstants[0], pathConstants[1],
                relHeading + (pathConstants[2] * DriveConstants.ROTATION_SPEED_DAMPENER));

        if (isSlow) {
            setSpeeds(currentPositions, nextPositions, DriveConstants.TELEOP_SLOW_SPEED);
        } else {
            setSpeeds(currentPositions, nextPositions, DriveConstants.TELEOP_MAX_SPEED);
        }

        setDirections(currentPositions, nextPositions);
    }

    // returns true if fully finished and false otherwise
    public boolean followAutonPath(double gamepadX, double gamepadY, double turn) { // in ratio to each other, turning clockwise
        boolean isFinished = activePath.updateRobot();

        Pose2d robotPosition = activePath.robot.getPose();
        double[][] currentPositions = getPoints(robotPosition.position.x, robotPosition.position.y, robotPosition.heading.toDouble());

        double[] activePoint = activePath.getActivePoint();
        double[][] nextPositions = getPoints(activePoint[0], activePoint[1], activePoint[2]);

        setSpeeds(currentPositions, nextPositions, DriveConstants.AUTON_MAX_SPEED);
        setDirections(currentPositions, nextPositions);

        return isFinished && activePath.robot.inRange(activePoint, DriveConstants.AUTON_PATH_FINISHED_TOLERANCE);
    }

    public Points getCurrentPath() {
        return activePath.getPoints();
    }

    public void changePath(Points points) {
        activePath.changePath(points);
    }

    public void setModulesToTurn(double speed) {
        speed *= DriveConstants.TURN_MAX_SPEED;

        modules[0].setDirection(-45);
        modules[1].setDirection(45);
        modules[2].setDirection(45);
        modules[3].setDirection(-45);

        modules[0].setVelocity(speed);
        modules[1].setVelocity(-speed);
        modules[2].setVelocity(speed);
        modules[3].setVelocity(-speed);
    }

    public void setModules(double speed, double[] directions) {
        modules[0].setDirection(directions[0]);
        modules[1].setDirection(directions[1]);
        modules[2].setDirection(directions[2]);
        modules[3].setDirection(directions[3]);

        modules[0].setVelocity(speed);
        modules[1].setVelocity(speed);
        modules[2].setVelocity(speed);
        modules[3].setVelocity(speed);
    }

    public void setModules(double speed, double direction) {
        modules[0].setDirection(direction);
        modules[1].setDirection(direction);
        modules[2].setDirection(direction);
        modules[3].setDirection(direction);

        modules[0].setVelocity(speed);
        modules[1].setVelocity(speed);
        modules[2].setVelocity(speed);
        modules[3].setVelocity(speed);
    }

    public void setModules(double speed) {
        modules[0].setVelocity(speed);
        modules[1].setVelocity(speed);
        modules[2].setVelocity(speed);
        modules[3].setVelocity(speed);
    }

    private void setSpeeds(double[][] initialPositions, double[][] finalPositions, double speed) {
        double[] distances = new double[] {
                Math.sqrt(Math.pow(finalPositions[0][0] - initialPositions[0][0], 2) + Math.pow(finalPositions[0][1] - initialPositions[0][1], 2)),
                Math.sqrt(Math.pow(finalPositions[1][0] - initialPositions[1][0], 2) + Math.pow(finalPositions[1][1] - initialPositions[1][1], 2)),
                Math.sqrt(Math.pow(finalPositions[2][0] - initialPositions[2][0], 2) + Math.pow(finalPositions[2][1] - initialPositions[2][1], 2)),
                Math.sqrt(Math.pow(finalPositions[3][0] - initialPositions[3][0], 2) + Math.pow(finalPositions[3][1] - initialPositions[3][1], 2))};

        // for normalizing them so the max one is one bc idk what the range is on the original one
        double maxDistance = Math.max(Math.max(Math.max(distances[0], distances[1]), distances[2]), distances[3]);

        if (maxDistance > DriveConstants.PATH_FOLLOWING_TOLERANCE) {
            inPathTolerance = false;
        } else {
            inPathTolerance = true;
        }

        modules[0].setVelocity(distances[0]/maxDistance * speed);
        modules[1].setVelocity(distances[1]/maxDistance * speed);
        modules[2].setVelocity(distances[2]/maxDistance * speed);
        modules[3].setVelocity(distances[3]/maxDistance * speed);
    }

    private double[] setDirections(double[][] initPositions, double[][] finalPositions) {
        // adding 90 to account for the 90 degree zero point on the robot
        double lf = Math.atan2(finalPositions[0][1] - initPositions[0][1], finalPositions[0][0] - initPositions[0][0]) * 180.0/Math.PI + 90;
        double rf = Math.atan2(finalPositions[1][1] - initPositions[1][1], finalPositions[1][0] - initPositions[1][0]) * 180.0/Math.PI + 90;
        double lr = Math.atan2(finalPositions[2][1] - initPositions[2][1], finalPositions[2][0] - initPositions[2][0]) * 180.0/Math.PI + 90;
        double rr = Math.atan2(finalPositions[3][1] - initPositions[3][1], finalPositions[3][0] - initPositions[3][0]) * 180.0/Math.PI + 90;

        // adding the heading, since what we calculated before was like the "overhead view" angles of the wheels
        lf -= getRelativeHeading(AngleUnit.DEGREES);
        rf -= getRelativeHeading(AngleUnit.DEGREES);
        lr -= getRelativeHeading(AngleUnit.DEGREES);
        rr -= getRelativeHeading(AngleUnit.DEGREES);

        modules[0].setDirection(lf);
        modules[1].setDirection(rf);
        modules[2].setDirection(lr);
        modules[3].setDirection(rr);

        return new double[] {lf, rf, lr, rr};
    }

    // PATHING FUNCTIONS

    // convert forward/turn to path so we can approx the derivative
    private double[] teleopPath(double gamepadX, double gamepadY, double turn) {
        double x;
        double y;

        // solve for x and y scaled to teleop change constant
        if (gamepadY == 0) { // special case where y = 0
            if (gamepadX == 0) {
                x = 0;
            } else {
                x = (gamepadX / Math.abs(gamepadX));
            }
            y = 0;
        } else if (gamepadX == 0) { // special case where x = 0
            x = 0;
            y = (gamepadY / Math.abs(gamepadY));
        } else { // normal case
            x = (gamepadX/Math.abs(gamepadX));
            y = Math.abs(gamepadY/gamepadX) * (gamepadY / Math.abs(gamepadY));
        }

//        // since turn's impact is relative to the other values, scale it accordingly if driving
//        if (x > 0 || y > 0) { // TODO: decide if this is smth we want or if turn speed should increase as we go slower
//            turn *= Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
//        }

        return new double[] {x, y, turn};
    }

    // plugs info into path equation to account for angle
    public double[][] getPoints(double x, double y , double angle) {
        double halfWidth = DriveConstants.TRACK_WIDTH / 2;

        double[][] points = new double[4][2]; // lf, rf, lr, rr: x,y

        points[0][0] = -halfWidth * (Math.cos(angle) + Math.sin(angle))+x;
        points[0][1] = halfWidth * (Math.cos(angle) - Math.sin(angle))+y;

        points[1][0] = halfWidth * (Math.cos(angle) - Math.sin(angle))+x;
        points[1][1] = halfWidth * (Math.cos(angle) + Math.sin(angle))+y;

        points[2][0] = halfWidth * (-Math.cos(angle) + Math.sin(angle))+x;
        points[2][1] = -halfWidth * (Math.cos(angle) + Math.sin(angle))+y;

        points[3][0] = -halfWidth * (-Math.cos(angle) - Math.sin(angle))+x;
        points[3][1] = -halfWidth * (Math.cos(angle) - Math.sin(angle))+y;

        return points;
    }

    // GETTERS

    public double[] getMotorEncoderValues() {
        return new double[] {modules[0].getMotorEncoderValue(), modules[1].getMotorEncoderValue(),
                modules[2].getMotorEncoderValue(), modules[3].getMotorEncoderValue()};
    }

    private double getHeading(AngleUnit unit) {
        return imu.getRobotYawPitchRollAngles().getYaw(unit);
    }

    public double getRelativeHeading(AngleUnit unit) {
        return imu.getRobotYawPitchRollAngles().getYaw(unit) - INIT_HEADING;
    }

    // STRAFE FUNCTIONS
    private void configureStrafe(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
    }

    public void runStrafe(double driveY, double turnX, double strafeX) {
        double leftFPower, rightFPower, leftBPower, rightBPower;
        double drive = driveY * 0.8;
        double turn = turnX * 0.6;
        double strafe = strafeX * 0.8;

        // Calculate drive power
        if (drive != 0 || turn != 0) {
            leftFPower = Range.clip(drive + turn, -1.0, 1.0);
            rightFPower = Range.clip(drive - turn, -1.0, 1.0);
            leftBPower = Range.clip(drive + turn, -1.0, 1.0);
            rightBPower = Range.clip(drive - turn, -1.0, 1.0);
        } else if (strafe != 0) {
            // Strafing
            leftFPower = -strafe;
            rightFPower = strafe;
            leftBPower = strafe;
            rightBPower = -strafe;
        } else {
            leftFPower = 0;
            rightFPower = 0;
            leftBPower = 0;
            rightBPower = 0;
        }

        leftFront.setPower(leftFPower);
        leftRear.setPower(leftBPower);
        rightFront.setPower(rightFPower);
        rightRear.setPower(rightBPower);
    }
}