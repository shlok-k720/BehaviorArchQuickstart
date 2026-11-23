package org.firstinspires.ftc.teamcode.kotlin.commands

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.rmsftc.ftc.ArchitectureManager
import com.rmsftc.ftc.ArchitecturalOpMode
import org.firstinspires.ftc.teamcode.commands.DriveCommand
import org.firstinspires.ftc.teamcode.robot.Robot

/**
 * Command-based autonomous in Kotlin, using the `commands { }` DSL.
 *
 * `manager.commands { ... }` builds a command and schedules it
 * immediately. Steps run in sequence; `race { }` ends when its first
 * child finishes. Requirement tokens (Robot.DRIVE etc.) keep two
 * commands from fighting over the same mechanism. Java command classes
 * like [DriveCommand] plug in with `command(...)`.
 */
@Autonomous(name = "Kotlin Commands: Auto", group = "BehaviorArchitectures")
class CommandAutoKt : ArchitecturalOpMode() {

    private val robot = Robot()

    override fun onInit() {
        robot.init(hardwareMap)
    }

    override fun build(manager: ArchitectureManager) {
        manager.commands {
            // Intake while driving forward for up to 1.2 s.
            race {
                run({ robot.setIntakePower(1.0) }, Robot.INTAKE)
                command(DriveCommand(robot, { 0.5 }, { 0.0 }, { 0.0 }))
                waitMs(1200)
            }
            runOnce({ robot.setIntakePower(0.0) }, Robot.INTAKE)
            waitMs(300)
            // Score: open the claw, wait, close it.
            runOnce({ robot.openClaw() }, Robot.CLAW)
            waitMs(500)
            runOnce({ robot.closeClaw() }, Robot.CLAW)
            // Back away.
            race {
                command(DriveCommand(robot, { -0.4 }, { 0.0 }, { 0.0 }))
                waitMs(800)
            }
        }
    }

    override fun onStop() {
        robot.stopAll()
    }
}
