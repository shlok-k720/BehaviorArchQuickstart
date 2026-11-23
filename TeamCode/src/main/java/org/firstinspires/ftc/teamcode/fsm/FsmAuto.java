package org.firstinspires.ftc.teamcode.fsm;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rmsftc.fsm.State;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * State-machine autonomous example.
 *
 * <p>The machine walks a list of states in order. Timed cases
 * ({@link DriveCase}, {@link IntakeCase}) call {@code transitionToNext()}
 * when their time is up; the builder's {@code instant} case runs once and
 * advances on its own:
 * <ol>
 *   <li>{@code DRIVE_FWD} — drive forward 1 s.</li>
 *   <li>{@code INTAKE} — intake 2 s.</li>
 *   <li>{@code DRIVE_BACK} — drive back 1 s.</li>
 *   <li>{@code SCORE} — open the claw (instant).</li>
 *   <li>{@code DONE} — print and stay (a {@code run} case never advances;
 *       the machine is finished when the OpMode stops).</li>
 * </ol>
 * States are enum constants wrapped with {@code State.of(...)} — the enum
 * name is the state's identity, so {@code State.of(AutoState.INTAKE)} and
 * {@code State.of("INTAKE")} are the same state.
 */
@Autonomous(name = "FSM: Auto", group = "BehaviorArchitectures")
public class FsmAuto extends ArchitecturalOpMode {

    enum AutoState { DRIVE_FWD, INTAKE, DRIVE_BACK, SCORE, DONE }

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        manager.jStateMachine(b -> b
                .initial(State.of(AutoState.DRIVE_FWD))
                .addCase(new DriveCase(State.of(AutoState.DRIVE_FWD), robot, 0.5, 0, 0, 1.0))
                .addCase(new IntakeCase(State.of(AutoState.INTAKE), robot, 2.0))
                .addCase(new DriveCase(State.of(AutoState.DRIVE_BACK), robot, -0.5, 0, 0, 1.0))
                .instant(State.of(AutoState.SCORE), () -> robot.openClaw())
                .print(State.of(AutoState.DONE), "FSM auto finished"));
    }

    @Override
    protected void onLoop() {
        telemetry.addData("State", manager.machines().get(0).getCurrentState().name());
    }

    @Override
    public void onStop() {
        super.onStop();
        robot.stopAll();
    }
}
