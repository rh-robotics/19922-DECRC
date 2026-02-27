package org.firstinspires.ftc.teamcode.subsystems.apriltag;

import android.util.Size;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.SubsystemBase;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTag implements SubsystemBase {
    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private WhiteBalanceControl whiteBalance;
    private ExposureControl exposure;
    private GainControl gain;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();
    public AprilTag(HardwareMap map, Telemetry telemetry) {
        hardwareMap = map;
        this.telemetry = telemetry;
        init();
    }

    private void init() {

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
        builder.setCameraResolution(new Size(640, 480));
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();

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
    }
    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }
    public void displayDetectionTelemetry(AprilTagDetection detectedID) {
        if (detectedID == null) {
            return;
        }

        // Update last seen distance if we currently see a tag
        if (detectedID != null) {
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


        } else {
            telemetry.addLine(String.format("\n==== (ID %d) Unknown", detectedID.id));
            telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detectedID.center.x, detectedID.center.y));
        }
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }

    public AprilTagDetection getTagBySpecificID(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                return detection;
            }
        }
        return null;
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    @Override
    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {

    }

    public double findDistance() {
        if (getTagBySpecificID(24).rawPose == null) {
            return getTagBySpecificID(20).ftcPose.range;
        } else if (getTagBySpecificID(20).rawPose == null){
            return getTagBySpecificID(24).ftcPose.range;
        }
        if(getTagBySpecificID(20).frameAcquisitionNanoTime > getTagBySpecificID(24).frameAcquisitionNanoTime) {
            return getTagBySpecificID(20).ftcPose.range;
        } else {
            return getTagBySpecificID(24).ftcPose.range;
        }
    }
}
