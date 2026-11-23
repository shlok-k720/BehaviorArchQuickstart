package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rmsftc.cmdlib.cmd.Commands;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;
import com.rmsftc.ftc.CommandsBuilder;

import org.firstinspires.ftc.teamcode.robot.Robot;

import java.util.function.Consumer;

/**
 * Command-based autonomous example.
 *
 * <p>{@code manager.commands(...)} builds a sequential command from the
 * builder steps and schedules it immediately; it starts running on the
 * first loop tick after start:
 * <ol>
 *   <li>Drive forward for 1 second (a {@link DriveCommand} with constant
 *       suppliers, raced against a 1 s wait — the race ends the drive).</li>
 *   <li>Intake for 2 seconds, in parallel with nothing else here, but the
 *       {@code parallel} block shows how to run steps together.</li>
 *   <li>Drive back, then open the claw to score.</li>
 * </ol>
 * Timed drives use {@code race}: the drive command never finishes on its
 * own, so the wait "wins" and interrupts it — and {@code DriveCommand.end}
 * stops the motors.
 */
@Autonomous(name = "Commands: Auto", group = "BehaviorArchitectures")
public class CommandAuto extends ArchitecturalOpMode {

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        // The cast picks the Java (Consumer) overload over the Kotlin DSL one.
        manager.commands((Consumer<CommandsBuilder>) b -> b
                // Drive forward 1 s.
                .race((Consumer<CommandsBuilder>) r -> r
                        .command(new DriveCommand(robot, () -> 0.5, () -> 0, () -> 0))
                        .waitMs(1000))
                // Intake for 2 s.
                .race((Consumer<CommandsBuilder>) r -> r
                        .startEnd(() -> robot.setIntakePower(1.0),
                                () -> robot.setIntakePower(0),
                                Robot.INTAKE)
                        .waitMs(2000))
                // Drive back 1 s.
                .race((Consumer<CommandsBuilder>) r -> r
                        .command(new DriveCommand(robot, () -> -0.5, () -> 0, () -> 0))
                        .waitMs(1000))
                .runOnce(() -> robot.openClaw(), Robot.CLAW)
                .print("Commands auto finished"));
    }

    @Override
    public void onStop() {
        super.onStop();
        robot.stopAll();
    }
}
