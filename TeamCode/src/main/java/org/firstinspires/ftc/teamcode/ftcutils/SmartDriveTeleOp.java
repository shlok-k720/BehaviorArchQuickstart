package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.rmsftc.utils.AllianceColor;
import com.rmsftc.utils.Clock;
import com.rmsftc.utils.DriveVector;
import com.rmsftc.utils.control.LimiterConfig;
import com.rmsftc.utils.ftc.hardware.SmartMotor;
import com.rmsftc.utils.ftc.hardware.drivetrain.MecanumDrive;

/**
 * FTCUtils hardware showcase: {@link SmartMotor} (rate-limited motors)
 * and {@link MecanumDrive} (mecanum mixing + field-centric), driven with
 * {@link DriveVector} and timed with {@link Clock.Helper}.
 *
 * <p>Nothing reaches the motors when you call {@code drive(...)} — it
 * only sets targets. {@code update(dtSeconds)} pushes them through each
 * motor's limiter once per loop, which is why the loop needs a real dt.
 * Hold <b>left bumper</b> for field-centric driving (heading fixed at
 * 0 here; wire it to your IMU heading in real code, offset by
 * {@link AllianceColor#fieldCentricOffset()} per alliance).
 */
@TeleOp(name = "FTCUtils: SmartDrive", group = "BehaviorArchitectures")
public class SmartDriveTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        LimiterConfig limiter = new LimiterConfig();
        limiter.maxRate.set(4.0); // full swing in 0.25 s

        SmartMotor frontLeft = smart("leftFront", limiter);
        SmartMotor frontRight = smart("rightFront", limiter);
        SmartMotor backLeft = smart("leftBack", limiter);
        SmartMotor backRight = smart("rightBack", limiter);
        MecanumDrive drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);

        Clock.Helper timer = new Clock.Helper(Clock.SYSTEM);
        double headingRadians = 0.0; // replace with IMU heading

        waitForStart();
        timer.update();
        while (opModeIsActive()) {
            timer.update();

            DriveVector input = new DriveVector(
                    -gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            if (gamepad1.left_bumper) {
                drive.driveFieldCentric(input.forward(), input.strafe(), input.turn(), headingRadians);
            } else {
                drive.drive(input);
            }
            drive.update(timer.dtSeconds());

            telemetry.addData("Wheel powers", java.util.Arrays.toString(drive.getWheelPowers()));
            telemetry.update();
        }
        drive.stop();
    }

    private SmartMotor smart(String name, LimiterConfig limiter) {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, name);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        return new SmartMotor(motor, limiter);
    }
}
