package org.firstinspires.ftc.teamcode.subsystems.pid;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Config
@TeleOp(name = "PID Motor Position Tuner", group = "PIDs")
public class PIDMotorPositionTuner extends OpMode {
    public static String motorName = "motor";
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;

    public static double reference = 100;

    private PIDController PIDControl;
    private DcMotorEx motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        PIDControl = new PIDController(Kp, Ki, Kd);

        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        motor.setVelocity(PIDControl.getPower(reference, motor.getCurrentPosition()));

        telemetry.addData("Reference", reference);
        telemetry.addData("Current motor position", motor.getCurrentPosition());
    }
}
