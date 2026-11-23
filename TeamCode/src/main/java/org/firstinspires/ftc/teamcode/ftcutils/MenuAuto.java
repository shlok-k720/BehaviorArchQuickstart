package org.firstinspires.ftc.teamcode.ftcutils;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.rmsftc.utils.AllianceColor;
import com.rmsftc.utils.ftc.telemetry.GamepadMenu;
import com.rmsftc.utils.ftc.telemetry.TelemetryLogger;
import com.rmsftc.utils.ftc.utils.Storage;
import com.rmsftc.utils.ftc.utils.Value;
import com.rmsftc.utils.telemetry.selection.AnswerChoice;
import com.rmsftc.utils.telemetry.selection.Prompter;
import com.rmsftc.utils.telemetry.selection.Select;

import org.firstinspires.ftc.teamcode.robot.Robot;

import java.util.List;

/**
 * FTCUtils menu + storage showcase: pick options on the Driver Station
 * before the match, and persist them across OpMode restarts.
 *
 * <p>{@link Prompter} asks questions (built with {@link Select}'s short
 * factories); {@link GamepadMenu} drives it from the gamepad (dpad to
 * move, A to confirm — check the menu's own prompts on telemetry).
 * {@link Storage} saves the answers to the Robot Controller's
 * preferences with typed {@link Value} codecs, so the next run can
 * default to the last choice.
 */
@Autonomous(name = "FTCUtils: Menu + Storage", group = "BehaviorArchitectures")
public class MenuAuto extends LinearOpMode {

    private final Robot robot = new Robot();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        Storage storage = new Storage(hardwareMap);

        String lastAlliance = storage.get("alliance", Value.STRING, "Red");

        Prompter prompter = new Prompter(new TelemetryLogger(telemetry),
                Select.q("Alliance", Select.a(lastAlliance),
                        Select.a("Red"), Select.a("Blue")),
                Select.q("Start position", Select.a("Near"),
                        Select.a("Near"), Select.a("Far")));
        GamepadMenu menu = new GamepadMenu(prompter, gamepad1);

        // Menu phase: runs during init, before waitForStart.
        while (!isStarted() && !isStopRequested() && !menu.isFinished()) {
            menu.update();
            telemetry.update();
        }

        List<AnswerChoice> answers = menu.getAnswers();
        String allianceName = answers.size() > 0 ? answers.get(0).name : lastAlliance;
        AllianceColor alliance = "Blue".equals(allianceName) ? AllianceColor.BLUE : AllianceColor.RED;
        storage.set("alliance", Value.STRING, allianceName);

        telemetry.addData("Alliance", alliance);
        telemetry.addData("Start", answers.size() > 1 ? answers.get(1).name : "?");
        telemetry.update();

        waitForStart();
        // A real auto would branch on the answers here; this example just
        // nudges forward to prove the OpMode ran.
        robot.drive(0.3, 0, 0);
        sleep(500);
        robot.stopAll();
    }
}
