package org.firstinspires.ftc.teamcode.subsystems.launcher;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.SubsystemBase;
import org.firstinspires.ftc.teamcode.subsystems.apriltag.AprilTag;

public class Launcher implements SubsystemBase {
    private DcMotorEx topLauncher = null;
    private DcMotorEx bottomLauncher = null;
    private Servo kicker = null;
    private Servo angler = null;
    private Servo rgbIndicator;
    private AprilTag aprilTag;
    private final double TICKS_PER_SECOND = 28; //change

    private double motorSpeed = 0;
    private double ticksPerSecond = 0;
    private boolean override = false;
    private boolean feeding = false;
    private ElapsedTime shootTimer = new ElapsedTime();
    private Telemetry telemetry;

    public Launcher(HardwareMap hardwareMap, AprilTag aprilTag, Telemetry telemetry){
        this.aprilTag = aprilTag;
        this.telemetry = telemetry;

        topLauncher = hardwareMap.get(DcMotorEx.class, "launch1");
        bottomLauncher = hardwareMap.get(DcMotorEx.class, "launch2");
        kicker = hardwareMap.get(Servo.class, "kicker");
        angler = hardwareMap.get(Servo.class, "angler");
        rgbIndicator = hardwareMap.get(Servo.class, "rgbIndicator");

        topLauncher.setDirection(DcMotorEx.Direction.FORWARD);
        bottomLauncher.setDirection(DcMotorEx.Direction.REVERSE);

        topLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
    public double convertRPM(double rpm) {
        return ((rpm*TICKS_PER_SECOND)/60.0);
    }

    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        if (currentGamepad1.circle){
            aprilTag.update();
            double distance = aprilTag.findDistance();

            if(currentGamepad1.cross && !previousGamepad1.cross){
                if (override) {
                    override = false;
                } else {
                    override = true;
                }
            }

            if (!override) {
                //large, close triangle
                if (distance < 30) {
                    motorSpeed = 1500;
                } else if (distance > 30 && distance < 35) {
                    motorSpeed =1500;
                    //angler.setPosition();
                } else if (distance > 35 && distance < 40) {
                    motorSpeed =1500;
                    //angler.setPosition();
                } else if (distance > 40 && distance < 45) {
                    motorSpeed = 1500;
                } else if (distance > 45 && distance < 50) {
                    motorSpeed = 1500;
                } else if (distance > 50 && distance < 55) {
                    motorSpeed = 1500;
                } else if (distance > 55 && distance < 60) {
                    motorSpeed = 1500;
                } else if (distance > 60 && distance < 65) {
                    motorSpeed = 1500;
                } else if (distance > 65 && distance < 70) {
                    motorSpeed = 1500;
                } else if (distance > 70 && distance < 75) {
                    motorSpeed = 1500;
                } else if (distance > 75 && distance < 80) {
                    motorSpeed = 1500;
                } else if (distance > 80 && distance < 85) {
                    motorSpeed = 1500;
                } else if (distance > 85 && distance < 90) {
                    motorSpeed = 1500;
                } else if (distance > 90 && distance < 95) {
                    motorSpeed = 1500;
                } else if (distance > 95 && distance < 100) {
                    motorSpeed = 1500;
                } else if (distance > 100 && distance < 105) {
                    motorSpeed = 1500;
                } else if (distance > 105 && distance < 110) {
                    motorSpeed = 1500;
                }

                //small, far triangle
                else if (distance > 130 && distance < 135) {

                } else if (distance > 135 && distance < 140) {

                } else if (distance > 140 && distance < 145) {

                } else if (distance > 145 && distance < 150) {

                } else if (distance > 150 && distance < 155) {

                } else if (distance > 155 && distance < 160) {

                } else if (distance > 160 && distance < 165) {

                } else if (distance > 165 && distance < 170) {

                }
                ticksPerSecond = convertRPM(motorSpeed);
                topLauncher.setVelocity(ticksPerSecond);
                bottomLauncher.setVelocity(ticksPerSecond);
                telemetry.addLine("Distance: " + distance);
                telemetry.addLine("Motor speed: " + motorSpeed);
                telemetry.addLine("Angled hood position: " + angler.getPosition());
            } else {
                if (motorSpeed == 0) {
                    motorSpeed= 2000;
                }
                if (currentGamepad1.dpad_left) {
                    motorSpeed-=10;
                }
                if (currentGamepad1.dpad_right) {
                    motorSpeed+=10;
                }
                if (currentGamepad1.dpad_down) {
                    angler.setPosition(angler.getPosition()-0.01);
                }
                if (currentGamepad1.dpad_up) {
                    angler.setPosition(angler.getPosition()+0.01);
                }
                ticksPerSecond = convertRPM(motorSpeed);
                topLauncher.setVelocity(ticksPerSecond);
                bottomLauncher.setVelocity(ticksPerSecond);
                telemetry.addLine("Motor speed: " + motorSpeed);
                telemetry.addLine("Angled hood position: " + angler.getPosition());
            }

            //TODO: change kicker position

            if(topLauncher.getVelocity() >= ticksPerSecond-50) {
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

        } else {
            topLauncher.setVelocity(0);
            bottomLauncher.setVelocity(0);
        }

        double angleFromGoal = aprilTag.findAngle();
        if (angleFromGoal > 3) {
            rgbIndicator.setPosition(.63); //blue means go left
        } else if (angleFromGoal < -3) {
            rgbIndicator.setPosition(.27); //red means go right
        } else if (-3 < angleFromGoal && angleFromGoal < 3){
            rgbIndicator.setPosition(.5); //green means you're within 3 degrees
        } else {
            rgbIndicator.setPosition(.72); //green means nothing running
        }
    }

}