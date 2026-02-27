package org.firstinspires.ftc.teamcode.teleop.testfiles;

import android.util.Size;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;

import java.util.concurrent.TimeUnit;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTagTest {
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private WhiteBalanceControl whiteBalance;
    private ExposureControl exposure;
    private GainControl gain;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();
    private Telemetry telemetry;
    private DcMotorEx topLauncher = null;
    private DcMotorEx bottomLauncher = null;
    private Servo kicker = null;
    //private Servo angler = null;
    private CRServo leftWheel = null;
    private CRServo rightWheel = null;
    private ElapsedTime shootTimer = new ElapsedTime();
    private boolean feeding = false;

    int order = 0;
    private double lastSeenDistance = -1;
    private double motorSpeed = 0;

    public void init(HardwareMap hardwareMap, Telemetry telemetry){
        this.telemetry = telemetry;

        AprilTagLibrary customLibrary = new AprilTagLibrary.Builder()
                .addTag(20, "BLUE GOAL", 7.87, DistanceUnit.INCH)
                .addTag(21, "GPP", 7.87, DistanceUnit.INCH)
                .addTag(22, "PGP", 7.87, DistanceUnit.INCH)
                .addTag(23, "PPG", 7.87, DistanceUnit.INCH)// ID, name (any), size, units
                .addTag(24, "RED GOAL", 7.87, DistanceUnit.INCH)
                .build();

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(customLibrary)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setLensIntrinsics(481.985, 481.985, 334.203, 241.948)
                .build();


        VisionPortal.Builder builder = new VisionPortal.Builder();
        //note that the camera is called "Webcam" in hardware map :)
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640,480));
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();


        topLauncher = hardwareMap.get(DcMotorEx.class, "topLauncher");
        bottomLauncher = hardwareMap.get(DcMotorEx.class, "bottomLauncher");
        kicker = hardwareMap.get(Servo.class, "kicker");
        //angler = hardwareMap.get(Servo.class, "angler");
        leftWheel = hardwareMap.get(CRServo.class, "leftWheel");
        rightWheel = hardwareMap.get(CRServo.class, "rightWheel");

        topLauncher.setDirection(DcMotorEx.Direction.FORWARD);
        bottomLauncher.setDirection(DcMotorEx.Direction.FORWARD);

        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            // Do nothing, just wait
        }

        whiteBalance = visionPortal.getCameraControl(WhiteBalanceControl.class);
        exposure = visionPortal.getCameraControl(ExposureControl.class);
        gain = visionPortal.getCameraControl(GainControl.class);
        whiteBalance.setMode(WhiteBalanceControl.Mode.MANUAL);
        exposure.setMode(ExposureControl.Mode.Manual);

        //14, 5250, 20 from back triangle @ 4pm1
        exposure.setExposure(18, TimeUnit.MILLISECONDS);
        whiteBalance.setWhiteBalanceTemperature(5250);
        gain.setGain(20);
    }

    public void update(){
        detectedTags = aprilTagProcessor.getDetections();
        for (AprilTagDetection detection : detectedTags) {
            lastSeenDistance = detection.ftcPose.range;
        }
    }

    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }

    public double getLastSeenDistance() {
        return lastSeenDistance;
    }

    public void displayDetectionTelemetry(AprilTagDetection detectedID) {
        if (detectedID == null) {
            return;
        }

        // Update last seen distance if we currently see a tag
        if (detectedID != null) {
            lastSeenDistance = detectedID.ftcPose.range;

            String name = (detectedID.metadata != null)
                    ? detectedID.metadata.name
                    : "Unknown";

            telemetry.addLine(String.format("\n==== (ID %d) %s",
                    detectedID.id, name));

            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                    detectedID.ftcPose.x,
                    detectedID.ftcPose.y,
                    detectedID.ftcPose.z));
        }

        if (detectedID.metadata != null) {
            telemetry.addLine(String.format("\n==== (ID %d) %s", detectedID.id, detectedID.metadata.name));
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detectedID.ftcPose.x, detectedID.ftcPose.y, detectedID.ftcPose.z));
            telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detectedID.ftcPose.pitch, detectedID.ftcPose.roll, detectedID.ftcPose.yaw));
            telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detectedID.ftcPose.range, detectedID.ftcPose.bearing, detectedID.ftcPose.elevation));

            double angle = Math.abs(detectedID.ftcPose.yaw);
            //double distance = detectedID.ftcPose.range;


        } else {
            telemetry.addLine(String.format("\n==== (ID %d) Unknown", detectedID.id));
            telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detectedID.center.x, detectedID.center.y));
        }
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }

    public void findColorOrder(AprilTagDetection detectedID) {
        //use to determine order of shooting for auton
        if (detectedID.id == 21) {
            //GPP
            order = 1;
        } else if (detectedID.id == 22) {
            //PGP
            order = 2;
        } else if (detectedID.id == 23) {
            //PPG
            order = 3;
        }
        telemetry.addLine("Green is in spot " + order);
    }

    public AprilTagDetection getTagBySpecificID(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                return detection;
            }
        }
        return null;
    }

    public void handleLauncher(Gamepad gamepad) {

        double distance = lastSeenDistance;
        //can remove this down through telemetries after done testing
        boolean launcherPressed = gamepad.a;
        if (gamepad.left_bumper) {
            motorSpeed += 10;
        }
        else if (gamepad.right_bumper) {
            motorSpeed -= 10;
        }

        telemetry.addLine("Top wheel: go up -> left bumper, go down -> right bumper");
        telemetry.addLine("Wheel spin speed: " + motorSpeed);


        //TODO: add in values for speeds and angles at different distances

        if (launcherPressed) {
            telemetry.addLine("Launching!");

            //large, close triangle
            if (distance > 30 && distance < 40) {

            }
            else if (distance > 40 && distance < 50) {

            }
            else if (distance > 50 && distance < 60) {

            }
            else if (distance > 60 && distance < 70) {

            }
            else if (distance > 70 && distance < 80) {

            }
            else if (distance > 80 && distance < 90) {

            }
            else if (distance > 90 && distance < 100) {

            }
            else if (distance > 100 && distance < 110) {

            }

            //small, far triangle
            else if (distance > 130 && distance < 140) {

            }
            else if (distance > 140 && distance < 150) {

            }
            else if (distance > 150 && distance < 160) {

            }
            else if (distance > 160 && distance < 170) {

            }

            topLauncher.setVelocity(motorSpeed);
            bottomLauncher.setVelocity(motorSpeed);

            //TODO: change kicker position

            if(topLauncher.getVelocity() >= motorSpeed-100 && bottomLauncher.getVelocity() >= motorSpeed-100) {
                leftWheel.setPower(1);
                rightWheel.setPower(1);

                if (!feeding) {
                    feeding = true;
                    shootTimer.reset();
                }

                // Servo cycles every 0.5 sec
                double cycleTime = 500; // milliseconds
                double phase = shootTimer.milliseconds() % (cycleTime * 2); // forward + back

                if (phase < cycleTime) {
                    kicker.setPosition(1); // forward
                } else {
                    kicker.setPosition(0); // back
                }
            }

        }
        else {
            topLauncher.setVelocity(0);
            bottomLauncher.setVelocity(0);
            leftWheel.setPower(0);
            rightWheel.setPower(0);
        }

    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
