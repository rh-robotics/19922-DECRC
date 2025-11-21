package org.firstinspires.ftc.teamcode.teleop.testfiles;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous
public class AprilTagTestExample extends OpMode {
    AprilTagTest aprilTagTest = new AprilTagTest();

    @Override
    public void init(){
        aprilTagTest.init(hardwareMap, telemetry);
    }

    @Override
    public void loop(){
        //update vision portal
        aprilTagTest.update();
        AprilTagDetection id20 = aprilTagTest.getTagBySpecificID(20);
        aprilTagTest.displayDetectionTelemetry(id20);
    }
}
