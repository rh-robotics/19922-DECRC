package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

@Config
@TeleOp(name = "PID Swerve Module Test", group = "PIDs")
public class PIDSwerveModuleTest extends OpMode {
    public static String servoName = "servo";
    public static double Kp = 0.004;
    public static double Ki = 0.05;
    public static double Kd = 0.0001;

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

        double ZERO_POSITION = 0;
        double backwardChange = Math.floorMod((int) (target - servoPos + 180 - ZERO_POSITION), 180) - 180;
        double forwardChange = backwardChange + 180;

        double smallestChange = forwardChange;
        if (Math.abs(backwardChange) < forwardChange) {
            smallestChange = backwardChange;
        }

        // if change is positive, target > pos
        double shiftedServoPos = 180 - (smallestChange / 2);
        double shiftedTargetPos = 180 + (smallestChange / 2);

        double power = controller.calculate(shiftedServoPos, shiftedTargetPos);
        servo.setPower(power);

        telemetry.addData("Power", power);
        telemetry.addLine();

        telemetry.addData("Reference", target);
        telemetry.addData("Current servo position", servoPos);

        telemetry.addLine();
        telemetry.addData("Backward Change", backwardChange);
        telemetry.addData("Forward Change", forwardChange);

        telemetry.update();
    }
}
