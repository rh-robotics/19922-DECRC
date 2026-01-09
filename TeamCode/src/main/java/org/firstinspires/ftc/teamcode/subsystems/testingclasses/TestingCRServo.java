package org.firstinspires.ftc.teamcode.subsystems.testingclasses;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoController;

public class TestingCRServo implements CRServo {

    private double power = 0.0;
    private Direction direction = Direction.FORWARD;

    @Override
    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "TestingCRServo";
    }

    @Override
    public String getConnectionInfo() {
        return "No hardware (test CR servo)";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {}

    @Override
    public void close() {}

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }
}
