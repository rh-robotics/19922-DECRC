package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
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

    public void update(Gamepad currentGamepad) {
        if (currentGamepad.square) {
            turnOn();
        } else if (currentGamepad.triangle) {
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
