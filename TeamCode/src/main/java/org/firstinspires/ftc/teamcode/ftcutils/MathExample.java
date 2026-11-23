package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.math.MathUtils;
import com.rmsftc.utils.math.Matrix;
import com.rmsftc.utils.math.Statistics;

/**
 * FTCUtils math examples: {@link MathUtils} helpers, {@link Statistics},
 * and {@link Matrix} linear algebra (the same Matrix the Kalman filters
 * use).
 */
public final class MathExample {

    private MathExample() {
    }

    public static String mathUtilsDemo() {
        double clamped = MathUtils.clamp(1.7, -1.0, 1.0);          // 1.0
        double stick = MathUtils.deadband(0.04, 0.05);              // 0.0 (inside deadband)
        double shaped = MathUtils.signSquare(-0.5);                 // -0.25 (finer low-end control)
        double mapped = MathUtils.mapRange(0.25, 0.0, 1.0, -1.0, 1.0); // -0.5
        double err = MathUtils.angleErrorDegrees(350.0, 10.0);      // -20 (short way around)
        double wrapped = MathUtils.wrapAngleRadians(3.5 * Math.PI); // -PI/2
        double limited = MathUtils.rateLimit(0.0, 1.0, 0.2);        // 0.2 (one step toward target)

        return String.format("clamp=%.1f deadband=%.1f square=%.2f map=%.1f err=%.0f wrap=%.2f rate=%.1f",
                clamped, stick, shaped, mapped, err, wrapped, limited);
    }

    public static String statisticsDemo() {
        double[] samples = {1.0, 1.2, 0.9, 1.1, 5.0}; // last one is an outlier
        return String.format("mean=%.2f median=%.2f stddev=%.2f p90=%.2f",
                Statistics.mean(samples),
                Statistics.median(samples),
                Statistics.stddev(samples),
                Statistics.percentile(samples, 90.0));
    }

    public static String matrixDemo() {
        Matrix a = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix b = Matrix.identity(2);
        Matrix c = a.multiply(b).add(Matrix.diagonal(1, 1));
        double[] v = c.multiply(new double[]{1.0, 0.0});
        return String.format("A*I+diag(1,1) = [[%.0f,%.0f],[%.0f,%.0f]], times [1,0] = [%.0f,%.0f]",
                c.get(0, 0), c.get(0, 1), c.get(1, 0), c.get(1, 1), v[0], v[1]);
    }
}
