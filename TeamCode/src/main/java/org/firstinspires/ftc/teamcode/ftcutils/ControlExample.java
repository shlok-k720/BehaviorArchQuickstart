package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.control.Controller;
import com.rmsftc.utils.control.Limiter;
import com.rmsftc.utils.control.LimiterConfig;
import com.rmsftc.utils.control.PidConfig;
import com.rmsftc.utils.control.PiecewiseController;

/**
 * FTCUtils control examples: PIDF controllers, rate limiters, and
 * piecewise (gain-scheduled) controllers.
 *
 * <p>Controllers are composable values: {@code evaluate(setpoint, input)}
 * returns the output, and controllers combine with arithmetic
 * ({@code add}, {@code multiply}, ...). {@link PidConfig} holds the gains
 * as {@code Var}s (see {@link RobotConfig}) and builds a controller that
 * reads them live, so tuning changes apply without rebuilding.
 */
public final class ControlExample {

    private ControlExample() {
    }

    /** A PIDF controller from a config; gains are read live on each call. */
    public static Controller shooterPidf() {
        PidConfig config = new PidConfig();
        config.p.set(0.002);
        config.i.set(0.0001);
        config.d.set(0.00005);
        config.f.set(0.00018); // feedforward per RPM of setpoint
        return config.controller();
    }

    /** Plain PID without a config object. */
    public static Controller armPid() {
        return Controller.pid(0.05, 0.001, 0.002);
    }

    /**
     * Gain scheduling: gentle gains near the target, aggressive gains far
     * away. The baseline applies below the first threshold.
     */
    public static Controller scheduledPid() {
        return new PiecewiseController(Controller.pid(0.02, 0.0, 0.001))
                .put(50.0, Controller.pid(0.08, 0.0, 0.002));
    }

    /** A slew-rate limiter for motor powers (max change per second). */
    public static Limiter driveLimiter() {
        LimiterConfig config = new LimiterConfig();
        config.maxRate.set(3.0);   // full swing in ~0.33 s
        config.minOutput.set(-1.0);
        config.maxOutput.set(1.0);
        return config.limiter();
    }

    public static String demo() {
        Controller pidf = shooterPidf();
        double power = pidf.evaluate(3000.0, 2800.0); // target 3000 RPM, at 2800

        Limiter limiter = driveLimiter();
        double first = limiter.limit(1.0, 0.02);  // one 20 ms step toward 1.0
        double second = limiter.limit(1.0, 0.02);

        return String.format("pidf(3000,2800)=%.3f, limiter steps: %.3f -> %.3f", power, first, second);
    }
}
