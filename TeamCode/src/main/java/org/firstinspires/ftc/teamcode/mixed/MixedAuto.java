package org.firstinspires.ftc.teamcode.mixed;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rmsftc.bttree.NodeStatus;
import com.rmsftc.cmdlib.cmd.Commands;
import com.rmsftc.ftc.ArchitectureConverter;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;
import com.rmsftc.ftc.Trees;
import com.rmsftc.fsm.State;

import org.firstinspires.ftc.teamcode.fsm.DriveCase;
import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * Mixed-architecture autonomous: the point of BehaviorArchitectures is that
 * the three styles compose instead of forcing you to pick one.
 *
 * <p>One state machine runs the routine, and two of its states are other
 * architectures in disguise, via {@link ArchitectureConverter}:
 * <ol>
 *   <li>{@code DRIVE_FWD} — a plain FSM case ({@link DriveCase}).</li>
 *   <li>{@code INTAKE} — a <em>command</em> wrapped as a case
 *       ({@code toCase}): intake on, wait 2 s, intake off.</li>
 *   <li>{@code SCORE} — a <em>behavior tree</em> wrapped as a case
 *       ({@code toCase}): open the claw, wait, close it.</li>
 *   <li>{@code DONE} — print and finish.</li>
 * </ol>
 * Converted things run <em>inline</em>: the wrapped command/tree is ticked
 * by the case that wraps it, not by a second loop. So drive each mechanism
 * from exactly one place at a time — here the machine owns everything.
 */
@Autonomous(name = "Mixed: Auto", group = "BehaviorArchitectures")
public class MixedAuto extends ArchitecturalOpMode {

    enum MixedState { DRIVE_FWD, INTAKE, SCORE, DONE }

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        manager.jStateMachine(b -> b
                .initial(State.of(MixedState.DRIVE_FWD))
                .addCase(new DriveCase(State.of(MixedState.DRIVE_FWD), robot, 0.5, 0, 0, 1.0))
                // A command sequence, running as an FSM state.
                .addCase(ArchitectureConverter.toCase(
                        Commands.sequence(
                                Commands.runOnce(() -> robot.setIntakePower(1.0), Robot.INTAKE),
                                Commands.waitMs(2000),
                                Commands.runOnce(() -> robot.setIntakePower(0), Robot.INTAKE)),
                        State.of(MixedState.INTAKE)))
                // A behavior tree, running as an FSM state.
                .addCase(ArchitectureConverter.toCase(
                        Trees.JavaBuilder.rootTree(manager.blackboard(), t -> t
                                .sequence("score", s -> s
                                        .action("open claw", () -> {
                                            robot.openClaw();
                                            return NodeStatus.SUCCESS;
                                        })
                                        .waitMs("hold open", 500, w -> w
                                                .action("wait", () -> NodeStatus.RUNNING))
                                        .action("close claw", () -> {
                                            robot.closeClaw();
                                            return NodeStatus.SUCCESS;
                                        }))),
                        State.of(MixedState.SCORE)))
                .print(State.of(MixedState.DONE), "Mixed auto finished"));
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
