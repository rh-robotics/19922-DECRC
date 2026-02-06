package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class DriveTrain {
    private SwerveModule[] modules;
    private DcMotorEx leftFront, rightFront, rightRear, leftRear;
    private double t; // ticker, "time", paths use this as their parametric variable to keep track of where they are on the path
    private boolean inPathTolerance = true;
    private IMU imu;
    private double INIT_HEADING;

    // CONSTRUCTORS
    public DriveTrain(HardwareMap hardwareMap, boolean isSwerve, boolean usingPIDs) {
        // allows handling of swerve and strafe drive
        if (isSwerve) {
            configureSwerve(hardwareMap, usingPIDs);
        } else {
            configureStrafe(hardwareMap);
        }

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
    }

    // CONSTRUCTOR FUNCTIONS
    private void configureSwerve(HardwareMap hardwareMap, boolean usingPIDs) {
        if (usingPIDs) {
            modules = new SwerveModule[]{
                    new SwerveModule(hardwareMap, "leftFront", "leftFrontServo", DriveConstants.LEFTFRONT_ZERO, DriveConstants.LEFTFRONT_ISREVERSED, DriveConstants.LEFT_FRONT_PID),
                    new SwerveModule(hardwareMap, "rightFront", "rightFrontServo", DriveConstants.RIGHTFRONT_ZERO, DriveConstants.RIGHTFRONT_ISREVERSED, DriveConstants.RIGHT_FRONT_PID),
                    new SwerveModule(hardwareMap, "leftRear", "leftRearServo", DriveConstants.LEFTREAR_ZERO, DriveConstants.LEFTREAR_ISREVERSED, DriveConstants.LEFT_REAR_PID),
                    new SwerveModule(hardwareMap, "rightRear", "rightRearServo", DriveConstants.RIGHTREAR_ZERO, DriveConstants.RIGHTREAR_ISREVERSED, DriveConstants.RIGHT_REAR_PID)};
        } else {
            modules = new SwerveModule[]{
                    new SwerveModule(hardwareMap, "leftFront", "leftFrontServo", DriveConstants.LEFTFRONT_ZERO, DriveConstants.LEFTFRONT_ISREVERSED),
                    new SwerveModule(hardwareMap, "rightFront", "rightFrontServo", DriveConstants.RIGHTFRONT_ZERO, DriveConstants.RIGHTFRONT_ISREVERSED),
                    new SwerveModule(hardwareMap, "leftRear", "leftRearServo", DriveConstants.LEFTREAR_ZERO, DriveConstants.LEFTREAR_ISREVERSED),
                    new SwerveModule(hardwareMap, "rightRear", "rightRearServo", DriveConstants.RIGHTREAR_ZERO, DriveConstants.RIGHTREAR_ISREVERSED)};
        }

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
        INIT_HEADING = getHeading(AngleUnit.DEGREES);
    }

    // SETTERS

    public double[] setModulesWithGamepad(double gamepadX, double gamepadY, double turn) { // in ratio to each other, turning clockwise
        double[] pathConstants = teleopPath(-gamepadX, gamepadY, turn); // x, y, angle pathing to be plugged into getPoints()

        // paths:
        // x = pathConstants[0] * t
        // y = pathConstants[1] * t
        // angle = pathConstants[2] * t * dampener

        double relHeading = getRelativeHeading(AngleUnit.RADIANS);

        double[][] currentPositions = getPoints(0, 0, relHeading);
        double[][] nextPositions = getPoints(pathConstants[0] * DriveConstants.TELEOP_CHANGE_CONSTANT, pathConstants[1] * DriveConstants.TELEOP_CHANGE_CONSTANT,
                relHeading + (pathConstants[2] * DriveConstants.TELEOP_CHANGE_CONSTANT * DriveConstants.ROTATION_SPEED_DAMPENER));

        setSpeeds(currentPositions, nextPositions, 0.2);
        setDirections(currentPositions, nextPositions);
        return new double[] {pathConstants[0] * DriveConstants.TELEOP_CHANGE_CONSTANT, pathConstants[1] * DriveConstants.TELEOP_CHANGE_CONSTANT,
                (pathConstants[2])};
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

        // since turn's impact is relative to the other values, dampen it accordingly if driving
        if (x > 0 || y > 0) {
            turn *= Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

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