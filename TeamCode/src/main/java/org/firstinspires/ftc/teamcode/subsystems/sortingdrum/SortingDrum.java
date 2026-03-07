package org.firstinspires.ftc.teamcode.subsystems.sortingdrum;


import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;

public class SortingDrum {
    public enum Artifacts {
        GREEN,
        PURPLE,
        EMPTY,
        NULL
    }

    public enum ScoringModes {
        MOSAIC,
        NORMAL,
        EFFICIENCY
    }

    // how are we keeping track of what the next color/current index is?
    // manual has driver inputs
    // default just assumes it doesn't change unless we shoot
    public enum IndexingModes {
        MANUAL,
        DEFAULT
    }

    public enum State {
        INTAKING,
        SCORING
    }

    private Artifacts[] drum;
    private ColorSensor intakeSensor;
    private ColorSensor launchSensor;
    private ScoringModes scoringMode;
    private IndexingModes indexingMode;
    private State state;

    // evens = launching, odds = intaking
    // 0 = index 0 at launch, 1 = index 1 at intake, 2 = index 2 at launch,
    // 3 = index 0 at intake, 4 = index 1 at launch, 5 = index 2 at intake

    // increases with clockwise movement
    private int drumPosition = 0;

    private int setPosition = 0;
    private boolean launch = false;
    private Artifacts[] mosaicPattern;
    private int mosaicIndex = 0;

    DigitalChannel clockwiseMagnet, counterClockwiseMagnet;
    DcMotorEx drumMotor;
    public static int indexesRemaining = 0;
    private boolean clockwiseMagnetSeen = false, counterclockwiseMagnetSeen = false;

    public static double power = 0.5;

    // basically indicates the index facing the launch in this diagram
    // but this diagram of positions is also used for indexing a secondary overlay of staticPositions

    /*              launch^
     *           _____________
     *          |  (ball 0)  |
     *          |     0      |
     *  ____1___|____________|_5_____
     *  | ( ball  2)  |  (ball 1)   |
     *  |    2        |     4       |
     *  |_____________|_____________|
     *                3
     *
     *           intake v
     */

    public SortingDrum(HardwareMap hardwareMap, Artifacts[] artifacts, Artifacts[] mosaicPattern, ScoringModes scoringMode, IndexingModes indexingMode, State startState) {
        assert artifacts.length == 3;
        drum = artifacts;

        this.mosaicPattern = mosaicPattern;

        intakeSensor = hardwareMap.get(ColorSensor.class, "intakeColorSensor");
        launchSensor = hardwareMap.get(ColorSensor.class, "launchColorSensor");

        clockwiseMagnet = hardwareMap.get(DigitalChannel.class, "clockwiseMagnet");
        counterClockwiseMagnet = hardwareMap.get(DigitalChannel.class, "counterclockwiseMagnet");

        drumMotor = hardwareMap.get(DcMotorEx.class, "drumMotor");

        this.scoringMode = scoringMode;
        this.indexingMode = indexingMode;
        this.state = startState;
    }

    public SortingDrum(HardwareMap hardwareMap) {
        drum = new Artifacts[] {Artifacts.EMPTY, Artifacts.EMPTY, Artifacts.EMPTY};

        this.mosaicPattern = new Artifacts[] {Artifacts.PURPLE, Artifacts.GREEN, Artifacts.GREEN};

        intakeSensor = hardwareMap.get(ColorSensor.class, "intakeColorSensor");
        launchSensor = hardwareMap.get(ColorSensor.class, "launchColorSensor");

        clockwiseMagnet = hardwareMap.get(DigitalChannel.class, "clockwiseMagnet");
        counterClockwiseMagnet = hardwareMap.get(DigitalChannel.class, "counterclockwiseMagnet");

        drumMotor = hardwareMap.get(DcMotorEx.class, "drumMotor");

        this.scoringMode = ScoringModes.NORMAL;
        this.indexingMode = IndexingModes.DEFAULT;
        this.state = State.SCORING;
    }

    public void teleopUpdate(Gamepad currentGamepad1, Gamepad previousGamepad1, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        // verify that an empty space is currently the one in front
        if (state == State.INTAKING && emptySpace() && getValueInStaticPosition(3) != Artifacts.EMPTY) {
            setPosition = getNearestIndex(Artifacts.EMPTY, 3) + drumPosition;
        } else if (state == State.INTAKING) {
            Artifacts intakeSensorOutput = getIntakeSensorInput();
            if (getValueInStaticPosition(3) == Artifacts.EMPTY && intakeSensorOutput != Artifacts.EMPTY)
                setValueInStaticPosition(intakeSensorOutput, 3);
        }

        else if (state == State.SCORING && fullSpace() && getValueInStaticPosition(0) != mosaicPattern[mosaicIndex % 3]) {
            if (scoringMode == ScoringModes.EFFICIENCY) {
                setPosition = getNearestFullIndex(0) + drumPosition;
            } else {
                setPosition = getNearestIndex(mosaicPattern[mosaicIndex % 3], 0) + drumPosition;
            }
        } else if (state == State.SCORING) {
            Artifacts launchSensorOutput = getLaunchSensorInput();
            if (scoringMode == ScoringModes.EFFICIENCY && isFull(launchSensorOutput)) {
                launch = true;
                mosaicIndex ++;
            } else if (scoringMode == ScoringModes.NORMAL && isFull(launchSensorOutput)) {
                launch = true;
                mosaicIndex ++;
            } else {
                if (launchSensorOutput == mosaicPattern[mosaicIndex % 3]) {
                    launch = true;
                    mosaicIndex ++;
                } else {
                    setValueInStaticPosition(launchSensorOutput, 0);
                }
            }
        }

        if (currentGamepad2.circle && !previousGamepad2.circle) {
            mosaicIndex = 0;
        } else if (currentGamepad2.square && !previousGamepad2.square) {
            mosaicIndex ++;
        } else if (currentGamepad2.triangle && !previousGamepad2.triangle) {
            mosaicIndex --;
        }

        if (mosaicIndex > 9) {
            mosaicIndex = 9;
        } else if (mosaicIndex < 0) {
            mosaicIndex = 0;
        }

        updatePosition();
    }


