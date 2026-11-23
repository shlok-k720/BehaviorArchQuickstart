package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * A minimal robot used by every example in this quickstart: a mecanum drive,
 * one intake motor, and one claw servo.
 *
 * <p>Hardware configuration names (create these in the Robot Controller app
 * before running the examples):
 * <ul>
 *   <li>{@code leftFront}, {@code rightFront}, {@code leftBack},
 *       {@code rightBack} — drive motors</li>
 *   <li>{@code intake} — intake motor</li>
 *   <li>{@code claw} — claw servo</li>
 * </ul>
 *
 * <p>The architecture packages (trees, commands, fsm) all drive this same
 * robot, so you can compare how the same behavior looks in each style.
 */
public class Robot {

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;
    private DcMotor intake;
    private Servo claw;

    /** Requirement tokens for the command examples: one per mechanism. */
    public static final Object DRIVE = new Object();
    public static final Object INTAKE = new Object();
    public static final Object CLAW = new Object();

    public void init(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        intake = hardwareMap.get(DcMotor.class, "intake");
        claw = hardwareMap.get(Servo.class, "claw");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Mecanum drive: {@code forward} and {@code strafe} in [-1, 1] (robot
     * frame), {@code turn} in [-1, 1]. Powers are normalized so no wheel
     * exceeds 1.
     */
    public void drive(double forward, double strafe, double turn) {
        double lf = forward + strafe + turn;
        double rf = forward - strafe - turn;
        double lb = forward - strafe + turn;
        double rb = forward + strafe - turn;

        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(lf), Math.abs(rf)),
                Math.max(Math.abs(lb), Math.abs(rb))));

        leftFront.setPower(lf / max);
        rightFront.setPower(rf / max);
        leftBack.setPower(lb / max);
        rightBack.setPower(rb / max);
    }

    public void stopDrive() {
        drive(0, 0, 0);
    }

    public void setIntakePower(double power) {
        intake.setPower(power);
    }

    public void openClaw() {
        claw.setPosition(0.6);
    }

    public void closeClaw() {
        claw.setPosition(0.2);
    }

    /** Stops every motor. Call from {@code onStop()} overrides. */
    public void stopAll() {
        stopDrive();
        setIntakePower(0);
    }
}
