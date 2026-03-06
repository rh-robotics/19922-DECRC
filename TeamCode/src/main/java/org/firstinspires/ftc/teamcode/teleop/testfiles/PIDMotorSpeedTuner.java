package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Config
@TeleOp(name = "PID Motor Tuner", group = "PIDs")
public class PIDMotorSpeedTuner extends OpMode {
    public static String motorName = "leftFront";
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;

    public static double targetSpeed = 100;

    private PIDController controller;
    private DcMotorEx motor;
    AnalogInput absoluteEncoder;
    FtcDashboard dashboard;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        motor = hardwareMap.get(DcMotorEx.class, motorName);
        controller = new PIDController(Kp, Ki, Kd);

        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        controller.setPID(Kp, Ki, Kd); // since this is a configure file, so expect to change

        double power = controller.calculate(motor.getVelocity(), targetSpeed);

        motor.setPower(power);

        telemetry.addData("Reference", targetSpeed);
        telemetry.addData("Current servo velocity", motor.getVelocity());
        telemetry.addData("Current servo power", power);

        telemetry.update();
    }
}
