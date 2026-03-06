package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
@TeleOp(name = "Geneva Test")
public class GenevaTest extends OpMode {
    DcMotorEx drumMotor;
    DigitalChannel clockwiseMagnet, counterClockwiseMagnet;
    public static int indexesRemaining = 0;
    private boolean magnetSeen = false;

    public static double power = 1;
    public static int magnetIndex = 0;

    @Override
    public void init() {
        drumMotor = hardwareMap.get(DcMotorEx.class, "drumMotor");

        clockwiseMagnet = hardwareMap.get(DigitalChannel.class, "clockwiseMagnet");
        counterClockwiseMagnet = hardwareMap.get(DigitalChannel.class, "counterclockwiseMagnet");

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);
    }

    @Override
    public void loop() {
        if (gamepad1.aWasPressed()) {
            indexesRemaining ++;
        }

        if (indexesRemaining > 0) {
            drumMotor.setPower(power);

            if (!magnetSeen && magnetInRange(magnetIndex)) { // if you got out of range and just got back in
                indexesRemaining --;
            }
        } else {
            drumMotor.setPower(0);
        }

        magnetSeen = magnetInRange(magnetIndex);

        telemetry.addData("Clockwise magnet (0)", magnetInRange(0));
        telemetry.addData("Counterclockwise magnet (1)", magnetInRange(1));
        telemetry.addData("indexes remaining", indexesRemaining);
    }

    public boolean magnetInRange(int index) {
        if (index == 0) {
            return !clockwiseMagnet.getState();
        }
        return !counterClockwiseMagnet.getState();
    }
}
