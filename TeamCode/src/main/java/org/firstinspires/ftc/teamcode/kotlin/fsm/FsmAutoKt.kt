package org.firstinspires.ftc.teamcode.kotlin.fsm

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rmsftc.fsm.State
import com.rmsftc.ftc.ArchitectureManager
import com.rmsftc.ftc.ArchitecturalOpMode
import com.rmsftc.utils.Clock
import org.firstinspires.ftc.teamcode.robot.Robot

/**
 * State-machine autonomous in Kotlin, using the `stateMachine { }` DSL.
 *
 * Each `case` gets `onStart` / `onExecute` / `onTransition` lambdas;
 * inside them, `transitionTo(...)` and `transitionToNext()` jump between
 * states. Timing uses FTCUtils' `Clock.Helper` — `update()` per tick,
 * accumulate `dtSeconds()` — the same delta-time pattern as the Java
 * examples.
 */
@Autonomous(name = "Kotlin FSM: Auto", group = "BehaviorArchitectures")
class FsmAutoKt : ArchitecturalOpMode() {

    private enum class AutoState { DRIVE, INTAKE, SCORE, DONE }

    private val robot = Robot()

    override fun onInit() {
        robot.init(hardwareMap)
    }

    override fun build(manager: ArchitectureManager) {
        val timer = Clock.Helper(Clock.SYSTEM)
        var elapsed = 0.0

        manager.stateMachine {
            case(State.of(AutoState.DRIVE)) {
                onStart { elapsed = 0.0; timer.update() }
                onExecute {
                    timer.update()
                    elapsed += timer.dtSeconds()
                    robot.drive(0.5, 0.0, 0.0)
                    if (elapsed >= 1.2) transitionToNext()
                }
                onTransition { robot.stopDrive() }
            }
            case(State.of(AutoState.INTAKE)) {
                onStart { elapsed = 0.0; timer.update(); robot.setIntakePower(1.0) }
                onExecute {
                    timer.update()
                    elapsed += timer.dtSeconds()
                    if (elapsed >= 1.0) transitionToNext()
                }
                onTransition { robot.setIntakePower(0.0) }
            }
            case(State.of(AutoState.SCORE)) {
                onStart { elapsed = 0.0; timer.update(); robot.openClaw() }
                onExecute {
                    timer.update()
                    elapsed += timer.dtSeconds()
                    if (elapsed >= 0.5) transitionToNext()
                }
                onTransition { robot.closeClaw() }
            }
            case(State.of(AutoState.DONE)) {
                onStart { robot.stopAll() }
            }
        }
    }

    override fun onStop() {
        robot.stopAll()
    }
}
