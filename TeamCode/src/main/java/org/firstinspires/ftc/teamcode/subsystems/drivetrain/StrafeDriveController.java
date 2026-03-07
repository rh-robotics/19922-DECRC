package org.firstinspires.ftc.teamcode.subsystems.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;
import static org.firstinspires.ftc.teamcode.subsystems.drivetrain.DriveConstants.*;

public class StrafeDriveController implements DriveController {

    private Robot robot;
    private DcMotorEx leftFront, rightFront, leftRear, rightRear;

    public StrafeDriveController(Robot r) {
        robot = r;
        leftFront = robot.getHardwareMap().get(DcMotorEx.class, LEFTFRONT_NAME);
        rightFront = robot.getHardwareMap().get(DcMotorEx.class, RIGHTFRONT_NAME);
        leftRear = robot.getHardwareMap().get(DcMotorEx.class, LEFTREAR_NAME);
        rightRear = robot.getHardwareMap().get(DcMotorEx.class, RIGHTREAR_NAME);
    }
    @Override
    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        double lfPower, rfPower, lrPower, rrPower;
        double drive = currentGamepad1.left_stick_y;
        double turn = currentGamepad1.left_stick_x;
        double strafe = currentGamepad1.right_stick_x;

        if (drive != 0 || turn != 0) {
            lfPower = Range.clip(drive + turn, -1.0, 1.0);
            rfPower = Range.clip(drive - turn, -1.0, 1.0);
            lrPower = Range.clip(drive + turn, -1.0, 1.0);
            rrPower = Range.clip(drive - turn, -1.0, 1.0);
        } else if (strafe != 0) {
            // Strafing
            lfPower = -strafe;
            rfPower = strafe;
            lrPower = strafe;
            rrPower = -strafe;
        } else {
            lfPower = 0;
            rfPower = 0;
            lrPower = 0;
            rrPower = 0;
        }

        leftFront.setPower(lfPower);
        leftRear.setPower(lrPower);
        rightFront.setPower(rfPower);
        rightRear.setPower(rrPower);

    }
}
