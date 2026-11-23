package org.firstinspires.ftc.teamcode.fsm;

import com.rmsftc.fsm.Case;
import com.rmsftc.fsm.State;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * A custom state-machine case: drive in a direction for a fixed time, then
 * advance to the next case. See {@link IntakeCase} for the Case lifecycle.
 */
public class DriveCase extends Case {

    private final Robot robot;
    private final double forward;
    private final double strafe;
    private final double turn;
    private final long durationNanos;
    private long startNanos;

    public DriveCase(State state, Robot robot,
                     double forward, double strafe, double turn, double seconds) {
        super(state);
        this.robot = robot;
        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
        this.durationNanos = (long) (seconds * 1_000_000_000L);
    }

    @Override
    public void onStart() {
        startNanos = nowNanos();
    }

    @Override
    public void onExecute() {
        if (nowNanos() - startNanos >= durationNanos) {
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
