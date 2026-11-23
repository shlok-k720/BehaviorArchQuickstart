package org.firstinspires.ftc.teamcode.kotlin.ftcutils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.rmsftc.utils.Clock
import com.rmsftc.utils.math.MathUtils
import com.rmsftc.utils.math.Statistics
import com.rmsftc.utils.math.units.NumericalValue
import com.rmsftc.utils.math.units.basic.AngleUnit
import com.rmsftc.utils.math.units.basic.TimeUnit
import org.firstinspires.ftc.teamcode.ftcutils.RobotConfig
import org.firstinspires.ftc.teamcode.robot.Robot

/**
 * FTCUtils from Kotlin: the same library the Java examples use, in
 * Kotlin syntax. Highlights:
 *
 * - `Clock.Helper` delta time (the pattern used everywhere here).
 * - Unit-tagged `NumericalValue` conversions.
 * - `MathUtils` / `Statistics` (Kotlin objects in FTCUtils, called
 *   directly).
 * - The Java `RobotConfig` config class — Kotlin and Java share the
 *   same `Var`s, so a config written in either language works in both.
 */
@TeleOp(name = "Kotlin FTCUtils: Tour", group = "BehaviorArchitectures")
class FtcUtilsKt : LinearOpMode() {

    private val robot = Robot()
    private val config = RobotConfig()
    private val timer = Clock.Helper(Clock.SYSTEM)

    override fun runOpMode() {
        robot.init(hardwareMap)

        val rightAngle = NumericalValue(90.0, AngleUnit.DEGREE)
        val radians = rightAngle.convertTo(AngleUnit.RADIAN).value
        val matchLength = NumericalValue.of(2.5, TimeUnit.MINUTE)
            .add(NumericalValue.of(30.0, TimeUnit.SECOND))

        val samples = doubleArrayOf(1.0, 1.2, 0.9, 1.1, 5.0)

        waitForStart()
        timer.update()
        while (opModeIsActive()) {
            timer.update()

            // Drive at the configured speed, with a deadband on the stick.
            val forward = MathUtils.deadband(-gamepad1.left_stick_y.toDouble(), 0.05) *
                config.driveSpeed.get()
            robot.drive(forward, gamepad1.left_stick_x.toDouble(), gamepad1.right_stick_x.toDouble())

            telemetry.addData("dt (ms)", timer.dtSeconds() * 1000.0)
            telemetry.addData("90 deg in rad", radians)
            telemetry.addData("Match length", matchLength.toString())
            telemetry.addData("Sample mean/median",
                "%.2f / %.2f".format(Statistics.mean(samples), Statistics.median(samples)))
            telemetry.update()
        }
        robot.stopAll()
    }
}
