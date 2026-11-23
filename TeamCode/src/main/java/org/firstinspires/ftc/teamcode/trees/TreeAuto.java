package org.firstinspires.ftc.teamcode.trees;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rmsftc.bttree.NodeStatus;
import com.rmsftc.ftc.ArchitectureManager;
import com.rmsftc.ftc.ArchitecturalOpMode;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * Behavior-tree autonomous example.
 *
 * <p>A <em>sequence</em> runs its children in order, moving on when each
 * succeeds and failing if any fails:
 * <ol>
 *   <li>Drive forward for 1 second (custom {@link DriveForTimeNode}).</li>
 *   <li>Run the intake for 2 seconds (action + a {@code waitMs} timeout
 *       decorator around it).</li>
 *   <li>Drive back and open the claw to score.</li>
 * </ol>
 * The tree root is a reactive selector (that is what
 * {@code manager.tree(...)} builds), so the sequence is its only child
 * here. When the sequence finishes, the tree reports SUCCESS and the
 * OpMode idles until the match clock stops it.
 */
@Autonomous(name = "Trees: Auto", group = "BehaviorArchitectures")
public class TreeAuto extends ArchitecturalOpMode {

    private final Robot robot = new Robot();

    @Override
    protected void onInit() {
        robot.init(hardwareMap);
        robot.closeClaw();
    }

    @Override
    protected void build(ArchitectureManager manager) {
        manager.jTree(b -> b
                .sequence("score routine", s -> s
                        .node(new DriveForTimeNode("drive forward", manager.blackboard(), robot,
                                0.5, 0, 0, 1.0))
                        // Run the intake until the 2 s timeout decorator ends it.
                        .waitMs("intake for 2s", 2000, w -> w
                                .action("intake on", () -> {
                                    robot.setIntakePower(1.0);
                                    return NodeStatus.RUNNING;
                                }))
                        .action("intake off", () -> {
                            robot.setIntakePower(0);
                            return NodeStatus.SUCCESS;
                        })
                        .node(new DriveForTimeNode("drive back", manager.blackboard(), robot,
                                -0.5, 0, 0, 1.0))
                        .action("open claw", () -> {
                            robot.openClaw();
                            return NodeStatus.SUCCESS;
                        })));
    }

    @Override
    public void onStop() {
        super.onStop();
        robot.stopAll();
    }
}
