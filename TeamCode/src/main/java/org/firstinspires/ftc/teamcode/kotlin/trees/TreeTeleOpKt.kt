package org.firstinspires.ftc.teamcode.kotlin.trees

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.rmsftc.bttree.NodeStatus
import com.rmsftc.ftc.ArchitectureManager
import com.rmsftc.ftc.ArchitecturalOpMode
import org.firstinspires.ftc.teamcode.robot.Robot

/**
 * Behavior-tree TeleOp in Kotlin, using the Kotlin DSL.
 *
 * Same structure as the Java `TreeTeleOp` — a selector whose first child
 * is a panic sequence and whose fallback is gamepad drive — but built
 * with `manager.tree { ... }` and lambda `action`/`condition` nodes
 * instead of builder calls and node subclasses. Custom node classes
 * (like the Java `DriveForTimeNode`) work here too via `node(...)`.
 */
@TeleOp(name = "Kotlin Trees: TeleOp", group = "BehaviorArchitectures")
class TreeTeleOpKt : ArchitecturalOpMode() {

    private val robot = Robot()

    override fun onInit() {
        robot.init(hardwareMap)
    }

    override fun build(manager: ArchitectureManager) {
        manager.tree {
            // Panic: if Y is held, stop everything and do nothing else.
            sequence("panic") {
                condition("y held") { gamepad1.y }
                action("stop all", tick = {
                    robot.stopAll()
                    NodeStatus.SUCCESS
                })
            }
            // Fallback: drive from the gamepad every tick.
            action("drive", tick = {
                robot.drive(
                    -gamepad1.left_stick_y.toDouble(),
                    gamepad1.left_stick_x.toDouble(),
                    gamepad1.right_stick_x.toDouble()
                )
                if (gamepad1.a) robot.setIntakePower(1.0) else robot.setIntakePower(0.0)
                NodeStatus.RUNNING
            })
        }
    }

    override fun onStop() {
        robot.stopAll()
    }
}
