package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.bttree.Captions;
import com.rmsftc.bttree.NodeStatus;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.Trees;
import com.rmsftc.utils.logger.LogSystem;
import com.rmsftc.utils.logger.Logger;
import com.rmsftc.utils.telemetry.MultipleLogger;
import com.rmsftc.utils.telemetry.StringLogger;
import com.rmsftc.utils.telemetry.Strings;
import com.rmsftc.utils.telemetry.SystemLogger;
import com.rmsftc.utils.telemetry.html.Html;
import com.rmsftc.utils.telemetry.html.HtmlLogger;
import com.rmsftc.utils.ftc.telemetry.TelemetryLogger;

import org.firstinspires.ftc.teamcode.robot.Robot;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The full logging tour: every logging surface in FTCUtils and
 * BehaviorArchitectures, in one OpMode.
 *
 * <p><b>1. The core log bus</b> ({@code com.rmsftc.utils.logger}) — code
 * anywhere calls {@code Logger.log(caption, data)}; every attached
 * {@link LogSystem} sink receives the event with a timestamp. Behavior
 * trees, state machines, and commands log their lifecycle events here
 * (captions in {@link Captions} and its fsm/cmdlib siblings), and with
 * nothing attached logging is a no-op, so hot paths can log freely.
 *
 * <p><b>2. Sinks</b> — a {@code LogSystem} is just a lambda
 * {@code (caption, data, timestampMs) -> ...}. BehaviorArchitectures
 * ships one ready-made: {@code manager.telemetryLogger(telemetry)}
 * routes every architecture event to telemetry. Detach with
 * {@code Logger.removeLogger(...)}, clear all with {@code Logger.reset()}.
 *
 * <p><b>3. Telemetry-module loggers</b> ({@code com.rmsftc.utils.telemetry})
 * — a separate, simpler {@code Logger} interface ({@code log(String)}):
 * {@link SystemLogger} prints to stdout, {@link StringLogger} collects
 * lines you can read back, {@link MultipleLogger} fans out to several.
 * {@link Strings} has small formatting helpers.
 *
 * <p><b>4. HTML</b> — {@link Html} builds markup ({@code bold},
 * {@code list}, ...); {@code AdvancedLogger} sinks render it:
 * {@link TelemetryLogger} writes to FTC telemetry (FTC Dashboard renders
 * the HTML), {@link HtmlLogger} collects it as a string.
 */
@TeleOp(name = "FTCUtils: Logging", group = "BehaviorArchitectures")
public class LoggingExample extends LinearOpMode {

    private final Robot robot = new Robot();
    private final ArchitectureManager manager = new ArchitectureManager();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // --- 1+2: core bus with two sinks ---
        AtomicInteger eventCount = new AtomicInteger();
        LogSystem countingSink = (caption, data, timestampMs) -> eventCount.incrementAndGet();
        Logger.attachLogger(countingSink);
        // BehaviorArchitectures' ready-made telemetry sink (keep the handle to detach).
        LogSystem telemetrySink = manager.telemetryLogger(telemetry);

        // A tiny tree so real NODE_* caption events flow through the bus.
        manager.jTree(t -> t
                .sequence("demo", s -> s
                        .action("drive", () -> {
                            robot.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                            return NodeStatus.RUNNING;
                        })));

        // Your own events use the same bus; reuse library captions or your own.
        Logger.log(Captions.NODE_STARTED, "LoggingExample initialized");
        Logger.log("MyRobot", "custom caption, custom data");

        // --- 3: telemetry-module loggers ---
        StringLogger collected = new StringLogger();
        com.rmsftc.utils.telemetry.Logger fanOut =
                MultipleLogger.of(new SystemLogger(), collected);
        fanOut.log("hello from MultipleLogger");
        fanOut.log(Strings.rowOfDashes());

        // --- 4: HTML ---
        TelemetryLogger telemetryLogger = new TelemetryLogger(telemetry);
        telemetryLogger.logBold("LoggingExample ready");
        telemetryLogger.logHTML(Html.list("core bus", "telemetry loggers", "HTML"));
        HtmlLogger htmlCollected = new HtmlLogger();
        htmlCollected.logHTML(Html.line(Html.bold("collected HTML")));

        waitForStart();
        while (opModeIsActive()) {
            manager.tickAll(); // tree events log through the bus each tick

            telemetry.addData("Bus events seen", eventCount.get());
            telemetry.addData("StringLogger lines", collected.getLines().size());
            telemetry.addData("HtmlLogger output", htmlCollected.toString());
            telemetry.update();
        }

        // Clean up: detach sinks so the next OpMode starts quiet.
        Logger.removeLogger(countingSink);
        Logger.removeLogger(telemetrySink);
        Logger.reset();
        manager.invalidateAll();
        robot.stopAll();
    }
}
