//package org.firstinspires.ftc.teamcode.subsystems.pid.tuning;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
//import org.firstinspires.ftc.teamcode.subsystems.HWC;
//
//@Config
//@TeleOp(name = "Motor PID Tuning", group = "PID Tuning")
//public class VerticalSlidesPIDTuning extends OpMode {
//    // ------ Declare PID Variables ------ //
//    public static double p = 0, i = 0, d = 0, f = 0;
//    public static int target = 0;
//    private double motorPos = 0;
//    private double pid = 0;
//    private double ff = 0;
//
//    // ------ Declare Others ------ //
//    private PIDController controller;
//    private HWC robot;
//    private DcMotorEx slideLeft, slideRight;
//    private final double TICKS_IN_DEGREES = 751.8 / 360; // TODO: Update Value With Actual Slide Encoder Ticks Per Degree
//
//    @Override
//    public void init() {
//        // ------ Initialize Hardware ------ //
//        robot = new HWC(hardwareMap, telemetry);
//
//        slideLeft = robot.vSlideLeftMotor;
//        slideRight = robot.vSlideRightMotor;
//
//        slideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        slideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        // ------ Initialize PID Controller ------ //
//        controller = new PIDController(p, i, d);
//
//        // ------ Make telemetry FTC Dashboard Compatible ------ //
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//
//        slideLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        slideRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }
//
//    @Override
//    public void loop() {
//        // ------ Update Gamepads ------ //
//        robot.previousGamepad1.copy(robot.currentGamepad1);
//        robot.previousGamepad2.copy(robot.currentGamepad2);
//        robot.currentGamepad1.copy(gamepad1);
//        robot.currentGamepad2.copy(gamepad2);
//
//        // ------ Set PID ------ //
//        controller.setPID(p, i, d);
//
//        // ------ Get Motor Position ------ //
//        motorPos = (slideLeft.getCurrentPosition() + slideRight.getCurrentPosition()) / 2.0;
//
//        // ------ Calculate PID ------ //
//        pid = controller.calculate(motorPos, target);
//
//        // ------ Calculate Feed Forward ------ //
//        ff = Math.cos(Math.toRadians(target / TICKS_IN_DEGREES)) * f;
//
//        // ------ Set Motor Power ------ //
//        slideLeft.setPower(pid + ff);
//        slideRight.setPower(pid + ff);
//
//        // ------ Telemetry ------ //
//        telemetry.addData("Motor Position", motorPos);
//        telemetry.addData("Motor Target", target);
//        telemetry.addData("Motor Power", slideLeft.getPower());
//        telemetry.addData("P", p);
//        telemetry.addData("I", i);
//        telemetry.addData("D", d);
//        telemetry.addData("F", f);
//        telemetry.addData("Calculated PID", pid);
//        telemetry.addData("Calculated FF", ff);
//        telemetry.update();
//    }
//}