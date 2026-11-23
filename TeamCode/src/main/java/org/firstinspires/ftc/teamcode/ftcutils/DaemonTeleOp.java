package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.cmdlib.cmd.Commands;
import com.rmsftc.ftc.ArchitectureDaemon;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.utils.ftc.DaemonManager;
import com.rmsftc.utils.ftc.daemons.ClockManager;
import com.rmsftc.utils.ftc.daemons.FTCDaemon;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * FTCUtils daemon showcase.
 *
 * <p>A <b>daemon</b> is a static, zero-arg, void method annotated with
 * {@link FTCDaemon}. At app start, FTCUtils' Sinister scan finds every
 * {@code @FTCDaemon} method (in your code and in libraries) and registers
 * it with {@link DaemonManager}. Registration only <em>collects</em>
 * daemons — your loop runs them, one line:
 * {@code DaemonManager.unmodifiable().forEach(d -> d.daemon.execute())}.
 *
 * <p>Three daemons run here:
 * <ul>
 *   <li>{@link ClockManager}'s built-in daemon — snapshots the clock and
 *       computes dt every loop, readable anywhere via
 *       {@code ClockManager.getDtSeconds()} (the daemon alternative to
 *       holding a {@code Clock.Helper} yourself).</li>
 *   <li>{@code loopCounterDaemon} below — your own daemon: counts loops
 *       and tracks the slowest dt seen.</li>
 *   <li>{@link ArchitectureDaemon} — registering the manager makes the
 *       daemon loop tick all architectures, so this OpMode never calls
 *       {@code tickAll()} itself. The scheduled drive command below runs
 *       purely because the daemon ticks it.</li>
 * </ul>
 * This is a plain {@code LinearOpMode} (not {@code ArchitecturalOpMode})
 * because the whole point is driving the loop manually via daemons.
 */
@TeleOp(name = "FTCUtils: Daemons", group = "BehaviorArchitectures")
public class DaemonTeleOp extends LinearOpMode {

    private final Robot robot = new Robot();
    private final ArchitectureManager manager = new ArchitectureManager();

    private static long loopCount;
    private static double slowestDtSeconds;

    /** Your own daemon: runs once per loop, alongside the library daemons. */
    @FTCDaemon(name = "loop-stats")
    public static void loopCounterDaemon() {
        loopCount++;
        slowestDtSeconds = Math.max(slowestDtSeconds, ClockManager.getDtSeconds());
    }

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        loopCount = 0;
        slowestDtSeconds = 0;

        // A drive command on the manager; the ArchitectureDaemon will tick it.
        manager.schedule(Commands.run(() ->
                robot.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x),
                Robot.DRIVE));
        ArchitectureDaemon.register(manager);
        manager.startAll();

        telemetry.addData("Daemons registered", DaemonManager.unmodifiable().size());
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            // Run every registered daemon: ClockManager, loop-stats,
            // architecture-tick (which ticks the manager), and any others.
            DaemonManager.unmodifiable().forEach(d -> d.daemon.execute());

            telemetry.addData("Loops", loopCount);
            telemetry.addData("dt (ms)", ClockManager.getDtSeconds() * 1000.0);
            telemetry.addData("Slowest dt (ms)", slowestDtSeconds * 1000.0);
            telemetry.update();
        }

        ArchitectureDaemon.unregister();
        manager.invalidateAll();
        robot.stopAll();
    }
}
