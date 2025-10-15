package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoMotorTest extends OpMode {
    Servo testServo;
    DcMotorEx testMotor;

    Gamepad previousGamepad1, currentGamepad1;
    double servoPos = 0;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the servo
        testServo = hardwareMap.get(Servo.class, "testServo");
        testMotor = hardwareMap.get(DcMotorEx.class, "testMotor");

        currentGamepad1.copy(gamepad1);
        previousGamepad1.copy(gamepad1);

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);

        telemetry.addLine("Servo and Motor Test");
        telemetry.addLine();
        telemetry.addData("D-Pad up", "Increase servo position");
        telemetry.addData("D-Pad down", "Decrease servo position");
        telemetry.addData("Left joystick up/down", "Motor power");
        telemetry.addLine();
        telemetry.addData("Servo position", testServo.getPosition());
        telemetry.addData("Motor power", testMotor.getPower());

        if (currentGamepad1.dpad_up && !previousGamepad1.dpad_up) {
            servoPos += 0.1;
        } else if (currentGamepad1.dpad_down && !previousGamepad1.dpad_down) {
            servoPos -= 0.1;
        }

        testMotor.setPower(currentGamepad1.left_stick_y);
    }
}
