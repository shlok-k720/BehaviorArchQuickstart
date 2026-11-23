package org.firstinspires.ftc.teamcode.fsm;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.fsm.Case;
import com.rmsftc.fsm.State;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;

import org.firstinspires.ftc.teamcode.robot.Robot;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * State-machine TeleOp example.
 *
 * <p>The robot is always in exactly one state:
 * <ul>
 *   <li>{@code IDLE} — drive from the sticks. Pressing <b>A</b> goes to
 *       {@code INTAKE}; pressing <b>B</b> goes to {@code SCORE}.</li>
 *   <li>{@code INTAKE} — intake runs (driving still works). Releasing
 *       <b>A</b> returns to {@code IDLE}.</li>
 *   <li>{@code SCORE} — claw opens for half a second, then back to
 *       {@code IDLE}.</li>
 * </ul>
 * Cases read the gamepad through suppliers captured from the OpMode, and
 * move between states with {@code transition(State)}. Compare with the
 * tree and command TeleOps: same behavior, three styles.
 */
@TeleOp(name = "FSM: TeleOp", group = "BehaviorArchitectures")
public class FsmTeleOp extends ArchitecturalOpMode {

    enum TeleState { IDLE, INTAKE, SCORE }

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        manager.jStateMachine(b -> b
                .initial(State.of(TeleState.IDLE))
                .addCase(new IdleCase(robot,
                        () -> -gamepad1.left_stick_y,
                        () -> gamepad1.left_stick_x,
                        () -> gamepad1.right_stick_x,
                        () -> gamepad1.a,
                        () -> gamepad1.b))
                .addCase(new IntakeTeleCase(robot,
                        () -> -gamepad1.left_stick_y,
                        () -> gamepad1.left_stick_x,
                        () -> gamepad1.right_stick_x,
                        () -> gamepad1.a))
                .addCase(new ScoreCase(robot)));
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

    /** Drives from the sticks; A → INTAKE, B → SCORE. */
    private static class IdleCase extends Case {
        private final Robot robot;
        private final DoubleSupplier forward;
        private final DoubleSupplier strafe;
        private final DoubleSupplier turn;
        private final BooleanSupplier intakeButton;
        private final BooleanSupplier scoreButton;

        IdleCase(Robot robot, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn,
                 BooleanSupplier intakeButton, BooleanSupplier scoreButton) {
            super(State.of(TeleState.IDLE));
            this.robot = robot;
            this.forward = forward;
            this.strafe = strafe;
            this.turn = turn;
            this.intakeButton = intakeButton;
            this.scoreButton = scoreButton;
        }

        @Override
        public void onExecute() {
            robot.drive(forward.getAsDouble(), strafe.getAsDouble(), turn.getAsDouble());
            if (intakeButton.getAsBoolean()) {
                transition(State.of(TeleState.INTAKE));
            } else if (scoreButton.getAsBoolean()) {
                transition(State.of(TeleState.SCORE));
            }
        }
    }

    /** Intake on (driving still works); releasing A → IDLE. */
    private static class IntakeTeleCase extends Case {
        private final Robot robot;
        private final DoubleSupplier forward;
        private final DoubleSupplier strafe;
        private final DoubleSupplier turn;
        private final BooleanSupplier intakeButton;

        IntakeTeleCase(Robot robot, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn,
                       BooleanSupplier intakeButton) {
            super(State.of(TeleState.INTAKE));
            this.robot = robot;
            this.forward = forward;
            this.strafe = strafe;
            this.turn = turn;
            this.intakeButton = intakeButton;
        }

        @Override
        public void onStart() {
            robot.setIntakePower(1.0);
        }

        @Override
        public void onExecute() {
            robot.drive(forward.getAsDouble(), strafe.getAsDouble(), turn.getAsDouble());
            if (!intakeButton.getAsBoolean()) {
                transition(State.of(TeleState.IDLE));
            }
        }

        @Override
        public void onTransition(Case other) {
            robot.setIntakePower(0);
        }
    }

    /** Opens the claw for half a second, then returns to IDLE. */
    private static class ScoreCase extends Case {
        private final Robot robot;
        private long startNanos;

        ScoreCase(Robot robot) {
            super(State.of(TeleState.SCORE));
            this.robot = robot;
        }

        @Override
        public void onStart() {
            startNanos = nowNanos();
            robot.openClaw();
        }

        @Override
        public void onExecute() {
            if (nowNanos() - startNanos >= 500_000_000L) {
                transition(State.of(TeleState.IDLE));
            }
        }

        @Override
        public void onTransition(Case other) {
            robot.closeClaw();
        }
    }
}
