package org.firstinspires.ftc.teamcode.opmodes.comp;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.customclasses.Clock;
import org.firstinspires.ftc.teamcode.customclasses.CustomOpMode;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.MissingHardware;
import org.firstinspires.ftc.teamcode.customclasses.webcam.Webcam;
import org.firstinspires.ftc.teamcode.customclasses.webcam.ComplicatedPosPipeline;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

import java.util.ArrayList;
import java.util.Arrays;

@Autonomous(name="BlueFarSide")
public class BlueFarSide extends CustomOpMode {
    //Roadrunner Stuff
    // Conventions to Follow : the back of the field is the side with the scoring boards, front is the other side with the big apriltags
    // Remember that the when centered to field and heading is 0 then the robot is facing the right because the heading 0 is to the right on a unit circle

    private ArrayList<TrajectorySequence> trajectoriesToFollow = null;
    private boolean onBiasDone = false;
    private int timesTuned = 0;

    // Mechanisms || Webcams || Timers
    private Clock clock = null;
    //private final PixelTiltOuttake pixelTiltOuttake = null;
    //private final LinearSlides linearSlides = null;
    private Webcam webcam = null;

    // Miss
    private int trajectoryIndex = 0;
    private int autoVersion = 0;
    private double MaxBiasFixingTime = 10.0; // How much maximum time it should take to tune the bias. in seconds
    private boolean tuningBias = true;

    public void init() {
        super.init();
        telemetry.setMsTransmissionInterval(0);
        //pixelTiltOuttake = new PixelTiltOuttake(hardwareMap);
        //linearSlides = new LinearSlides(hardwareMap, telemetry);
        webcam = new Webcam(hardwareMap);
        webcam.UseCustomPipeline(new ComplicatedPosPipeline("Blue"));


        clock = new Clock();
        MissingHardware.printMissing(telemetry);
        sleep(1000);

    }

    private void tuneBias() {
        // Assume that the field is empty
        // First step is to get the process the current frame
        if (webcam.pipeline.tuneBias()) {
            timesTuned++;
        }
        webcam.pipeline.PrintTelemetry(telemetry);
        telemetry.addLine("Tuning... ");
    }

    protected void initLoop() {
        if (tuningBias) {
            MaxBiasFixingTime -= clock.getDeltaSeconds();
            telemetry.addData("Time Left", MaxBiasFixingTime);
            if (MaxBiasFixingTime < 0) {
                tuningBias = false;
            }
            tuneBias();
        } else {
            if (!onBiasDone) {
                webcam.pipeline.manualTuneBias(0, -0.04, 0);
                onBiasDone = true;
                webcam.pipeline.setDebug(false);
            }
            telemetry.addData("Times Tuned:", timesTuned);
            autoVersion = AutoVersionUpdate();
        }
    }

    protected boolean handleState(RobotState state) {
        return true;
    }

    public void start() {

        switch (autoVersion) {
            case 1:
                trajectoriesToFollow = CreateLeftTrajectories();
                break;
            case 2:
                trajectoriesToFollow = CreateCenterTrajectories();
                break;
            case 3:
                trajectoriesToFollow = CreateRightTrajectories();
                break;
            default:
                trajectoriesToFollow = CreateDefaultTrajectories();
        }
        drive.followTrajectorySequenceAsync(trajectoriesToFollow.get(trajectoryIndex));
    }

    protected void onMainLoop() {
        drive.update();
    }

    protected void onNextLoop() {
        trajectoryIndex++;
        if (trajectoryIndex > trajectoriesToFollow.size() - 1) {
            robotState = RobotState.STOP;
        } else {
            drive.followTrajectorySequenceAsync(trajectoriesToFollow.get(trajectoryIndex));
            drive.update();
            robotState = RobotState.MAIN;
        }
    }

    protected void onStopLoop() {
        super.onStopLoop();
        robotState = RobotState.IDLE;
    }

    protected void onIdleLoop() {
    }

    private int AutoVersionUpdate() {
        webcam.pipeline.PrintTelemetry(telemetry);
        return webcam.pipeline.ReturnCurrentTeamPropPos();
    }

    private ArrayList<TrajectorySequence> CreateDefaultTrajectories() {
        return CreateCenterTrajectories();
    }

    private ArrayList<TrajectorySequence> CreateLeftTrajectories() {
        TrajectorySequence trajectory1 =
                drive.trajectorySequenceBuilder(new Pose2d(-38.0, -61.0, Math.toRadians(-90)))
                        .back(10)
                        .splineToLinearHeading(new Pose2d(-45, -43, Math.toRadians(90)), Math.toRadians(0))
                        .splineToConstantHeading(new Vector2d(-36, -36), Math.toRadians(90))
                        .lineToConstantHeading((new Vector2d(12, -36)))
                        .splineToLinearHeading(new Pose2d(14, -36, Math.toRadians(180)), Math.toRadians(0))
                        .build();

        drive.setPoseEstimate(trajectory1.start());

        return new ArrayList<>(Arrays.asList(trajectory1));
    }

    private ArrayList<TrajectorySequence> CreateCenterTrajectories() {
        TrajectorySequence trajectory1 =
            drive.trajectorySequenceBuilder(new Pose2d(-38.0, -61.0, Math.toRadians(-90)))
                    .back(10)
                    .splineToLinearHeading(new Pose2d(-38, -34, Math.toRadians(90)), Math.toRadians(0))
                    .splineToConstantHeading(new Vector2d(-36, -36), Math.toRadians(90))
                    .lineToConstantHeading((new Vector2d(12, -36)))
                    .splineToLinearHeading(new Pose2d(14, -36, Math.toRadians(180)), Math.toRadians(0))
                    .build();

        drive.setPoseEstimate(trajectory1.start());

        return new ArrayList<>(Arrays.asList(trajectory1));
    }

    private ArrayList<TrajectorySequence> CreateRightTrajectories() {
        TrajectorySequence trajectory1 =
            drive.trajectorySequenceBuilder(new Pose2d(-38.0, -61.0, Math.toRadians(-90)))
                    .back(10)
                    .splineToLinearHeading(new Pose2d(-36, -36, Math.toRadians(90)), Math.toRadians(0))
                    .lineToConstantHeading(new Vector2d(-24,-36))
                    .lineToConstantHeading(new Vector2d(12, -36))
                    .splineToLinearHeading(new Pose2d(14, -36, Math.toRadians(180)), Math.toRadians(0))
                    .build();

        drive.setPoseEstimate(trajectory1.start());

        return new ArrayList<>(Arrays.asList(trajectory1));
    }
}