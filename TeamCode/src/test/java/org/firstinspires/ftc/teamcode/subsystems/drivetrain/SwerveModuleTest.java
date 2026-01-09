package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import static org.junit.Assert.*;
import org.junit.Test;

public class SwerveModuleTest {
    @Test
    public void testSetPosition() {
        SwerveModule module = new SwerveModule();

        for (int i = 0; i < 1; i++) {
            double pos = Math.floor(Math.random() * 720);
            module.setDirection(pos);

            assertEquals(module.servoPosToDegrees(module.getServoPosition()) % 180, pos % 180, 0.01);

            if (!module.isReversed()) {
                assertEquals((module.servoPosToDegrees(module.getServoPosition()) + 180) % 360, pos % 360, 0.01);
            } else {
                assertEquals(module.servoPosToDegrees(module.getServoPosition()) % 360, pos % 360, 0.01);
            }
        }
    }
}