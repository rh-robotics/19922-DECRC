package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo Motor Test", group = "testing")
public class ServoMotorTest extends OpMode {
    Servo testServo;
    CRServo testCRServo;
    DcMotorEx testMotor;

    Gamepad previousGamepad1 = new Gamepad(), currentGamepad1 = new Gamepad();
    double servoPos = 0;

    @Override
    public void init() {
        // Tell the driver the Op is initializing
        telemetry.addData("Status", "Initializing");

        // Initialize the servo
        testServo = hardwareMap.get(Servo.class, "testServo");
        testCRServo = hardwareMap.get(CRServo.class, "testCRServo");
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
        telemetry.addData("Triangle", gamepad1.triangle);
        telemetry.addData("Cross", gamepad1.cross);
        telemetry.addData("Left joystick up/down", "Motor power");
        telemetry.addLine();
        telemetry.addData("Servo position", testServo.getPosition());
        telemetry.addData("Motor power", testCRServo.getPower());

        if (gamepad1.square) {
            servoPos = 1;
        } else if (gamepad1.triangle) {
            servoPos = 0;
        }

        testMotor.setPower(0);
        if (gamepad1.left_bumper) {
            testMotor.setPower(1);
        }

        testServo.setPosition(servoPos);

        testCRServo.setPower(currentGamepad1.left_stick_y);
    }
}
