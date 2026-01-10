package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.drivetrain.SwerveModule;

@Config
@TeleOp(name = "Servo Zero", group = "testing")
public class ServoZero extends OpMode {
    Servo servo;
    public static String servoName = "testServo";
    public static double position = 0;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, servoName);
    }

    @Override
    public void loop() {
        servo.setPosition(position);
    }
}