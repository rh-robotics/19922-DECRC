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

import org.firstinspires.ftc.teamcode.subsystems.roadrunner.roadrunner.Localizer;
import org.firstinspires.ftc.teamcode.subsystems.roadrunner.roadrunner.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.subsystems.roadrunner.roadrunner.messages.PoseMessage;

import java.util.LinkedList;

// CREDIT TO ROADRUNNER FOR THIS
// This is code adapted from roadrunner to fit our needs, but none of the math or code behind
// the actual localization from odometry was written by the 19922 Iron Lions
public class RobotEntity {
    private int activePointIndex = 0; // furthest point that's been seen
    double catchRange;

    public static class Params {
        // IMU orientation
        // TODO: fill in these values based on
        //   see https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html?highlight=imu#physical-hub-mounting
        public RevHubOrientationOnRobot.LogoFacingDirection logoFacingDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        public RevHubOrientationOnRobot.UsbFacingDirection usbFacingDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        // drive model parameters
        public double inPerTick = 1;
        public double lateralInPerTick = inPerTick;
        public double trackWidthTicks = 0;
    }
    public static Params PARAMS = new Params();
    public final MecanumKinematics kinematics = new MecanumKinematics(
            PARAMS.inPerTick * PARAMS.trackWidthTicks, PARAMS.inPerTick / PARAMS.lateralInPerTick);


    public final Localizer localizer;
    private final LinkedList<Pose2d> poseHistory = new LinkedList<>();

    private final DownsampledWriter estimatedPoseWriter = new DownsampledWriter("ESTIMATED_POSE", 50_000_000);

    public RobotEntity(double x, double y, double heading, double catchRange, HardwareMap hardwareMap) {
        LynxFirmware.throwIfModulesAreOutdated(hardwareMap);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        localizer = new ThreeDeadWheelLocalizer(hardwareMap, PARAMS.inPerTick, new Pose2d(x, y, heading));

        this.catchRange = catchRange;

        FlightRecorder.write("MECANUM_PARAMS", PARAMS);
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
        PoseVelocity2d vel = localizer.update();
        poseHistory.add(localizer.getPose());

        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        estimatedPoseWriter.write(new PoseMessage(localizer.getPose()));
    }

    public Pose2d getPose() {
        return localizer.getPose();
    }
}
