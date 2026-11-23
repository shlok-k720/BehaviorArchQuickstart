package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.filter.ExtendedKalmanFilter;
import com.rmsftc.utils.filter.Filter;
import com.rmsftc.utils.filter.FilterState;
import com.rmsftc.utils.filter.KalmanFilter;
import com.rmsftc.utils.math.Matrix;

/**
 * FTCUtils filter examples: the scalar {@link Filter} factories, a
 * constant-velocity {@link KalmanFilter}, and an
 * {@link ExtendedKalmanFilter} with a nonlinear measurement.
 *
 * <p>{@link FilterState} wraps a stateless filter and remembers the last
 * output for you, so {@code update(input)} is all a loop needs.
 */
public final class FilterExample {

    private FilterExample() {
    }

    /** Smoothed joystick input: low-pass + deadband-style shaping. */
    public static FilterState stickSmoother() {
        return new FilterState(Filter.lowPassFilter(0.35));
    }

    /** Moving average over the last 5 samples (e.g. a noisy distance sensor). */
    public static FilterState sensorAverage() {
        return new FilterState(Filter.movingAverageFilter(5));
    }

    /**
     * Constant-velocity Kalman filter over [position, velocity], measuring
     * position only. Call {@code predict()} then {@code update(measurement)}
     * each loop; {@code getState()[0]} is the filtered position.
     * (Assumes a roughly constant loop period baked into F.)
     */
    public static KalmanFilter positionTracker(double dtSeconds) {
        Matrix f = new Matrix(new double[][]{{1.0, dtSeconds}, {0.0, 1.0}});
        Matrix h = new Matrix(new double[][]{{1.0, 0.0}});
        Matrix q = Matrix.diagonal(0.01, 0.1);   // process noise
        Matrix r = Matrix.diagonal(0.5);          // measurement noise
        return new KalmanFilter(f, h, q, r, new double[]{0.0, 0.0}, Matrix.identity(2));
    }

    /**
     * EKF for a 1-D state whose measurement is the state's square
     * (h(x) = x^2, dh/dx = 2x) — the shape of problem a linear Kalman
     * filter cannot express. Process: x' = x + u.
     */
    public static ExtendedKalmanFilter squaredMeasurementEkf() {
        return new ExtendedKalmanFilter(
                (state, control) -> new double[]{state[0] + control[0]},
                state -> new double[]{state[0] * state[0]},
                (state, control) -> Matrix.identity(1),
                state -> new Matrix(new double[][]{{2.0 * state[0]}}),
                Matrix.diagonal(0.01),
                Matrix.diagonal(0.1),
                new double[]{1.0},
                Matrix.identity(1));
    }

    public static String demo() {
        FilterState smooth = stickSmoother();
        double s = 0;
        for (double input : new double[]{0.0, 1.0, 1.0, 1.0}) {
            s = smooth.update(input);
        }

        KalmanFilter tracker = positionTracker(0.02);
        for (double measurement : new double[]{0.0, 0.2, 0.45, 0.7}) {
            tracker.predict();
            tracker.update(new double[]{measurement});
        }

        ExtendedKalmanFilter ekf = squaredMeasurementEkf();
        ekf.predict(new double[]{0.1});
        ekf.update(new double[]{1.3});

        return String.format("lowpass=%.2f, kalman pos=%.2f vel=%.2f, ekf x=%.2f",
                s, tracker.getState()[0], tracker.getState()[1], ekf.getState()[0]);
    }
}
