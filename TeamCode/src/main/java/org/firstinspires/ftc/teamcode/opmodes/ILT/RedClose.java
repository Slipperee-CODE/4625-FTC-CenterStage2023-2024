package org.firstinspires.ftc.teamcode.opmodes.ILT;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
@Autonomous(name= "Red Close Auto")
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
        timer = new Clock();
        telemetry.addLine("Not Ready Yet!!");
        telemetry.update();
        multipurposeWebcam = new ContourAndAprilTagWebcam(hardwareMap);
        multipurposeWebcam.setActiveProcessor(ContourAndAprilTagWebcam.Processor.CONTOUR);
        multipurposeWebcam.setExposure(19);
        multipurposeWebcam.setGain(110);
        multipurposeWebcam.SetContourColor(ContourVisionProcessor.Color.RED);
        outtake = new Outtake(hardwareMap,new CustomGamepad(gamepad1));
        pixelQuickRelease = new PixelQuickRelease(hardwareMap,new CustomGamepad(gamepad2),false);
        pixelQuickRelease.setState(MechanismState.CLOSED);
        //roadrunnerDrivetrain.followTrajectorySequenceAsync(buildInitialTrajectories());
        aprilTagAlign = new AprilTagAlign(hardwareMap,telemetry,null,robotDrivetrain);
        aprilTagAlign.setState(org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms.MechanismState.OFF);
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
        switch (tpPosition){
            case LEFT:
                aprilTagAlign.setTargetID(4);
                break;
            case CENTER:
                aprilTagAlign.setTargetID(5);
                break;
            case RIGHT:
                aprilTagAlign.setTargetID(6);
                break;
        }
        multipurposeWebcam.setActiveProcessor(ContourAndAprilTagWebcam.Processor.APRIL_TAG);    }

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
                if (aprilTagAlign.isAligned() || timer.getTimeSeconds() > 3) {
                    outtake.setDropPosition();
                    robotDrivetrain.stop();
                    outtake.startDropSequence(() -> roadrunnerDrivetrain.followTrajectorySequenceAsync(buildPark()));
                    autoState = State.SCORE_SCORE;
                }
                break;
            case SCORE_SCORE:
                telemetry.addLine("SCORE SCORE");
                roadrunnerDrivetrain.update();
                outtake.update();
                break;

        }
    }

    private TrajectorySequence buildTrajectory(ContourVisionProcessor.TeamPropState detection) {
        roadrunnerDrivetrain.setPoseEstimate(new Pose2d(-12, -61.0, Math.PI/2));
        TrajectorySequenceBuilder bob = roadrunnerDrivetrain.trajectorySequenceBuilder(roadrunnerDrivetrain.getPoseEstimate())
                .setReversed(true)
                .waitSeconds(2)
                // BLUE CLOSE SIDE
                .back(26);


        switch (detection) {
            case LEFT:
                return bob.back(5)
                        .turn(Math.PI/2)
                        .back(3.5)
                        .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                        .waitSeconds(0.5)
                        .forward(15)
                        .turn(Math.PI)
                        .back(15)
                        .build();
            //.forward(12)
            case CENTER:
                return bob.back(1)
                        .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                        .waitSeconds(0.5)
                        .forward(8)
                        .turn(-Math.PI/2)
                        .back(20)
                        .build();
                //.splineTo(new Vector2d(-24,-30),Math.PI)

            case RIGHT:
                bob.turn(-Math.PI/2)
                        .back(3)
                        .addTemporalMarker(() -> pixelQuickRelease.setState(MechanismState.OPEN))
                        .waitSeconds(0.4)
                        .forward(5)
                        .strafeLeft(9)
                        .back(20);
                break;
        }
        // ENDING
        bob.back(20);
        return bob.build();
                // ENDING
    }

    private TrajectorySequence buildPark() {
        telemetry.addData("Building PARK",roadrunnerDrivetrain.getPoseEstimate());
        //will use our current poseEstimate to try to park the robot in the corner
        TrajectorySequenceBuilder bob = roadrunnerDrivetrain.trajectorySequenceBuilder(roadrunnerDrivetrain.getPoseEstimate())
                .strafeTo(new Vector2d(roadrunnerDrivetrain.getPoseEstimate().getX(),50));
        return bob.build();
    }
}
