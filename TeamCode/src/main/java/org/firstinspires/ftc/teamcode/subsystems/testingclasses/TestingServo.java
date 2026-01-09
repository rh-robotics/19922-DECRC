package org.firstinspires.ftc.teamcode.subsystems.testingclasses;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class TestingServo implements Servo {

    private double position = 0.0;
    private Direction direction = Direction.FORWARD;
    private double minRange = 0.0;
    private double maxRange = 1.0;

    @Override
    public void setPosition(double position) {
        // Clamp to configured range like the SDK does
        this.position = Math.max(minRange, Math.min(maxRange, position));
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
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
    public void scaleRange(double min, double max) {
        this.minRange = min;
        this.maxRange = max;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "TestingServo";
    }

    @Override
    public String getConnectionInfo() {
        return "No hardware (test servo)";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {}

    @Override
    public void close() {}
}
