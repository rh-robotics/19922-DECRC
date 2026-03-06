package org.firstinspires.ftc.teamcode.subsystems.drivetrain.pathing;


import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.DownsampledWriter;
import com.acmerobotics.roadrunner.ftc.FlightRecorder;
import com.acmerobotics.roadrunner.ftc.LynxFirmware;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.roadrunner.Localizer;
import org.firstinspires.ftc.teamcode.subsystems.roadrunner.ThreeDeadWheelLocalizer;

import java.util.LinkedList;

// CREDIT TO ROADRUNNER FOR THIS
// This is code adapted from roadrunner to fit our needs, but none of the math or code behind
// the actual localization from odometry was written by the 19922 Iron Lions
public class RobotEntity {
    private int activePointIndex = 0; // furthest point that's been seen
    double catchRange;
    private final double IN_PER_TICK = 96.0/47814.5;;
    public final Localizer localizer;
    private final LinkedList<Pose2d> poseHistory = new LinkedList<>();

    private final DownsampledWriter estimatedPoseWriter = new DownsampledWriter("ESTIMATED_POSE", 50_000_000);

    public RobotEntity(double x, double y, double heading, double catchRange, HardwareMap hardwareMap) {
        LynxFirmware.throwIfModulesAreOutdated(hardwareMap);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        localizer = new ThreeDeadWheelLocalizer(hardwareMap, IN_PER_TICK, new Pose2d(x, y, heading));

        this.catchRange = catchRange;
    }

    public boolean inRange(double[] point, double range) {
        Vector2d pos = getPose().position;
        return Math.sqrt(Math.pow(pos.x - point[0], 2) + Math.pow(pos.y - point[1], 2)) <= range;
    }

    public boolean inRange(double[] point) {
        Vector2d pos = getPose().position;
        return Math.sqrt(Math.pow(pos.x - point[0], 2) + Math.pow(pos.y - point[1], 2)) <= catchRange;
    }

    public int getActivePointIndex(){
        return activePointIndex;
    }

    public void incrementActivePointIndex(){
        activePointIndex ++;
    }

    public void setActivePointIndex(int index){
        activePointIndex = index;
    }

    public void updatePoseEstimate() {
        localizer.update();
        poseHistory.add(localizer.getPose());

        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        estimatedPoseWriter.write(new org.firstinspires.ftc.teamcode.messages.PoseMessage(localizer.getPose()));
    }

    public Pose2d getPose() {
        return localizer.getPose();
    }
}
