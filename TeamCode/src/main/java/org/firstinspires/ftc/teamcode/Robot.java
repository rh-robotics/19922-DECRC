package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import java.util.List;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;
import org.firstinspires.ftc.teamcode.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.subsystems.launcher.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.sortingdrum.SortingDrum;

public class Robot {

    OpMode opMode;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    private DriveTrain driveTrain;
    private Intake intake;
    private Launcher launcher;
    private SortingDrum sortingDrum;
    private IMU imu;
    private final double initHeading;

    List<Subsystem> subsystemList;

    Gamepad previousGamepad1,previousGamepad2,currentGamepad1,currentGamepad2;

    public Robot(OpMode om, boolean auto) {
        telemetry = om.telemetry;
        hardwareMap = om.hardwareMap;
        opMode = om;
        previousGamepad1 =new Gamepad();
        previousGamepad2 = new Gamepad();
        currentGamepad1 = new Gamepad();
        currentGamepad2 = new Gamepad();
        imu = hardwareMap.get(IMU.class, "imu");
        initImu();
        initHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private void initImu() {
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.DOWN, RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)));
    }

    public void addSubsysem(Subsystem s) {
        subsystemList.add(s);
    }

    public void updateSubsystems() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(opMode.gamepad1);
        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(opMode.gamepad2);
        for (Subsystem sub : subsystemList) {
            sub.teleopUpdate(currentGamepad1, previousGamepad1, currentGamepad2, previousGamepad2);
        }
    }

    public void initGamepads() {
        currentGamepad1.copy(opMode.gamepad1);
        currentGamepad2.copy(opMode.gamepad2);
    }

    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }

    /**
     * Returns the heading relative to the initial heading as tracked by the IMU
     * @param units The type of units to do the calculation with
     * @return a double representing the relative heading
     */
    public double relativeHeading(AngleUnit units) {
        return imu.getRobotYawPitchRollAngles().getYaw(units) - initHeading;
    }



}
