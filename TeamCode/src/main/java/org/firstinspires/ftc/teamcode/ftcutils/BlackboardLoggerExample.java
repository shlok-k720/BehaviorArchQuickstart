package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.utils.blackboard.Blackboard;
import com.rmsftc.utils.logger.Logger;
import com.rmsftc.utils.telemetry.html.Html;

import org.firstinspires.ftc.teamcode.robot.Robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * FTCUtils blackboard + logging example (plain LinearOpMode — these
 * utilities are architecture-independent).
 *
 * <p><b>Blackboard</b> — typed shared values with change callbacks. Note
 * the lookup keys are boxed types ({@code Double.class}, not
 * {@code double.class}); the architecture manager's blackboard is the
 * same class, so anything stored here is visible to trees, commands,
 * and cases.
 *
 * <p><b>Logger</b> — attach one or more {@code LogSystem} sinks and every
 * {@code Logger.log(caption, data)} reaches all of them. Here the sink
 * writes to telemetry; {@code Html} helpers build formatted strings for
 * HTML-capable sinks (see {@code TelemetryLogger.logHTML}).
 */
@TeleOp(name = "FTCUtils: Blackboard + Logger", group = "BehaviorArchitectures")
public class BlackboardLoggerExample extends LinearOpMode {

    private final Robot robot = new Robot();
    private final Blackboard blackboard = new Blackboard();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // Route every Logger event to telemetry.
        Logger.attachLogger((caption, data, timestampMs) ->
                telemetry.addData(caption, data));

        // React whenever the intake state changes on the blackboard.
        blackboard.registerCallback("intakeOn", (key, value) ->
                Logger.log("Blackboard", key + " -> " + value));

        waitForStart();
        while (opModeIsActive()) {
            boolean intakeOn = gamepad1.a;
            blackboard.set("intakeOn", intakeOn);
            blackboard.set("loopCount", blackboard.getOrDefault("loopCount", Integer.class, 0) + 1);

            robot.setIntakePower(Boolean.TRUE.equals(blackboard.get("intakeOn", Boolean.class)) ? 1.0 : 0.0);
            robot.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            telemetry.addData("Loops", blackboard.get("loopCount", Integer.class));
            telemetry.addData("HTML demo", Html.bold("bold") + " " + Html.italic("italic"));
            telemetry.update();
        }
        robot.stopAll();
    }
}
