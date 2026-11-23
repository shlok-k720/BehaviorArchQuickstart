package org.firstinspires.ftc.teamcode.ftcutils;

import com.rmsftc.utils.AllianceColor;
import com.rmsftc.utils.DriveVector;
import com.rmsftc.utils.math.Pose;
import com.rmsftc.utils.math.Vector;
import com.rmsftc.utils.math.Vector2D;

/**
 * FTCUtils geometry examples: the mutable Pedro geometry family and the
 * shared FTC enums.
 *
 * <p>{@link Pose} extends Pedro Pathing's Pose but keeps its own mutable
 * state — {@code setX}/{@code set}/{@code withX} and friends update the
 * pose in place (or return adjusted copies) instead of forcing you to
 * allocate a new pose per loop. {@link Vector} and {@link Vector2D} do
 * the same for vectors, and {@link DriveVector} is a drive command
 * (forward/strafe/turn, optionally with a heading).
 *
 * <p>Note: these classes extend Pedro Pathing types, so a project using
 * them needs Pedro on the classpath — this quickstart declares
 * {@code com.pedropathing:core} in TeamCode for exactly that reason.
 */
public final class GeometryExample {

    private GeometryExample() {
    }

    public static String demo() {
        Pose pose = new Pose(24.0, 36.0, Math.toRadians(90));
        pose.setX(30.0);                       // mutate in place
        Pose shifted = pose.withY(40.0);       // adjusted copy, original untouched

        Vector v = new Vector(1.0, 2.0, 3.0);
        v.set(0, 5.0);
        v.setAll(7.0, 8.0, 9.0);

        Vector2D dir = Vector2D.polar(2.0, Math.toRadians(45));
        Vector2D unit = Vector2D.unit(Math.toRadians(90));

        DriveVector drive = new DriveVector(0.6, 0.1, -0.2);

        double redOffset = AllianceColor.RED.fieldCentricOffset();

        return String.format("pose=(%.0f,%.0f), shifted y=%.0f, v0=%.0f, dir=(%.2f,%.2f), unit=(%.1f,%.1f), drive f=%.1f, red offset=%.0f",
                pose.x(), pose.y(), shifted.y(), v.get(0),
                dir.x(), dir.y(), unit.x(), unit.y(),
                drive.forward(), redOffset);
    }
}
