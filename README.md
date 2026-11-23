# BehaviorArchitectures Quickstart

A ready-to-run FTC project showing how to use
[**BehaviorArchitectures**](https://github.com/shlok-k720/BehaviorArchitectures) —
a library that gives your robot code three composable ways to structure
behavior:

- **Behavior trees** — reactive, priority-ordered decision making
  ("if this, else that", re-evaluated every tick).
- **Commands** — WPILib-style command-based programming with a scheduler,
  requirement tokens, triggers, and command groups.
- **State machines** — explicit states with transitions, for routines that
  are naturally "do this, then that, then the other thing".

All three run through one `ArchitectureManager` with one shared blackboard,
and they convert into each other: a command can run as a state-machine
state, a tree can run as a command, and so on. This quickstart is based on
the official FIRST Tech Challenge SDK quickstart for the BIOBUZZ
(2026–2027) season, with the library wired in and examples in `TeamCode`.

## Getting started

1. Clone this repository and open it in Android Studio (Narwhal 3 or later).
2. Let Gradle sync. The BehaviorArchitectures dependency is already
   declared in `TeamCode/build.gradle`:

   ```groovy
   repositories {
       maven { url 'https://jitpack.io' }
   }

   dependencies {
       implementation 'com.github.shlok-k720.BehaviorArchitectures:ftc:v0.2.2'
   }
   ```

3. Create a robot configuration with these device names (the examples
   expect exactly these):

   | Name        | Type  | What it is    |
   |-------------|-------|---------------|
   | `leftFront` | Motor | Drive motor   |
   | `rightFront`| Motor | Drive motor   |
   | `leftBack`  | Motor | Drive motor   |
   | `rightBack` | Motor | Drive motor   |
   | `intake`    | Motor | Intake motor  |
   | `claw`      | Servo | Claw servo    |

4. Deploy to the Robot Controller and pick an OpMode from the
   **BehaviorArchitectures** group.

## The examples

Every example drives the same simple robot (`robot/Robot.java`: mecanum
drive + intake + claw), so you can compare the same behavior written three
ways.

### `org.firstinspires.ftc.teamcode.robot`

- **`Robot.java`** — hardware wrapper shared by all examples. Also defines
  the requirement tokens (`DRIVE`, `INTAKE`, `CLAW`) the command examples
  use.

### `org.firstinspires.ftc.teamcode.trees` — behavior trees

- **`TreeTeleOp`** — a reactive selector: while **A** is held the intake
  runs, otherwise the robot drives from the sticks. Shows conditions,
  actions, and how a reactive tree re-decides every tick.
- **`TreeAuto`** — a sequence: drive forward, intake for 2 s (a `waitMs`
  timeout decorator), drive back, open the claw.
- **`DriveForTimeNode`** — writing your own leaf node by subclassing
  `BehaviorNode` (`onStart` / `onTick` / `onEnd`).

### `org.firstinspires.ftc.teamcode.commands` — command-based

- **`CommandTeleOp`** — a default drive command plus gamepad triggers:
  **A** held runs the intake (`whileTrue`), **B** runs a score sequence
  (`onTrue`).
- **`CommandAuto`** — a sequential routine built with
  `manager.commands(...)`; timed drives use `race` against a wait.
- **`DriveCommand`** — writing your own command by subclassing `Command`
  (`start` / `loop` / `status` / `end`), with requirement tokens.

### `org.firstinspires.ftc.teamcode.fsm` — state machines

- **`FsmTeleOp`** — `IDLE` / `INTAKE` / `SCORE` states with button-driven
  transitions.
- **`FsmAuto`** — an ordered routine: timed drive cases, an intake case,
  an instant score case.
- **`DriveCase`** and **`IntakeCase`** — writing your own cases by
  subclassing `Case`, using the machine clock (`nowNanos()`).

### `org.firstinspires.ftc.teamcode.mixed` — all three together

- **`MixedAuto`** — one state machine whose states are a plain case, a
  wrapped **command** (`ArchitectureConverter.toCase`), and a wrapped
  **behavior tree** (`toCase` again). This is the composition the library
  is built around. Note the rule: converted things run *inline* inside
  whatever wraps them, so drive each mechanism from exactly one place at
  a time.

## Using BehaviorArchitectures in your own project

Add the JitPack repository and the `ftc` artifact (it brings in the
`behaviorTrees`, `cmdlib`, and `fsm` modules, plus
[FTCUtils](https://github.com/shlok-k720/FTCUtils)):

```groovy
// build.gradle (TeamCode)
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.shlok-k720.BehaviorArchitectures:ftc:v0.2.2'
}
```

```kotlin
// build.gradle.kts (TeamCode)
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.shlok-k720.BehaviorArchitectures:ftc:v0.2.2")
}
```

Then extend `ArchitecturalOpMode` instead of `LinearOpMode`. The lifecycle
is fixed — `runOpMode()` is final — so you fill in hooks:

```java
@Autonomous(name = "My Auto")
public class MyAuto extends ArchitecturalOpMode {

    @Override
    protected void onInit() {
        // hardware setup, once
    }

    @Override
    protected void build(ArchitectureManager manager) {
        // build trees, state machines, and commands here;
        // they start running on the first loop tick
    }

    @Override
    protected void onLoop() {
        // optional extra work each loop (telemetry updates for you)
    }
}
```

Java and Kotlin are both fully supported: every builder has a Java-facing
form (used throughout this quickstart) and a Kotlin DSL form.

## Which architecture when?

- **State machine** — the routine is a fixed sequence of steps with clear
  transitions (most autonomous routines, mechanism cycles like
  intake → transfer → score).
- **Commands** — driver control with lots of button bindings, default
  commands per mechanism, and interruptible actions (most TeleOps).
- **Behavior tree** — behavior is a priority list that must react
  continuously ("if we have a game piece, do X; else if the intake is
  free, do Y; else drive").

And when one routine needs two styles, convert instead of rewriting:
`ArchitectureConverter` wraps commands as cases, trees as commands, state
machines as tree nodes, and more.

## Links

- [BehaviorArchitectures](https://github.com/shlok-k720/BehaviorArchitectures) — the library
- [FTCUtils](https://github.com/shlok-k720/FTCUtils) — utilities the library builds on (blackboard, clocks, logging)
- [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) — the upstream quickstart this project is based on
