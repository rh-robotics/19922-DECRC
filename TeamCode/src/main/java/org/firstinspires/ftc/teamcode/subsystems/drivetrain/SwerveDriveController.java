package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Path;
import org.firstinspires.ftc.teamcode.Point;
import org.firstinspires.ftc.teamcode.Robot;
import static org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveConstants.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SwerveDriveController implements DriveController {
    Robot robot;
    private final Map<String, SwerveModule> swerveModules;
    private boolean inPathTolerance;


    public SwerveDriveController(Robot robot) {
        this.robot = robot;
        inPathTolerance = true;
        robot.addSubsysem(this);
        // Use a Linked Hash Map to preserver order when iterating through the keys
        swerveModules = new LinkedHashMap<>();
        swerveModules.put(LEFTFRONT_NAME,new SwerveModule(robot.getHardwareMap(),LEFTFRONT_NAME, LEFTFRONT_SERVO_NAME, LEFTFRONT_ZERO, LEFTFRONT_ISREVERSED));
        swerveModules.put(RIGHTFRONT_NAME, new SwerveModule(robot.getHardwareMap(),RIGHTFRONT_NAME,RIGHTFRONT_SERVO_NAME,RIGHTFRONT_ZERO,RIGHTFRONT_ISREVERSED));
        swerveModules.put(LEFTREAR_NAME,new SwerveModule(robot.getHardwareMap(),LEFTREAR_NAME,LEFTREAR_SERVO_NAME,LEFTREAR_ZERO,LEFTREAR_ISREVERSED));
        swerveModules.put(RIGHTREAR_NAME,new SwerveModule(robot.getHardwareMap(),RIGHTREAR_NAME,RIGHTREAR_SERVO_NAME,RIGHTREAR_ZERO,RIGHTREAR_ISREVERSED));
    }

    private void stop() {
        drive(0);
    }
    private void drive(double speed) {
        for (Map.Entry<String,SwerveModule> entry : swerveModules.entrySet()) {
            entry.getValue().setVelocity(speed);
        }
    }

    private void drive(double speed, double direction) {
        for (Map.Entry<String,SwerveModule> entry : swerveModules.entrySet()) {
            entry.getValue().setVelocity(speed);
            entry.getValue().setDirection(direction);
        }
    }

    private void drive(Map<String,Point> initialPositions, Map<String,Point> finalPositions, double speed) {
        Map<String,Double> distances = new HashMap<>();
        double maxDistance = 0;
        for (Map.Entry<String,Point> entry: initialPositions.entrySet()) {
            double distance = Math.sqrt(Math.pow(finalPositions.get(entry.getKey()).x() - entry.getValue().x(),2) + Math.pow(finalPositions.get(entry.getKey()).y() - entry.getValue().y(),2));
            maxDistance = Math.max(maxDistance, distance);
            distances.put(entry.getKey(),distance);
        }

        inPathTolerance = !(maxDistance > PATH_FOLLOWING_TOLERANCE);

        for (Map.Entry<String,SwerveModule> entry : swerveModules.entrySet()) {
            entry.getValue().setVelocity(distances.get(entry.getKey())/maxDistance * speed);
        }
    }

    private void turn(Map<String,Point> initialPositions, Map<String,Point> finalPositions) {
        for (Map.Entry<String,Point> entry : initialPositions.entrySet()) {
            double d = calcTurn(entry.getValue(), Objects.requireNonNull(finalPositions.get(entry.getKey())));
            Objects.requireNonNull(swerveModules.get(entry.getKey())).setDirection(d);
        }
    }

    private void turn(double speed) {
        double turnSpeed = speed * TURN_MAX_SPEED;
        Objects.requireNonNull(swerveModules.get(LEFTFRONT_NAME)).setDirection(-45);
        Objects.requireNonNull(swerveModules.get(LEFTFRONT_NAME)).setVelocity(turnSpeed);

        Objects.requireNonNull(swerveModules.get(RIGHTFRONT_NAME)).setDirection(45);
        Objects.requireNonNull(swerveModules.get(RIGHTFRONT_NAME)).setVelocity(-turnSpeed);

        Objects.requireNonNull(swerveModules.get(LEFTREAR_NAME)).setDirection(45);
        Objects.requireNonNull(swerveModules.get(LEFTREAR_NAME)).setVelocity(turnSpeed);

        Objects.requireNonNull(swerveModules.get(RIGHTREAR_NAME)).setDirection(-45);
        Objects.requireNonNull(swerveModules.get(RIGHTREAR_NAME)).setVelocity(-turnSpeed);
    }


    private double calcTurn(Point i, Point f) {
        double d = Math.atan2(f.y() - i.y(), f.x()-i.x()) * 180.0/Math.PI + 90;
        d -= robot.relativeHeading(AngleUnit.DEGREES);
        return d;
    }

    private void driveWithGamepad(double gamepadX, double gamePadY, double turn, boolean isSlow) {
        Path path = teleopPath(gamepadX, gamePadY, turn);
        double relHeading = robot.relativeHeading(AngleUnit.RADIANS);

        Map<String, Point> currentPositions = calculatePathPoints(0,0, relHeading);
        Map<String, Point> nextPositions = calculatePathPoints(path.x(),path.y(), relHeading + (path.turn() * ROTATION_SPEED_DAMPENER));

        drive(currentPositions, nextPositions, isSlow ? TELEOP_SLOW_SPEED : TELEOP_MAX_SPEED);
        turn(currentPositions, nextPositions);
    }

    /*
      should maybe be static since it doesn't depend on state
     */
    private Map<String, Point> calculatePathPoints(double x, double y, double angle) {
        double halfWidth = TRACK_WIDTH / 2;
        Map<String,Point> map = new HashMap<>();
        map.put(LEFTFRONT_NAME, new Point(-halfWidth * (Math.cos(angle) + Math.sin(angle))+x,halfWidth * (Math.cos(angle) - Math.sin(angle))+y));
        map.put(RIGHTFRONT_NAME, new Point(halfWidth * (Math.cos(angle) - Math.sin(angle))+x,halfWidth * (Math.cos(angle) + Math.sin(angle))+y));
        map.put(LEFTREAR_NAME,new Point(halfWidth * (-Math.cos(angle) + Math.sin(angle))+x,-halfWidth * (Math.cos(angle) + Math.sin(angle))+y));
        map.put(RIGHTREAR_NAME, new Point(-halfWidth * (-Math.cos(angle) - Math.sin(angle))+x,-halfWidth * (Math.cos(angle) - Math.sin(angle))+y));

        return map;
    }

    /*
    Should maybe be static since it doesn't depend on state
     */
    private Path teleopPath(double x, double y, double turn) {
        double realx = 0;
        double realy = 0 ;
        double realTurn = turn;

        if (y==0 && x!=0) {
            realx = x/Math.abs(x);
        } else if (x==0) {
            realy = y/Math.abs(y);
        } else {
            realx=x/Math.abs(x);
            realy=Math.abs(y/x) * (y/Math.abs(y));
        }

        /* since turn's impact is relative to the other values, scale it accordingly if driving
        if (x > 0 || y > 0) { // TODO: decide if this is smth we want or if turn speed should increase as we go slower
            turn *= Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }
         */

        return new Path(realx, realy, realTurn);
    }

    @Override
    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        if (currentGamepad1.left_trigger >= 0.05 || currentGamepad1.right_trigger >= 0.05) {
            turn(currentGamepad1.right_trigger - currentGamepad1.left_trigger);
        } else if (Math.abs(currentGamepad1.right_stick_x) > 0.05) {
            turn(currentGamepad1.right_stick_x);
        } else if (currentGamepad1.left_stick_y != 0 || currentGamepad1.left_stick_x != 0 || currentGamepad1.right_stick_x != 0) {
            driveWithGamepad(currentGamepad1.left_stick_x, currentGamepad1.left_stick_y, currentGamepad1.right_stick_x, currentGamepad1.right_bumper);
        } else {
            stop();
        }
    }
}
