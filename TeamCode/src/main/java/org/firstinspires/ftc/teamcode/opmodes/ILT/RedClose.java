package org.firstinspires.ftc.teamcode.opmodes.ILT;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.customclasses.preILT.Clock;
import org.firstinspires.ftc.teamcode.customclasses.preILT.ContourAndAprilTagWebcam;
import org.firstinspires.ftc.teamcode.customclasses.preILT.CustomGamepad;
import org.firstinspires.ftc.teamcode.customclasses.preILT.mechanisms.MechanismState;
import org.firstinspires.ftc.teamcode.customclasses.preILT.mechanisms.Outtake;
import org.firstinspires.ftc.teamcode.customclasses.preILT.mechanisms.PixelQuickRelease;
import org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms.AprilTagAlign;
import org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms.TeamPropDetection;
import org.firstinspires.ftc.teamcode.opmodes.ILT.testing.BlueContourVisionProcessor;
import org.firstinspires.ftc.teamcode.opmodes.ILT.testing.ContourVisionProcessor;
import org.firstinspires.ftc.teamcode.opmodes.ILT.testingOpmodes.BlueContourVisionPortalWebcam;
import org.firstinspires.ftc.teamcode.opmodes.preILT.WaitingAuto;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;

public class RedClose extends WaitingAuto {
    public enum State {
        DRIVE,
        SCORE_BACKDROP,
        SCORE_SCORE,
    }
    ContourVisionProcessor.TeamPropState tpPosition;
    State autoState = State.DRIVE;
    ContourAndAprilTagWebcam multipurposeWebcam;
    AprilTagAlign aprilTagAlign;
    PixelQuickRelease pixelQuickRelease;
    Clock timer;
    Outtake outtake;



    public void init() {
        super.init();
        telemetry.addLine("Not Ready Yet!!");
        telemetry.update();
        multipurposeWebcam = new ContourAndAprilTagWebcam(hardwareMap);
        multipurposeWebcam.setExposure(12);
        multipurposeWebcam.SetContourColor(ContourVisionProcessor.Color.RED);
        outtake = new Outtake(hardwareMap,new CustomGamepad(gamepad1));
        pixelQuickRelease = new PixelQuickRelease(hardwareMap,new CustomGamepad(gamepad2),false);
        pixelQuickRelease.setState(MechanismState.CLOSED);
        //roadrunnerDrivetrain.followTrajectorySequenceAsync(buildInitialTrajectories());
    }
    private String makeLoadingString(int maxDots) {
        StringBuilder s = new StringBuilder(maxDots);
        int dots = (int) (2*timer.getTimeSeconds()) % maxDots;
        for (int i = 0 ; i < dots; i++) {
            s.append("*");
        }
        return s.toString();
    }

    public void init_loop() {
        super.init_loop();
        tpPosition = multipurposeWebcam.getTeamPropPosition();
        telemetry.addData("Detected Position", tpPosition);
        telemetry.addLine("Safe To Proceed");
        telemetry.addLine(makeLoadingString(5));
        telemetry.update();
        pixelQuickRelease.setState(MechanismState.CLOSED);

    }
    public void startBeforeWait() {
        roadrunnerDrivetrain.followTrajectorySequenceAsync(buildTrajectory(tpPosition));
    }

    @Override
    protected void update() {
        pixelQuickRelease.update();
        switch (autoState) {
            case DRIVE:
                roadrunnerDrivetrain.update();
                if (!roadrunnerDrivetrain.isBusy()) {
                    autoState = State.SCORE_BACKDROP;
                    multipurposeWebcam.setActiveProcessor(ContourAndAprilTagWebcam.Processor.APRIL_TAG);
                    multipurposeWebcam.setExposure(6);
                    multipurposeWebcam.setGain(50);
                    aprilTagAlign.setState(org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms.MechanismState.ON);
                    outtake.setLinearSlidesPosition(Outtake.LinearSlidesPosition.FIRST_ROW);
                    timer.reset();
                }
                telemetry.addData("PoseEstimate",roadrunnerDrivetrain.getPoseEstimate());
                break;
            case SCORE_BACKDROP:
                roadrunnerDrivetrain.updatePoseEstimate();
                multipurposeWebcam.update();
                aprilTagAlign.update();
                outtake.update();
                if (aprilTagAlign.isAligned() || timer.getTimeSeconds() > 2) {
                    outtake.setDropPosition();
                    robotDrivetrain.stop();
                    outtake.startDropSequence(() -> roadrunnerDrivetrain.followTrajectorySequenceAsync(buildPark()));
                    autoState = State.SCORE_SCORE;
                }
                outtake.update();

                break;
            case SCORE_SCORE:
                telemetry.addLine("SCORE SCORE");
                roadrunnerDrivetrain.update();
                outtake.update();
                break;

        }
    }

    private TrajectorySequence buildTrajectory(ContourVisionProcessor.TeamPropState detection) {
        roadrunnerDrivetrain.setPoseEstimate(new Pose2d(-12, 61.0, Math.PI/2));
        TrajectorySequenceBuilder bob = roadrunnerDrivetrain.trajectorySequenceBuilder(roadrunnerDrivetrain.getPoseEstimate())
                .setReversed(true)
                .waitSeconds(2)
                // BLUE CLOSE SIDE
                .back(26)
                .waitSeconds(1); //DETECTY

        switch(detection) {
            case CENTER:
             bob.back(6)
                .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                .waitSeconds(1)// Dumpy
                .forward(8)
                .turn(-Math.PI/2);

            case RIGHT:
             bob.splineTo(new Vector2d(-32,34),Math.PI)
                .turn(-Math.PI)
                .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                .waitSeconds(1)//Dumpy
                .forward(4)
                .turn(-Math.PI);

            case LEFT:
             bob.turn(Math.PI/2)
                .back(4)
                .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                .waitSeconds(1)// Dumpy
                .forward(10)
                .turn(-Math.PI);
        }

                // ENDING
        return bob.splineTo(new Vector2d(-44,34),Math.PI)
                .build();
    }

    private TrajectorySequence buildPark() {
        telemetry.addData("Building PARK",roadrunnerDrivetrain.getPoseEstimate());
        //will use our current poseEstimate to try to park the robot in the corner
        TrajectorySequenceBuilder bob = roadrunnerDrivetrain.trajectorySequenceBuilder(roadrunnerDrivetrain.getPoseEstimate())
                .strafeTo(new Vector2d(roadrunnerDrivetrain.getPoseEstimate().getX(),61))
                .back(9);
        return bob.build();
    }
}
