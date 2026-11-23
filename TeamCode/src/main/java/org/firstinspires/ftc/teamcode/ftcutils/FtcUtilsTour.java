package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.utils.Clock;

/**
 * One runnable tour of the pure-JVM FTCUtils examples: config, timing and
 * units, math, control, filters, and geometry. Each line on telemetry
 * comes from the matching example class in this package — open those
 * files to see the API behind each result.
 */
@TeleOp(name = "FTCUtils: Tour", group = "BehaviorArchitectures")
public class FtcUtilsTour extends LinearOpMode {

    @Override
    public void runOpMode() {
        RobotConfig config = new RobotConfig();
        TimingExample.TimedRoutine routine = new TimingExample.TimedRoutine();

        waitForStart();
        routine.start();
        while (opModeIsActive()) {
            boolean done = routine.tick(5.0);

            telemetry.addData("Config", "driveSpeed=%.2f fieldCentric=%s safeIntake=%.2f",
                    config.driveSpeed.get(), config.fieldCentric.get(), config.safeIntakePower.get());
            telemetry.addData("Units", TimingExample.unitDemo());
            telemetry.addData("MathUtils", MathExample.mathUtilsDemo());
            telemetry.addData("Statistics", MathExample.statisticsDemo());
            telemetry.addData("Matrix", MathExample.matrixDemo());
            telemetry.addData("Control", ControlExample.demo());
            telemetry.addData("Filters", FilterExample.demo());
            telemetry.addData("Geometry", GeometryExample.demo());
            telemetry.addData("Clock now (s)", Clock.SYSTEM.nowSeconds());
            telemetry.addData("5s routine done", done);
            telemetry.update();
        }
    }
}