    private void updatePosition() {
        if (indexesRemaining != 0) {
            if (indexesRemaining > 0) {
                drumMotor.setPower(power);

                // clockwise
                if (!clockwiseMagnetSeen && magnetInRange(0)) { // if you got out of range and just got back in
                    indexesRemaining--;
                }
            } else {
                drumMotor.setPower(-power);

                // counter clockwise
                if (!counterclockwiseMagnetSeen && magnetInRange(1)) { // if you got out of range and just got back in
                    indexesRemaining++;
                }
            }
        } else {
            drumMotor.setPower(0);
        }

        clockwiseMagnetSeen = magnetInRange(0);
        counterclockwiseMagnetSeen = magnetInRange(1);
    }

    public boolean magnetInRange(int index) {
        if (index == 0) {
            return !clockwiseMagnet.getState();
        }
        return !counterClockwiseMagnet.getState();
    }

    private boolean launchReady() {
        return launch;
    }

    private boolean emptySpace() {
        return drum[0] == Artifacts.EMPTY || drum[1] == Artifacts.EMPTY || drum[2] == Artifacts.EMPTY;
    }

    private boolean fullSpace() {
        return isFull(drum[0]) || isFull(drum[1]) || isFull(drum[2]);
    }

    // refer to diagram for index translation to position
    private Artifacts getValueInStaticPosition(int i) {
        // check if in launch position while checking intake index or vice versa
        if (i % 2 != drumPosition % 2) {
            return Artifacts.NULL;
        }

        // always even
        int ballIndex = Math.floorMod(i - drumPosition, 6); // actual index we're checking

        // based on diagram positions
        if (ballIndex == 0) {
            return drum[0];
        } else if (ballIndex == 4) {
            return drum[1];
        }
        return drum[2];
    }

    private void setValueInStaticPosition(Artifacts val, int i) {
        if (i % 2 != drumPosition % 2) {
            return;
        }

        int ballIndex = Math.floorMod(i - drumPosition, 6); // actual index we're checking

        // based on diagram positions
        if (ballIndex == 0) {
            drum[0] = val;
        } else if (ballIndex == 4) {
            drum[1] = val;
        } else {
            drum[2] = val;
        }
    }

    private int getNearestIndex(Artifacts val, int i) {
        Artifacts indexValue = getValueInStaticPosition(i);

        if (indexValue == Artifacts.NULL) {
            if (getValueInStaticPosition((i + 1) % 6) == val) {
                return (i + 1) % 6;
            } else if (getValueInStaticPosition(Math.floorMod(i - 1, 6)) == val) {
                return Math.floorMod(i - 1, 6);
            }
            return (i + 3) % 6;
        }

        if (getValueInStaticPosition((i + 2) % 6) == val) {
            return (i + 2) % 6;
        }
        return Math.floorMod(i - 2, 6);
    }

    private int getNearestFullIndex(int i) {
        Artifacts indexValue = getValueInStaticPosition(i);

        if (indexValue == Artifacts.NULL) {
            if (isFull(getValueInStaticPosition((i + 1) % 6))) {
                return (i + 1) % 6;
            } else if (isFull(getValueInStaticPosition(Math.floorMod(i - 1, 6)))) {
                return Math.floorMod(i - 1, 6);
            }
            return (i + 3) % 6;
        }

        if (isFull(getValueInStaticPosition((i + 2) % 6))) {
            return (i + 2) % 6;
        }
        return Math.floorMod(i - 2, 6);
    }

    private boolean isFull(Artifacts val) {
        return val == Artifacts.GREEN || val == Artifacts.PURPLE;
    }

    private Artifacts getIntakeSensorInput() {
        if (intakeSensor.green() > 150) {
            return Artifacts.GREEN;
        } else if (intakeSensor.red() > 100 && intakeSensor.blue() > 100) {
            return Artifacts.PURPLE;
        }

        return Artifacts.EMPTY;
    }

    private Artifacts getLaunchSensorInput() {
        if (launchSensor.green() > 150) {
            return Artifacts.GREEN;
        } else if (launchSensor.red() > 100 && launchSensor.blue() > 100) {
            return Artifacts.PURPLE;
        }

        return Artifacts.EMPTY;
    }

    public void resetLaunch() {
        launch = false;
    }

    public int getMosaicIndex() {
        return mosaicIndex;
    }
}
