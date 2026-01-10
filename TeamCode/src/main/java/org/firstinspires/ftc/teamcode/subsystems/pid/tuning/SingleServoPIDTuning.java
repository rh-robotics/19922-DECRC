//package org.firstinspires.ftc.teamcode.subsystems.pid.tuning;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//
//@Config
//@TeleOp(name = "Single Servo PID Tuning", group = "PID Tuning")
//public class SingleServoPIDTuning extends OpMode {
//    // PID Variables
//    public static double p = 0, i = 0, d = 0, f = 0;
//    public static int target = 0;
//
//    // Hardware Variables
//    private PIDController controller;
//    private HWC robot;
//    private CRServo servo;
//    private AnalogInput encoder;
//
//    // Tracking Variables
//    private double lastRawAngle = 0;
//    private int fullRotations = 0;
//    private double currentPosition = 0;
//
//    @Override
//    public void init() {
//        // Initialize Hardware
//        robot = new HWC(hardwareMap, telemetry);
//
//        // Get specific hardware you want to tune (modify these to match your needs)
//        servo = robot.hSlideLeftServo;
//        encoder = robot.hSlideLeftEncoder;
//
//        // Initialize PID Controller
//        controller = new PIDController(p, i, d);
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
//        controller.setPID(p, i, d);
//
//        // Get current position with rotation tracking
//        updatePosition();
//
//        // Calculate PID
//        double pid = controller.calculate(currentPosition, target);
//
//        // Calculate Feed Forward
//        double ff = Math.cos(Math.toRadians(target)) * f;
//
//        // Set Motor Power
//        double power = pid + ff;
//        servo.setPower(power);
//
//        // Telemetry
//        telemetry.addData("Current Position (degrees)", currentPosition);
//        telemetry.addData("Target Position (degrees)", target);
//        telemetry.addData("Servo Power", servo.getPower());
//        telemetry.addData("Raw Encoder Value (V)", encoder.getVoltage());
//        telemetry.addData("Raw Angle (degrees)", getRawAngle());
//        telemetry.addData("# Full Rotations", fullRotations);
//        telemetry.addData("P", p);
//        telemetry.addData("I", i);
//        telemetry.addData("D", d);
//        telemetry.addData("F", f);
//        telemetry.addData("Calculated PID", pid);
//        telemetry.addData("Calculated FF", ff);
//        telemetry.update();
//    }
//
//    private double getRawAngle() {
//        return (encoder.getVoltage() / 3.3) * 360.0;
//    }
//
//    private void updatePosition() {
//        double rawAngle = getRawAngle();
//
//        // Detect if we've completed a full rotation
//        if (Math.abs(rawAngle - lastRawAngle) > 180) {
//            // If the change is large and negative, we've completed a forward rotation
//            if (rawAngle < lastRawAngle) {
//                fullRotations++;
//            } else { // If the change is large and positive, we've completed a backward rotation
//                fullRotations--;
//            }
//        }
//
//        // Update current position
//        currentPosition = (fullRotations * 360) + rawAngle;
//
//        // Store last angle for next iteration
//        lastRawAngle = rawAngle;
//    }
//}
