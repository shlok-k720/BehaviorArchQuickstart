package org.firstinspires.ftc.teamcode.kotlin.mixed

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rmsftc.bttree.NodeStatus
import com.rmsftc.cmdlib.cmd.Commands
import com.rmsftc.fsm.State
import com.rmsftc.ftc.ArchitectureConverter
import com.rmsftc.ftc.ArchitectureManager
import com.rmsftc.ftc.ArchitecturalOpMode
import com.rmsftc.ftc.Trees
import org.firstinspires.ftc.teamcode.fsm.DriveCase
import org.firstinspires.ftc.teamcode.robot.Robot

/**
 * All three architectures in one Kotlin autonomous — the same routine
 * as the Java `MixedAuto`, in Kotlin syntax.
 *
 * One state machine runs the routine; two of its states are other
 * architectures in disguise via [ArchitectureConverter]:
 * 1. `DRIVE_FWD` — a plain FSM case (the Java [DriveCase]).
 * 2. `INTAKE` — a command sequence wrapped as a case (`toCase`).
 * 3. `SCORE` — a behavior tree wrapped as a case (`toCase`).
 * 4. `DONE` — stop everything.
 *
 * Converted things run inline inside whatever wraps them — so drive
 * each mechanism from exactly one place at a time.
 */
@Autonomous(name = "Kotlin Mixed: Auto", group = "BehaviorArchitectures")
class MixedAutoKt : ArchitecturalOpMode() {

    private enum class MixedState { DRIVE_FWD, INTAKE, SCORE, DONE }

    private val robot = Robot()

    override fun onInit() {
        robot.init(hardwareMap)
        robot.closeClaw()
    }

    override fun build(manager: ArchitectureManager) {
        // A command sequence, running as an FSM state.
        val intakeCommand = Commands.sequence(
            Commands.runOnce({ robot.setIntakePower(1.0) }, Robot.INTAKE),
            Commands.waitMs(2000),
            Commands.runOnce({ robot.setIntakePower(0.0) }, Robot.INTAKE)
        )

        // A behavior tree, running as an FSM state.
        val scoreTree = Trees.KotlinBuilder.rootTree(manager.blackboard()) {
            sequence("score") {
                action("open claw", tick = {
                    robot.openClaw()
                    NodeStatus.SUCCESS
                })
                action("close claw", tick = {
                    robot.closeClaw()
                    NodeStatus.SUCCESS
                })
            }
        }

        manager.stateMachine {
            initial(State.of(MixedState.DRIVE_FWD))
            add(DriveCase(State.of(MixedState.DRIVE_FWD), robot, 0.5, 0.0, 0.0, 1.0))
            add(ArchitectureConverter.toCase(intakeCommand, State.of(MixedState.INTAKE)))
            add(ArchitectureConverter.toCase(scoreTree, State.of(MixedState.SCORE)))
            case(State.of(MixedState.DONE)) {
                onStart { robot.stopAll() }
            }
        }
    }

    override fun onStop() {
        robot.stopAll()
    }
}
