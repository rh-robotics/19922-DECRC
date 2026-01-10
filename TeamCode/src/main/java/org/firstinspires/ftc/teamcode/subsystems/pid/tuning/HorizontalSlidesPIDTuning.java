//package org.firstinspires.ftc.teamcode.subsystems.pid.tuning;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import org.firstinspires.ftc.teamcode.subsystems.HWC;
//
//@Config
//@TeleOp(name = "Servo PID Tuning", group = "PID Tuning")
//public class HorizontalSlidesPIDTuning extends OpMode {
//
//    // PID Variables
//    public static double p = 0, i = 0, d = 0, f = 0;
//    public static int target = 0;
//
//    // Hardware Variables
//    private PIDController leftController, rightController;
//    private HWC robot;
//    private CRServo leftServo, rightServo;
//    private AnalogInput leftEncoder, rightEncoder;
//
//    // Left Tracking Variables
//    private double lastLeftRawAngle = 0;
//    private int leftFullRotations = 0;
//    private double leftCurrentPosition = 0;
//    private double leftPID = 0;
//    private double leftFF = 0;
//
//    // Right Tracking Variables
//    private double lastRightRawAngle = 0;
//    private int rightFullRotations = 0;
//    private double rightCurrentPosition = 0;
//    private double rightPID = 0;
//    private double rightFF = 0;
//
//    @Override
//    public void init() {
//        // Initialize Hardware
//        robot = new HWC(hardwareMap, telemetry);
//
//        // Get servos and encoders
//        leftServo = robot.hSlideLeftServo;
//        rightServo = robot.hSlideRightServo;
//        leftEncoder = robot.hSlideLeftEncoder;
//        rightEncoder = robot.hSlideRightEncoder;
//
//        // Initialize PID Controllers
//        leftController = new PIDController(p, i, d);
//        rightController = new PIDController(p, i, d);
//
//        // Make telemetry FTC Dashboard Compatible
//        telemetry = new MultipleTelemetry(
//            telemetry,
//            FtcDashboard.getInstance().getTelemetry()
//        );
//    }
//
//    @Override
//    public void loop() {
//        // Update Gamepads
//        robot.previousGamepad1.copy(robot.currentGamepad1);
//        robot.currentGamepad1.copy(gamepad1);
//
//        // Update PID coefficients
//        leftController.setPID(p, i, d);
//        rightController.setPID(p, i, d);
//
//        // Get current positions with rotation tracking
//        updatePositions();
//
//        // Calculate PIDs
//        leftPID = leftController.calculate(leftCurrentPosition, target);
//        rightPID = rightController.calculate(rightCurrentPosition, target);
//
//        // Calculate Feed Forwards
//        leftFF = Math.cos(Math.toRadians(target)) * f;
//        rightFF = Math.cos(Math.toRadians(target)) * f;
//
//        // Set Servo Powers
//        double leftPower = leftPID + leftFF;
//        double rightPower = rightPID + rightFF;
//        leftServo.setPower(leftPower);
//        rightServo.setPower(rightPower);
//
//        // Telemetry
//        telemetry.addData("Target Position (degrees)", target);
//        telemetry.addData("Left Servo Power", leftServo.getPower());
//        telemetry.addData("Right Servo Power", rightServo.getPower());
//        telemetry.addData("Left Servo Position (degrees)", leftCurrentPosition);
//        telemetry.addData("Right Servo Position (degrees)", rightCurrentPosition);
//        telemetry.addLine("------");
//        telemetry.addData("Left Calculated PID", leftPID);
//        telemetry.addData("Right Calculated PID", rightPID);
//        telemetry.addData("Left Calculated FF", leftFF);
//        telemetry.addData("Right Calculated FF", rightFF);
//        telemetry.addData("    P", p);
//        telemetry.addData("    I", i);
//        telemetry.addData("    D", d);
//        telemetry.addData("    F", f);
//        telemetry.update();
//    }
//
//    private double getRawAngle(AnalogInput encoder) {
//        return (encoder.getVoltage() / 3.3) * 360.0;
//    }
//
//    private void updatePositions() {
//        // Update Left Position
//        double leftRawAngle = getRawAngle(leftEncoder);
//        if (Math.abs(leftRawAngle - lastLeftRawAngle) > 180) {
//            if (leftRawAngle < lastLeftRawAngle) {
//                leftFullRotations++;
//            } else {
//                leftFullRotations--;
//            }
//        }
//        leftCurrentPosition = (leftFullRotations * 360) + leftRawAngle;
//        lastLeftRawAngle = leftRawAngle;
//
//        // Update Right Position
//        double rightRawAngle = getRawAngle(rightEncoder);
//        if (Math.abs(rightRawAngle - lastRightRawAngle) > 180) {
//            if (rightRawAngle < lastRightRawAngle) {
//                rightFullRotations++;
//            } else {
//                rightFullRotations--;
//            }
//        }
//        rightCurrentPosition = (rightFullRotations * 360) + rightRawAngle;
//        lastRightRawAngle = rightRawAngle;
//    }
//}
