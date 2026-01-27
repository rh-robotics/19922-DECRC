package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.helpers.Tuple;
import org.firstinspires.ftc.teamcode.teleop.testfiles.SwerveTurnTest;

public class TeleOpStateManager {

    private static TeleOpStateManager instance;
    private final float minPadTouch;
    private double gamepadSpeed;
    private double gamepadDirection;
    private final double launcherVelocity;
    private boolean launcherOn;
    private final Gamepad previousGamepad;
    private boolean speedAndDirection;
    private Tuple<Double,Double> driveVector;
    private Tuple<Boolean,Float> turnTuple;
    private TeleOpStateManager() {
        launcherVelocity = 0.5;
        minPadTouch = 0.05f;
        gamepadSpeed = 0;
        gamepadDirection= 0;
        launcherOn = false;
        previousGamepad = new Gamepad();
    }

    /**
     * Initializes the singleton instance of the TeleOpStateManager
     * @return The singlton instance
     */
    public static TeleOpStateManager getInstance() {
        if (instance == null) {
            instance = new TeleOpStateManager();
        }
        return instance;
    }

    public boolean isTurn() {
        return turnTuple.getFirst();
    }

    public double getTurnRate() {
        return turnTuple.getSecond();
    }

    public double launcherPower() {
        return launcherOn ? launcherVelocity : 0.0;
    }

    public double getGamepadSpeed() {
        return gamepadSpeed;
    }

    public double getGamepadDirection() {
        return gamepadDirection;
    }

    public boolean isSpeedAndDirection() {
        return speedAndDirection;
    }

    public void newGamePad(Gamepad currentPad) {
        gamepadSpeed = Math.sqrt(Math.pow(currentPad.left_stick_y, 2) + Math.pow(currentPad.left_stick_x, 2));
        gamepadDirection = 180 - (Math.atan2(currentPad.left_stick_y, currentPad.left_stick_x) * 180 / Math.PI + 90);

        if (currentPad.left_trigger >= minPadTouch || currentPad.right_trigger >= minPadTouch) {
            turnTuple = new Tuple<>(true,currentPad.right_trigger - currentPad.left_trigger);
        } else if (Math.abs(currentPad.right_stick_x) > minPadTouch) {
            turnTuple = new Tuple<>(true,currentPad.right_stick_x);
        } else {
            turnTuple = new Tuple<>(false,0f);
            speedAndDirection = !(currentPad.left_stick_y == 0 && currentPad.left_stick_x == 0);
        }
        if (currentPad.triangle && !previousGamepad.triangle) {
            launcherOn = !launcherOn;
        }
        previousGamepad.copy(currentPad);
    }
}
