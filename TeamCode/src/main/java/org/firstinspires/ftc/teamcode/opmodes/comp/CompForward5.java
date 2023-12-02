package org.firstinspires.ftc.teamcode.opmodes.comp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.customclasses.Clock;
import org.firstinspires.ftc.teamcode.customclasses.CustomGamepad;
import org.firstinspires.ftc.teamcode.customclasses.CustomOpMode;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.MissingHardware;

@Autonomous(name="(Blue||Red)Close Meet1")

public class CompForward5 extends CustomOpMode {
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

    public static double FORWARD_TIME = 5.0;

    public void init() {
        super.init();
        telemetry.setMsTransmissionInterval(6);
        //pixelTiltOuttake = new PixelTiltOuttake(hardwareMap);
        //linearSlides = new LinearSlides(hardwareMap, telemetry);
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
        timer.reset();
    }

    protected void onMainLoop() {
        if (waiting){
            waiting = timer.getTimeSeconds() < time_to_start;
            robot.stop();
            return;
        }
        if (clock.getTimeSeconds() < FORWARD_TIME) {
            robot.emulateController(-.356,0,0);
        } else {
            robot.stop();
        }
    }

    protected void onNextLoop() {

    }

    protected void onIdleLoop() {    }

}