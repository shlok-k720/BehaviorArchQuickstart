package org.firstinspires.ftc.teamcode.fsm;

import com.rmsftc.fsm.Case;
import com.rmsftc.fsm.State;
import com.rmsftc.utils.Clock;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * A custom state-machine case: drive in a direction for a fixed time, then
 * advance to the next case. See {@link IntakeCase} for the Case lifecycle
 * and the {@link Clock.Helper} delta-time pattern used for timing.
 */
public class DriveCase extends Case {

    private final Robot robot;
    private final double forward;
    private final double strafe;
    private final double turn;
    private final double seconds;
    private final Clock.Helper timer = new Clock.Helper(Clock.SYSTEM);
    private double elapsedSeconds;

    public DriveCase(State state, Robot robot,
                     double forward, double strafe, double turn, double seconds) {
        super(state);
        this.robot = robot;
        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
        this.seconds = seconds;
    }

    @Override
    public void onStart() {
        elapsedSeconds = 0;
        timer.update();
    }

    @Override
    public void onExecute() {
        timer.update();
        elapsedSeconds += timer.dtSeconds();
        if (elapsedSeconds >= seconds) {
            transitionToNext();
            return;
        }
        robot.drive(forward, strafe, turn);
    }

    @Override
    public void onTransition(Case other) {
        robot.stopDrive();
    }
}
