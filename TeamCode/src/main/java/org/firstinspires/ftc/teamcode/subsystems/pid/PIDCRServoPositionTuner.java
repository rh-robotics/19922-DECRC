package org.firstinspires.ftc.teamcode.subsystems.pid;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp(name = "PID CR Servo With Absolute Encoder Position Tuner", group = "PIDs")
public class PIDCRServoPositionTuner extends OpMode {
    public static String servoName = "servo";
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;

    public static double reference = 100;

    private PIDController PIDControl;
    private CRServo servo;
    AnalogInput absoluteEncoder;
    FtcDashboard dashboard;
    Telemetry dashboardTelemetry;

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();

        servo = hardwareMap.get(CRServo.class, servoName);
        PIDControl = new PIDController(Kp, Ki, Kd);

        absoluteEncoder = hardwareMap.get(AnalogInput.class, "absoluteEncoder");
    }

    @Override
    public void loop() {
        double angle = (absoluteEncoder.getVoltage() / 2.2) * 360.0; // 2.2 volts converts to 360 degrees

        servo.setPower(PIDControl.getPower(reference, angle));

        dashboardTelemetry.addData("Reference", reference);
        dashboardTelemetry.addData("Current servo position", angle);
    }
}
