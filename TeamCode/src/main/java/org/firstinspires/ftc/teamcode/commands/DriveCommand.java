package org.firstinspires.ftc.teamcode.commands;

import com.rmsftc.cmdlib.EndCondition;
import com.rmsftc.cmdlib.Status;
import com.rmsftc.cmdlib.cmd.Command;

import org.firstinspires.ftc.teamcode.robot.Robot;

import java.util.function.DoubleSupplier;

/**
 * A custom command: drive from three suppliers until interrupted.
 *
 * <p>Writing your own command means subclassing {@link Command} and
 * implementing four methods:
 * <ul>
 *   <li>{@code start()} — runs once when the command is scheduled.</li>
 *   <li>{@code loop()} — runs every scheduler tick while running.</li>
 *   <li>{@code status()} — return {@link Status#FINISHED} when done, or
 *       {@link Status#RUNNING} to keep going. This one never finishes on
 *       its own; it runs until another command needs the drive.</li>
 *   <li>{@code end(EndCondition)} — runs once when the command finishes or
 *       is interrupted; {@code endCondition} says which.</li>
 * </ul>
 * <p>{@code addRequirements(...)} declares which mechanisms this command
 * owns. The scheduler uses requirement tokens to decide conflicts: when a
 * new command is scheduled, any running command sharing a token is
 * interrupted (or the new one is blocked, per its interruption behavior).
 */
public class DriveCommand extends Command {

    private final Robot robot;
    private final DoubleSupplier forward;
    private final DoubleSupplier strafe;
    private final DoubleSupplier turn;

    public DriveCommand(Robot robot, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn) {
        this.robot = robot;
        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
        addRequirements(Robot.DRIVE);
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        robot.drive(forward.getAsDouble(), strafe.getAsDouble(), turn.getAsDouble());
    }

    @Override
    public Status status() {
        return Status.RUNNING;
    }

    @Override
    public void end(EndCondition endCondition) {
        robot.stopDrive();
    }
}
