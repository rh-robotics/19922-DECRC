package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.SubsystemBase;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.paths.DrivePath;

public class DriveTrain extends SubsystemBase {
    SwerveModule[] modules;
    DcMotorEx leftFront, rightFront, rightRear, leftRear;

    public DriveTrain(HardwareMap hardwareMap, boolean isSwerve) {
        // allows handling of swerve and strafe drive
        if (isSwerve) {
            configureSwerve(hardwareMap);
        } else {
            configureStrafe(hardwareMap);
        }
    }

    public void configureSwerve(HardwareMap hardwareMap) {
        modules = new SwerveModule[]{
                new SwerveModule(hardwareMap, "leftFront", "leftFront"),
                new SwerveModule(hardwareMap, "rightFront", "rightFront"),
                new SwerveModule(hardwareMap, "leftRear", "leftRear"),
                new SwerveModule(hardwareMap, "rightRear", "rightRear")};
    }

    public void configureStrafe(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
    }

    public void setModules(DrivePath path) { // in ratio to each other, turning clockwise
        double diagonalHalf = DriveConstants.TRACK_WIDTH / 2 * Math.sqrt(2);

        // leftFront, rightFront, rightRear, leftRear
        // initial positions are all known, assuming 0, 0 and 0 rotation. x / y =+- halfWidth * sqrt(2)
        double[][] initialPositions = {{-diagonalHalf, diagonalHalf}, {diagonalHalf, diagonalHalf}, {diagonalHalf, -diagonalHalf}, {-diagonalHalf, -diagonalHalf}};
        double[][] finalPositions = wheelPositions(path.getPosition(DriveConstants.CHANGE_CONSTANT));

        setDirections(initialPositions, finalPositions);
        setSpeeds(initialPositions, finalPositions);
    }

    public void setModules(double gamepadX, double gamepadY, double turn) { // in ratio to each other, turning clockwise
        double diagonalHalf = DriveConstants.TRACK_WIDTH / 2 * Math.sqrt(2);

        // leftFront, rightFront, rightRear, leftRear
        // initial positions are all known, assuming 0, 0 and 0 rotation. x / y =+- halfWidth * sqrt(2)
        double[][] initialPositions = {{-diagonalHalf, diagonalHalf}, {diagonalHalf, diagonalHalf}, {diagonalHalf, -diagonalHalf}, {-diagonalHalf, -diagonalHalf}};
        double[][] finalPositions = wheelPositions(teleopPath(gamepadX, gamepadY, turn, DriveConstants.CHANGE_CONSTANT));

        setDirections(initialPositions, finalPositions);
        setSpeeds(initialPositions, finalPositions);
    }

    private void setDirections(double[][] initialPositions, double[][] finalPositions) {
        modules[0].setDirection(90 - Math.atan2(finalPositions[0][1] - initialPositions[0][1], finalPositions[0][0] - initialPositions[0][0]) * 180 / Math.PI);
        modules[1].setDirection(90 - Math.atan2(finalPositions[1][1] - initialPositions[1][1], finalPositions[1][0] - initialPositions[1][0]) * 180 / Math.PI);
        modules[2].setDirection(90 - Math.atan2(finalPositions[2][1] - initialPositions[2][1], finalPositions[2][0] - initialPositions[2][0]) * 180 / Math.PI);
        modules[3].setDirection(90 - Math.atan2(finalPositions[3][1] - initialPositions[3][1], finalPositions[3][0] - initialPositions[3][0]) * 180 / Math.PI);
    }

