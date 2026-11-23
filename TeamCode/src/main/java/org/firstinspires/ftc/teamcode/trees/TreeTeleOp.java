package org.firstinspires.ftc.teamcode.trees;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rmsftc.bttree.NodeStatus;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * Behavior-tree TeleOp example.
 *
 * <p>The tree is a <em>reactive selector</em>: every tick it re-checks its
 * children from the top and runs the first one that does not fail.
 * <ol>
 *   <li>While <b>A</b> is held, the intake runs (a condition gates an action).</li>
 *   <li>Otherwise, the robot drives from the gamepad sticks.</li>
 * </ol>
 * Because the selector is reactive, releasing A immediately falls back to
 * driving — no state bookkeeping needed. The claw is handled the plain way
 * in {@link #onLoop()} to show trees and ordinary loop code can coexist.
 */
@TeleOp(name = "Trees: TeleOp", group = "BehaviorArchitectures")
public class TreeTeleOp extends ArchitecturalOpMode {

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        manager.jTree(b -> b
                // Child 1: intake while A is held.
                .sequence("intake while A", s -> s
                        .condition("A pressed", () -> gamepad1.a)
                        .action("run intake", () -> {
                            robot.setIntakePower(1.0);
                            return NodeStatus.RUNNING;
                        }))
                // Child 2: always succeeds, so it is the fallback behavior.
                .action("drive from sticks", () -> {
                    robot.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                    return NodeStatus.RUNNING;
                }));
    }

    @Override
    protected void onLoop() {
        // Stop the intake whenever the tree is not running it.
        if (!gamepad1.a) {
            robot.setIntakePower(0);
        }
        if (gamepad1.b) {
            robot.openClaw();
        } else if (gamepad1.x) {
            robot.closeClaw();
        }
        telemetry.addData("Tree", "reactive selector: intake (A) else drive");
    }

    @Override
    public void onStop() {
        super.onStop();
        robot.stopAll();
    }
}
