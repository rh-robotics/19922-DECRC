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
//@TeleOp(name = "Single Motor PID Tuning", group = "PID Tuning")
//public class SingleMotorPIDTuning extends OpMode {
//    // PID Variables
//    public static double p = 0, i = 0, d = 0, f = 0;
//    public static int target = 0;
//    private double pos = 0;
//    private double pid = 0;
//    private double ff = 0;
//
//    // Hardware Variables
//    private PIDController controller;
//    private HWC robot;
//    private DcMotorEx motor;
//    private final double TICKS_IN_DEGREES = 1425.1 / 360; // 117 RPM -> 1425.1 PPR
//
//    @Override
//    public void init() {
//        // ------ Initialize Hardware ------ //
//        robot = new HWC(hardwareMap, telemetry);
//
//        motor = robot.turretMotor;
//
//        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        // ------ Initialize PID Controller ------ //
//        controller = new PIDController(p, i, d);
//
//        // ------ Make telemetry FTC Dashboard Compatible ------ //
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//
//        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }
//
//    @Override
//    public void loop() {
//        // Update gamepads
//        robot.previousGamepad1.copy(robot.currentGamepad1);
//        robot.currentGamepad1.copy(gamepad1);
//
//        // Set PID values
//        controller.setPID(p, i, d);
//
//        // Get motor position
//        pos = motor.getCurrentPosition();
//
//        // Calculate PID
//        pid = controller.calculate(pos, target);
//
//        // Calculate feed forward
//        ff = Math.cos(Math.toRadians(target / TICKS_IN_DEGREES)) * f;
//
//        // Set motor power
//        motor.setPower(pid + ff);
//
//        // Update telemetry
//        telemetry.addData("Motor Position", pos);
//        telemetry.addData("Motor Target", target);
//        telemetry.addData("Motor Power", motor.getPower());
//        telemetry.addData("P", p);
//        telemetry.addData("I", i);
//        telemetry.addData("D", d);
//        telemetry.addData("F", f);
//        telemetry.addData("Calculated PID", pid);
//        telemetry.addData("Calculated FF", ff);
//        telemetry.update();
//    }
//}
