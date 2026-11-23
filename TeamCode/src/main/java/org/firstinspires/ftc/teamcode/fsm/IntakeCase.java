package org.firstinspires.ftc.teamcode.fsm;

import com.rmsftc.fsm.Case;
import com.rmsftc.fsm.State;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * A custom state-machine case: run the intake for a fixed time, then
 * advance to the next case in the machine's list.
 *
 * <p>Writing your own case means subclassing {@link Case}:
 * <ul>
 *   <li>{@code onStart()} — runs once when the machine enters this state.</li>
 *   <li>{@code onExecute()} — runs every tick while in this state. Call
 *       {@code transition(State)} to jump to a named state, or
 *       {@code transitionToNext()} to advance to the next case in the
 *       order cases were added.</li>
 *   <li>{@code onTransition(Case)} — runs when leaving this state.</li>
 * </ul>
 * {@code nowNanos()} reads the machine's clock (the same clock tests can
 * fake), so timed cases do not need their own timer objects.
 */
public class IntakeCase extends Case {

    private final Robot robot;
    private final long durationNanos;
    private long startNanos;

    public IntakeCase(State state, Robot robot, double seconds) {
        super(state);
        this.robot = robot;
        this.durationNanos = (long) (seconds * 1_000_000_000L);
    }

    @Override
    public void onStart() {
        startNanos = nowNanos();
        robot.setIntakePower(1.0);
    }

    @Override
    public void onExecute() {
        if (nowNanos() - startNanos >= durationNanos) {
            transitionToNext();
        }
    }

    @Override
    public void onTransition(Case other) {
        robot.setIntakePower(0);
    }
}
