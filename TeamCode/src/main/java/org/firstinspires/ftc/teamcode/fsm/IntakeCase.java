package org.firstinspires.ftc.teamcode.fsm;

import com.rmsftc.fsm.Case;
import com.rmsftc.fsm.State;
import com.rmsftc.utils.Clock;

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
 * Timing uses FTCUtils' {@link Clock.Helper}: call {@code update()} once
 * per tick and accumulate {@code dtSeconds()}. That is the library's
 * delta-time pattern — prefer it over reading raw nanosecond timestamps
 * and subtracting them yourself.
 */
public class IntakeCase extends Case {

    private final Robot robot;
    private final double seconds;
    private final Clock.Helper timer = new Clock.Helper(Clock.SYSTEM);
    private double elapsedSeconds;

    public IntakeCase(State state, Robot robot, double seconds) {
        super(state);
        this.robot = robot;
        this.seconds = seconds;
    }

    @Override
    public void onStart() {
        elapsedSeconds = 0;
        timer.update(); // first update reports dt = 0; starts the clock
        robot.setIntakePower(1.0);
    }

    @Override
    public void onExecute() {
        timer.update();
        elapsedSeconds += timer.dtSeconds();
        if (elapsedSeconds >= seconds) {
            transitionToNext();
        }
    }

    @Override
    public void onTransition(Case other) {
        robot.setIntakePower(0);
    }
}
