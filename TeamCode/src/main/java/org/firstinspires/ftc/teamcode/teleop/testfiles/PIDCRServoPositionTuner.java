package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp(name = "PID CR Servo With Absolute Encoder Position Tuner", group = "PIDs")
public class PIDCRServoPositionTuner extends OpMode {
    public static String servoName = "servo";
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;

    public static double target = 100;

    private PIDController controller;
    private CRServo servo;
    AnalogInput absoluteEncoder;
    FtcDashboard dashboard;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        servo = hardwareMap.get(CRServo.class, servoName);
        controller = new PIDController(Kp, Ki, Kd);

        absoluteEncoder = hardwareMap.get(AnalogInput.class, "absoluteEncoder");
    }

    @Override
    public void loop() {
        controller.setPID(Kp, Ki, Kd); // since this is a configure file, so expect to change

        double servoPos = (absoluteEncoder.getVoltage() / 2.2) * 360.0; // 2.2 volts converts to 360 degrees

        double power = controller.calculate(servoPos, target);

        servo.setPower(power);

        telemetry.addData("Reference", target);
        telemetry.addData("Current servo position", servoPos);
        telemetry.addData("Current servo power", power);

        telemetry.update();
    }
}
