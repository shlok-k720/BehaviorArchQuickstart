package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.config.Configuration;
import com.rmsftc.utils.config.Configurator;
import com.rmsftc.utils.config.vars.Val;
import com.rmsftc.utils.config.vars.Var;
import com.rmsftc.utils.config.vars.Variables;

/**
 * FTCUtils config example: tunable values as {@link Var}s grouped in a
 * {@link Configuration}.
 *
 * <p>The pattern (the same one {@code PidConfig} and {@code LimiterConfig}
 * use):
 * <ul>
 *   <li>Declare {@code Var<T>} fields for every tunable.</li>
 *   <li>Assign them in {@link #defaults()} — <b>not</b> at declaration.
 *       {@code defaults()} runs from the superclass constructor, before
 *       field initializers, so initializer-assigned fields would still be
 *       null when {@code defaults()} executes.</li>
 *   <li>Read with {@code var.get()}, write with {@code var.set(...)}.
 *       Named vars ({@code new Var<>("driveSpeed", 0.8)}) register
 *       themselves in {@link Variables}, so tooling can list and edit
 *       every tunable in one place.</li>
 *   <li>{@link Val} is the read-only sibling: a constant, or a live
 *       computed value via a getter.</li>
 * </ul>
 * A {@link Configurator} passed to the constructor can override any
 * default after {@code defaults()} runs — that is how you make variants
 * of a config without subclassing.
 */
public class RobotConfig extends Configuration<RobotConfig> {

    public Var<Double> driveSpeed;
    public Var<Double> intakePower;
    public Var<Boolean> fieldCentric;

    /** Read-only derived value: intake power, clamped to [0, 1] when read. */
    public Val<Double> safeIntakePower;

    public RobotConfig() {
        this(config -> {});
    }

    public RobotConfig(Configurator<RobotConfig> configurator) {
        super(configurator);
    }

    @Override
    public void defaults() {
        driveSpeed = new Var<>("driveSpeed", 0.8);
        intakePower = new Var<>("intakePower", 1.0);
        fieldCentric = new Var<>("fieldCentric", false);
        safeIntakePower = new Val<>(() -> Math.max(0.0, Math.min(1.0, intakePower.get())));
    }
}
