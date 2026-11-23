package org.firstinspires.ftc.teamcode.trees;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.rmsftc.bttree.BehaviorNode;
import com.rmsftc.bttree.NodeStatus;
import com.rmsftc.utils.blackboard.Blackboard;

import org.firstinspires.ftc.teamcode.robot.Robot;

/**
 * A custom behavior-tree leaf node: drive in a direction for a fixed time.
 *
 * <p>Writing your own node means subclassing {@link BehaviorNode} and
 * overriding three hooks:
 * <ul>
 *   <li>{@code onStart()} — runs once when the node becomes active.</li>
 *   <li>{@code onTick()} — runs every tick while active; return
 *       {@link NodeStatus#RUNNING} to stay active, or
 *       {@link NodeStatus#SUCCESS}/{@link NodeStatus#FAILURE} to finish.</li>
 *   <li>{@code onEnd()} — runs once when the node finishes or is aborted.</li>
 * </ul>
 * The {@code blackboard} passed to the constructor is the tree's shared
 * blackboard, so nodes can read/write values other nodes set.
 */
public class DriveForTimeNode extends BehaviorNode {

    private final Robot robot;
    private final double forward;
    private final double strafe;
    private final double turn;
    private final double seconds;

    private final ElapsedTime timer = new ElapsedTime();

    public DriveForTimeNode(String name, Blackboard blackboard, Robot robot,
                            double forward, double strafe, double turn, double seconds) {
        super(name, blackboard);
        this.robot = robot;
        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
        this.seconds = seconds;
    }

    @Override
    protected void onStart() {
        timer.reset();
    }

    @Override
    protected NodeStatus onTick() {
        if (timer.seconds() >= seconds) {
            return NodeStatus.SUCCESS;
        }
        robot.drive(forward, strafe, turn);
        return NodeStatus.RUNNING;
    }

    @Override
    protected void onEnd() {
        robot.stopDrive();
    }
}
