package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class Intake implements Subsystem {
    DcMotorEx intakeMotor;

    public Intake(HardwareMap hardwareMap, boolean isReversed) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        if (isReversed) {
            intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setPower(0);
    }

    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        if (currentGamepad1.square) {
            turnOn();
        } else if (currentGamepad1.triangle) {
            turnOff();
        }
    }

    public void turnOn() {
        intakeMotor.setPower(1);
    }

    public void turnOff() {
        intakeMotor.setPower(0);
    }
}
