package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
@TeleOp(name = "Linked Motors Test", group = "Testing OpModes")
public class DualMotorTest extends OpMode {
    private DcMotorEx motorWithEncoder;
    private DcMotorEx motorFollower;

    // ticks per second
    public static double TARGET_VELOCITY = 1000;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        motorWithEncoder = hardwareMap.get(DcMotorEx.class, "launch1");
        motorFollower = hardwareMap.get(DcMotorEx.class, "launch2");

        motorWithEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorWithEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorFollower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorWithEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorFollower.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        motorWithEncoder.setVelocity(TARGET_VELOCITY);
        motorFollower.setPower(motorWithEncoder.getPower());

        telemetry.addData("Ticks per sec", TARGET_VELOCITY);
    }
}

