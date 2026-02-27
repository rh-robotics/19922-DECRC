package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveTrain;

//@Config
@TeleOp(name = "Lamprey Absolutes Test")
public class AbsolutesTest extends OpMode {
    AnalogInput absoluteEncoder;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        absoluteEncoder = hardwareMap.get(AnalogInput.class, "absoluteEncoder");

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double angle = (absoluteEncoder.getVoltage() / 2.2) * 360.0; // 2.2 volts converts to 360 degrees
        telemetry.addData("Absolute Angle", angle);
        telemetry.addData("Absolute Voltage", absoluteEncoder.getVoltage());
    }
}
