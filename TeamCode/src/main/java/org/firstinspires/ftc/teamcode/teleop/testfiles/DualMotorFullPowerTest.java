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
@TeleOp(name = "Dual Motor Full Power Test", group = "Testing OpModes")
public class DualMotorFullPowerTest extends OpMode {
    private DcMotorEx motorWithEncoder;
    private DcMotorEx motorFollower;

    public static double power = 1;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        motorWithEncoder = hardwareMap.get(DcMotorEx.class, "launch1");
        motorFollower = hardwareMap.get(DcMotorEx.class, "launch2");

        motorFollower.setDirection(DcMotorSimple.Direction.REVERSE);

        motorWithEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorFollower.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        motorWithEncoder.setPower(power);
        motorFollower.setPower(power);

        telemetry.addData("Ticks per sec", motorWithEncoder.getVelocity());
    }
}