    private void setSpeeds(double[][] initialPositions, double[][] finalPositions) {
        double[] distances = new double[] {
                Math.sqrt(Math.pow(finalPositions[0][0] - initialPositions[0][0], 2) + Math.pow(finalPositions[0][1] - initialPositions[0][1], 2)),
                Math.sqrt(Math.pow(finalPositions[1][0] - initialPositions[1][0], 2) + Math.pow(finalPositions[1][1] - initialPositions[1][1], 2)),
                Math.sqrt(Math.pow(finalPositions[2][0] - initialPositions[2][0], 2) + Math.pow(finalPositions[2][1] - initialPositions[2][1], 2)),
                Math.sqrt(Math.pow(finalPositions[3][0] - initialPositions[3][0], 2) + Math.pow(finalPositions[3][1] - initialPositions[3][1], 2))};

        double maxDistance =  Math.max(Math.max(Math.max(distances[0], distances[1]), distances[2]), distances[3]);

        modules[0].setVelocity(distances[0]/maxDistance);
        modules[1].setVelocity(distances[1]/maxDistance);
        modules[2].setVelocity(distances[2]/maxDistance);
        modules[3].setVelocity(distances[3]/maxDistance);
    }

    /**
     * Calculates wheel positions given a path
     * @param position = x, y, angle (in radians) on coordinate plane
     * @return an array of sets of x, y points in the order leftFront, rightFront, rightRear, leftRear
     */
    private double[][] wheelPositions(double[] position) {
        double[][] wheelPositions = new double[4][2]; // leftFront, rightFront, rightRear, leftRear
        double x = position[0], y = position[1], angle = position[2], halfWidth = DriveConstants.TRACK_WIDTH/2;

        // matrix multiplication for rotation and translation
        wheelPositions[0][0] = (x-halfWidth)*Math.cos(angle)- (y+halfWidth)*Math.sin(angle)-x*Math.cos(angle)+y*Math.sin(angle)+x;
        wheelPositions[0][1] =(x-halfWidth)*Math.sin(angle)+(y+halfWidth)*Math.cos(angle)-x*Math.sin(angle)-y*Math.cos(angle)+y;

        wheelPositions[1][0] = (x+halfWidth)*Math.cos(angle)-(y+halfWidth)*Math.sin(angle)-x*Math.cos(angle)+y*Math.sin(angle)+x;
        wheelPositions[1][1] =(x+halfWidth)*Math.sin(angle)+(y+halfWidth)*Math.cos(angle)-x*Math.sin(angle)-y*Math.cos(angle)+y;

        wheelPositions[2][0] = (x+halfWidth)*Math.cos(angle)-(y-halfWidth)*Math.sin(angle)-x*Math.cos(angle)+y*Math.sin(angle)+x;
        wheelPositions[2][1] =(x+halfWidth)*Math.sin(angle)+(y-halfWidth)*Math.cos(angle)-x*Math.sin(angle)-y*Math.cos(angle)+y;

        wheelPositions[3][0] = (x-halfWidth)*Math.cos(angle)-(y-halfWidth)*Math.sin(angle)-x*Math.cos(angle)+y*Math.sin(angle)+x;
        wheelPositions[3][1] =(x-halfWidth)*Math.sin(angle)+(y-halfWidth)*Math.cos(angle)-x*Math.sin(angle)-y*Math.cos(angle)+y;

        return wheelPositions;
    }

    private double[] teleopPath(double gamepadX, double gamepadY, double turn, double t) { // convert forward/turn to path so we can approx the derivative
        double x;
        double y;

        if (gamepadY == 0) {
            if (gamepadX == 0) {
                x = 0;
            } else {
                x = t * (gamepadX / Math.abs(gamepadX));
            }
            y = 0;
        } else if (gamepadX == 0) {
            x = 0;
            y = t * (gamepadY / Math.abs(gamepadY));
        } else { // normal case
            x = t * (gamepadX / Math.abs(gamepadX));
            y = t * Math.abs(gamepadY/ gamepadX) * (gamepadY / Math.abs(gamepadY));
        }

        double angle = turn * t * (2 * Math.PI / 360) * DriveConstants.ROTATION_SPEED_CONSTANT;
        return new double[] {x, y, angle};
    }
}