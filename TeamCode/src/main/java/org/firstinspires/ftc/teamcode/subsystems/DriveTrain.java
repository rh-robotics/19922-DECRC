package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class DriveTrain extends Subsystem {
    final boolean isSwerve = true;
    SwerveModule[] modules;
    DcMotorEx leftFront, rightFront, rightRear, leftRear;

    public void configure() {
        // allows handling of swerve and strafe drive
        if (isSwerve) {
            configureSwerve();
        } else {
            configureStrafe();
        }
    }

    public void configureSwerve() {

    }

    public void configureStrafe() {

    }

    public void swerveTeleopPeriodic() {

    }

    public void strafeTeleopPeriodic() {

    }

    public void swerveAutonPeriodic() {

    }

    public void strafeAutonPeriodic() {

    }
}
