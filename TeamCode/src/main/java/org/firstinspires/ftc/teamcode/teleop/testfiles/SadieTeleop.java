package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class SadieTeleop extends OpMode {
    AprilTagTest aprilTagTest = new AprilTagTest();
    DigitalChannel mainMagneticEncoder;
    DigitalChannel secondaryMagneticEncoder;
    DcMotor drumMotor;
    int rotationCounter = 0;

    @Override
    public void init(){

        aprilTagTest.init(hardwareMap, telemetry);
        mainMagneticEncoder = hardwareMap.get(DigitalChannel.class, "mainMagneticEncoder");
        secondaryMagneticEncoder = hardwareMap.get(DigitalChannel.class, "secondaryMagneticEncoder");
        drumMotor = hardwareMap.get(DcMotor.class, "drumMotor");

        mainMagneticEncoder.setMode(DigitalChannel.Mode.INPUT);
        secondaryMagneticEncoder.setMode(DigitalChannel.Mode.INPUT);

        drumMotor.setDirection(DcMotorSimple.Direction.FORWARD);


        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public boolean mainMagnetClosed() {
        return !mainMagneticEncoder.getState();
    }
    public boolean secondaryMagnetClosed() {
        return !secondaryMagneticEncoder.getState();
    }

    @Override
    public void loop(){

        if (gamepad1.b) {
            drumMotor.setPower(.5);
            if (secondaryMagnetClosed()){
                rotationCounter = 1;
            }
            if (mainMagnetClosed() && rotationCounter == 1){
                drumMotor.setPower(0);
                rotationCounter = 0;
            }
        } else {
            drumMotor.setPower(0);
        }

        boolean launcherPressed = gamepad1.a;
        //update vision portal
        aprilTagTest.update();
        aprilTagTest.handleLauncher(gamepad1);
        AprilTagDetection id20 = aprilTagTest.getTagBySpecificID(20);
        AprilTagDetection id21 = aprilTagTest.getTagBySpecificID(21);
        AprilTagDetection id22 = aprilTagTest.getTagBySpecificID(22);
        AprilTagDetection id23 = aprilTagTest.getTagBySpecificID(23);
        AprilTagDetection id24 = aprilTagTest.getTagBySpecificID(24);
        aprilTagTest.displayDetectionTelemetry(id20);
        aprilTagTest.displayDetectionTelemetry(id24);
        aprilTagTest.findColorOrder(id21);
        aprilTagTest.findColorOrder(id22);
        aprilTagTest.findColorOrder(id23);
    }
}
