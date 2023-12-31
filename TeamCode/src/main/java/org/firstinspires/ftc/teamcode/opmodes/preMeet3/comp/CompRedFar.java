package org.firstinspires.ftc.teamcode.opmodes.preMeet3.comp;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.customclasses.preILT.Clock;
import org.firstinspires.ftc.teamcode.customclasses.preILT.CustomGamepad;
import org.firstinspires.ftc.teamcode.customclasses.preMeet3.CustomOpMode;
import org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms.MissingHardware;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

@Disabled
@Autonomous(name="RedFar Meet1")

public class CompRedFar extends CustomOpMode {
    //Roadrunner Stuff
    // Conventions to Follow : the back of the field is the side with the scoring boards, front is the other side with the big apriltags
    // Remember that the when centered to field and heading is 0 then the robot is facing the right because the heading 0 is to the right on a unit circle

    // Mechanisms || Webcams || Timers
    private Clock clock = null;
    //private final PixelTiltOuttake pixelTiltOuttake = null;
    //private final LinearSlides linearSlides = null;
    //private Webcam webcam = null;

    //private LeosAprilTagFun tagAlign = null;
    private CustomGamepad gamepadOne;
    // Miss
    private Clock timer;
    private double time_to_start = 0.0;
    private boolean waiting = true;

    public void init() {
        super.init();
        telemetry.setMsTransmissionInterval(6);

        gamepadOne = new CustomGamepad(this,1);

        clock = new Clock();
        timer = new Clock();
        MissingHardware.printMissing(telemetry);
        sleep(1000);

    }



    protected void initLoop() {
        gamepadOne.update();

        if (gamepadOne.yDown) {
            time_to_start = 0.0;
        }
        time_to_start += gamepad1.left_stick_y * clock.getDeltaSeconds() * 0.5;
        telemetry.addData("Time To Start: ",time_to_start);
        telemetry.addLine("Left Joystick to control");
        telemetry.addLine("Y to Reset to 0");
    }

    protected boolean handleState(RobotState state) {return false;    }

    public void start() {
        TrajectorySequence trajectoryToFollow = CreateDefaultTrajectories();
        drive.followTrajectorySequenceAsync(trajectoryToFollow);
    }

    protected void onMainLoop() {
        if (waiting){
            waiting = timer.getTimeSeconds() < time_to_start;
            robot.stop();
            return;
        }
        drive.update();
    }

    protected void onNextLoop() {

    }

    protected void onStopLoop() {
        super.onStopLoop();
        robotState = RobotState.IDLE;
    }

    protected void onIdleLoop() {    }

    private TrajectorySequence CreateDefaultTrajectories() {
        return CreateCenterTrajectories();
    }

    private TrajectorySequence CreateCenterTrajectories() {
        TrajectorySequence trajectory1 =
                drive.trajectorySequenceBuilder(new Pose2d(-38.0, -61.0, Math.toRadians(-90)))
                        .back(20)
                        .splineTo(new Vector2d(-35, -15),Math.PI/4)
                        .splineTo(new Vector2d(0,-10),0)
                        .splineTo(new Vector2d(30,-15),Math.toRadians(-20))
                        .back(14)
                        .build();
        drive.setPoseEstimate(trajectory1.start());
        return trajectory1;
    }
}
