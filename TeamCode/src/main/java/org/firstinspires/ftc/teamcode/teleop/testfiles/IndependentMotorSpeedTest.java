package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@Config
@TeleOp(name = "Independent Motor Speed Test", group = "testing")
public class IndependentMotorSpeedTest extends OpMode {
    public static double topMotorPower = 0;
    public static double bottomMotorPower = 0;
    public static double servoPower = 0;

    public DcMotorEx topLaunch, bottomLaunch;
    public CRServo servo1, servo2;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        topLaunch = hardwareMap.get(DcMotorEx.class, "topLaunch");
        bottomLaunch = hardwareMap.get(DcMotorEx.class, "bottomLaunch");

        servo1 = hardwareMap.get(CRServo.class, "servo1");
        servo2 = hardwareMap.get(CRServo.class, "servo2");

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        topLaunch.setPower(topMotorPower);
        bottomLaunch.setPower(bottomMotorPower);

        servo1.setPower(servoPower);
        servo2.setPower(-servoPower);
    }
}
