package org.firstinspires.ftc.teamcode.commands;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.cmdlib.cmd.Commands;
import com.rmsftc.cmdlib.trigger.Trigger;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;
import com.rmsftc.utils.Clock;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * Command-based TeleOp example.
 *
 * <p>Three ideas on display:
 * <ul>
 *   <li><b>A default drive command</b> ({@link DriveCommand}) scheduled in
 *       {@code build()}: it owns the {@code DRIVE} requirement and runs
 *       until something else needs the drive.</li>
 *   <li><b>Triggers</b> bind gamepad conditions to commands:
 *       <ul>
 *         <li><b>A</b> held → intake runs; released → it stops
 *             ({@code whileTrue}).</li>
 *         <li><b>B</b> pressed → a score sequence: open the claw, wait,
 *             close it ({@code onTrue}).</li>
 *       </ul></li>
 *   <li><b>Requirement tokens</b>: the intake command requires
 *       {@code INTAKE}, the score sequence requires {@code CLAW}, so they
 *       never fight the drive command or each other.</li>
 * </ul>
 * Triggers are created against the manager's scheduler and registered with
 * {@code addTrigger} so {@code tickAll()} polls them every loop.
 */
@TeleOp(name = "Commands: TeleOp", group = "BehaviorArchitectures")
public class CommandTeleOp extends ArchitecturalOpMode {

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        // Default drive: runs from the first loop tick until interrupted.
        manager.schedule(new DriveCommand(robot,
                () -> -gamepad1.left_stick_y,
                () -> gamepad1.left_stick_x,
                () -> gamepad1.right_stick_x));

        // A held: intake on; released: intake off (the command is cancelled).
        Trigger intakeButton = new Trigger(() -> gamepad1.a, manager.scheduler(), Clock.SYSTEM);
        intakeButton.whileTrue(Commands.startEnd(
                () -> robot.setIntakePower(1.0),
                () -> robot.setIntakePower(0),
                Robot.INTAKE));
        manager.scheduler().addTrigger(intakeButton);

        // B pressed: score sequence.
        Trigger scoreButton = new Trigger(() -> gamepad1.b, manager.scheduler(), Clock.SYSTEM);
        scoreButton.onTrue(Commands.sequence(
                Commands.runOnce(() -> robot.openClaw(), Robot.CLAW),
                Commands.waitMs(500),
                Commands.runOnce(() -> robot.closeClaw(), Robot.CLAW)));
        manager.scheduler().addTrigger(scoreButton);
    }

    @Override
    protected void onLoop() {
        telemetry.addData("Commands", "drive default; A = intake, B = score");
    }

    @Override
    public void onStop() {
        super.onStop();
        robot.stopAll();
    }
}
