package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;

public interface Subsystem {
    void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2);

}
