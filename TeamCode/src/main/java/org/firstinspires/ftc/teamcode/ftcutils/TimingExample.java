package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.Clock;
import com.rmsftc.utils.math.units.NumericalValue;
import com.rmsftc.utils.math.units.basic.AngleUnit;
import com.rmsftc.utils.math.units.basic.DistanceUnit;
import com.rmsftc.utils.math.units.basic.TimeUnit;

/**
 * FTCUtils timing and units examples.
 *
 * <p><b>Delta time</b> — the pattern used across this quickstart: keep a
 * {@link Clock.Helper}, call {@code update()} once per loop, and read
 * {@code dtSeconds()} (the first update reports zero). This replaces
 * hand-rolled {@code System.nanoTime()} subtraction everywhere.
 *
 * <p><b>Units</b> — {@link NumericalValue} tags a number with its unit so
 * conversions are explicit and type-checked: you cannot accidentally add
 * degrees to radians or inches to meters without converting first.
 */
public final class TimingExample {

    private TimingExample() {
    }

    /** One loop of a timed routine, the Clock.Helper way. */
    public static class TimedRoutine {
        private final Clock.Helper timer = new Clock.Helper(Clock.SYSTEM);
        private double elapsedSeconds;

        public void start() {
            elapsedSeconds = 0;
            timer.update();
        }

        /** Returns true once {@code seconds} have elapsed. */
        public boolean tick(double seconds) {
            timer.update();
            elapsedSeconds += timer.dtSeconds();
            return elapsedSeconds >= seconds;
        }

        public double elapsedSeconds() {
            return elapsedSeconds;
        }
    }

    /** Unit conversions with NumericalValue. */
    public static String unitDemo() {
        NumericalValue<AngleUnit> rightAngle = new NumericalValue<>(90.0, AngleUnit.DEGREE);
        double radians = rightAngle.convertTo(AngleUnit.RADIAN).getValue();

        NumericalValue<DistanceUnit> tile = new NumericalValue<>(24.0, DistanceUnit.INCH);
        double meters = tile.convertTo(DistanceUnit.METER).getValue();

        NumericalValue<TimeUnit> match = NumericalValue.of(2.5, TimeUnit.MINUTE);
        double seconds = match.convertTo(TimeUnit.SECOND).getValue();

        // Same-unit arithmetic keeps the tag; mixed units must convert first.
        NumericalValue<TimeUnit> total = match.add(NumericalValue.of(30.0, TimeUnit.SECOND));

        return String.format("90deg=%.3frad, 24in=%.3fm, 2.5min=%.0fs, +30s=%s",
                radians, meters, seconds, total);
    }
}
