package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name = "Toggle Test", group = "testing")
public class ToggleTest extends OpMode {
    Gamepad previousGamepad = new Gamepad(), currentGamepad = new Gamepad();
    boolean toggle;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        previousGamepad.copy(gamepad1);
        currentGamepad.copy(gamepad1);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        previousGamepad.copy(currentGamepad);
        currentGamepad.copy(gamepad1);

        if (currentGamepad.triangle && !previousGamepad.triangle) { // new press
            toggle = !toggle;
        }

        telemetry.addData("Toggle", toggle);
    }
}
