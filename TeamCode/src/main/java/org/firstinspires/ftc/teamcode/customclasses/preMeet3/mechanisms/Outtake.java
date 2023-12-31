package org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.customclasses.preILT.Clock;
import org.firstinspires.ftc.teamcode.customclasses.preILT.CustomGamepad;
import org.firstinspires.ftc.teamcode.customclasses.preILT.PIDMotor;

public class Outtake extends MechanismBase {
    private static final float OVERRIDE_SPEED = 50.0f;

    public static final int DROP_PIXEL_MIN_POSITION = -200; // position for linear slides
    public static int DROP_PIXEL_MAX_POSITION = 2_500; // position for linear slides
    public final int readyToReceivePixelsTarget = 0; // position for linear slides
    public final int readyToDropPixelsTarget = 1000; // position for linear slides
    public static final double OUTTAKE_RECEIVE_ANGLER_POSITION = 0.9; // position for dropAngler
    public static final double OUTTAKE_DROP_ANGLER_POSITION_LOWER = 0.7; // position for dropAngler
    public static final double OUTTAKE_DROP_ANGLER_POSITION_NORMAL = 0.15; // position for dropAngler
    public static final double OUTTAKE_ANGLER_RECHAMBER_POSITION = 0.25; // position for dropAnger
    public static final double LID_RECEIVE_POSITION = 0.03;//0.0895 // position for LidAngler
    public static final double LID_DROP_POSITION  = 0.02; // position for LidAngler
    public static final double LID_DROP_SLIGHTLY_OPEN = 0.045;//0.05 guesstimation for LidAngler
    public static final double OUTTAKE_CLOSED_POSITION = .6; // position for Dropper
    public static final double OUTTAKE_OPEN_POSITION = .2; // position for Dropper
    public static final double OUTTAKE_RECEIVE_POSITION = .3; // position for Dropper
    public static final float STARTING_JOYSTICK_THRESHOLD = 0.2f;
    public static int STARTING_SLIDES_MOTOR_TICK;
    private boolean startToChamber = false;
    private boolean slidesUp;
    private boolean receivingPixel;
    private boolean chambering = false;
    private boolean itsGoingDownForReal = false;
    public static final int TICK_THRESHOLD_FOR_GOING_DOWN = 500;
    private final PIDMotor slidesMotorRight;
    private final PIDMotor slidesMotorLeft;
    private final Servo DropAngler;
    private final Servo LidAngler;
    private final Servo Dropper;
    //private final DistanceSensor distanceSensor;
    private final Clock timer = new Clock();
    public static final double CHAMBERING_TIME = 0.25;
    public double chamber_start_time = -1.0;




    private static final double tolerance = .01;

    public static  final double p = 0.0045;
    public static  final double i = 0.00001;
    public static  final double d = 0.00;

    public Outtake(HardwareMap hardwareMap, CustomGamepad gamepad){
        slidesMotorRight = new PIDMotor(getHardware(DcMotor.class,"idunno",hardwareMap),p,i,d);
        slidesMotorLeft = new PIDMotor(getHardware(DcMotor.class,"rightLinearSlides",hardwareMap),p,i,d);
        slidesMotorLeft.motor.setDirection(DcMotorSimple.Direction.REVERSE);
        LidAngler = getHardware(Servo.class,"OuttakeLidAngler",hardwareMap);
        //distanceSensor = getHardware(DistanceSensor.class,"distanceSensor",hardwareMap);
        slidesMotorRight.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        DropAngler = getHardware(Servo.class, "OuttakeAngler",hardwareMap);
        Dropper = getHardware(Servo.class, "OuttakeDropper",hardwareMap);

        this.gamepad = gamepad;
        slidesUp = false;
        receivingPixel = true;
        STARTING_SLIDES_MOTOR_TICK = slidesMotorRight.motor.getCurrentPosition();
        setReceivePosition();
    }
    public void setState(MechanismState newState) {
        this.state = newState;

    }
    public void setReceivePosition() {
        //SlidesMotor.setTarget(readyToReceivePixelsTarget);
        DropAngler.setPosition(OUTTAKE_RECEIVE_ANGLER_POSITION);
        LidAngler.setPosition(LID_RECEIVE_POSITION);
        Dropper.setPosition(OUTTAKE_RECEIVE_POSITION);

    }
    public void setDropLowerPosition() {
        //SlidesMotor.setTarget(readyToDropPixelsTarget);
        DropAngler.setPosition(OUTTAKE_DROP_ANGLER_POSITION_LOWER);
        LidAngler.setPosition(LID_DROP_SLIGHTLY_OPEN);
        Dropper.setPosition(OUTTAKE_CLOSED_POSITION); // here we can directly open the outtake because we are sure that we are in the correct spot to drop it
    }
    public void setDropLowerPositionWithLidClosed() {
        DropAngler.setPosition(OUTTAKE_DROP_ANGLER_POSITION_LOWER);
        LidAngler.setPosition(LID_DROP_POSITION);
        Dropper.setPosition(OUTTAKE_CLOSED_POSITION);
    }
    public void drop() {
        Dropper.setPosition(OUTTAKE_OPEN_POSITION);
    }
    public void rechamber() {
        startToChamber = true;
    }
    public void setDropNormalPosition() {
        //SlidesMotor.setTarget(Math.max(SlidesMotor.getTarget(),DROP_PIXEL_MIN_POSITION));
        LidAngler.setPosition(LID_DROP_POSITION);

        DropAngler.setPosition(OUTTAKE_DROP_ANGLER_POSITION_NORMAL);
        // here we CANNOT open the dropper as we still have to align ourselves to where we want to drop it at
        Dropper.setPosition(OUTTAKE_CLOSED_POSITION); // we close it just to make sure that out pixel doesn't fall before we are at the right spot
    }
    public void startChambering() {
        Dropper.setPosition(OUTTAKE_CLOSED_POSITION);
        LidAngler.setPosition(LID_DROP_SLIGHTLY_OPEN);
        DropAngler.setPosition(OUTTAKE_ANGLER_RECHAMBER_POSITION);
    }
    public void stopChambering() {
        Dropper.setPosition(OUTTAKE_CLOSED_POSITION);
        LidAngler.setPosition(LID_DROP_POSITION);
        DropAngler.setPosition(OUTTAKE_DROP_ANGLER_POSITION_NORMAL);
    }




    public void update()
    {
        if (itsGoingDownForReal) {
            int error = slidesMotorRight.motor.getCurrentPosition() - STARTING_SLIDES_MOTOR_TICK;
            //SlidesMotor.setRawPower(Math.tanh(error));
            //if (Math.abs(error) < TICK_THRESHOLD_FOR_GOING_DOWN) {
            //    itsGoingDownForReal = false;
            //}
        }
        if (gamepad.yDown || startToChamber) {
            if (!chambering) {
                if (slidesUp && !receivingPixel) {
                    chambering = true;
                    startToChamber = false;
                    chamber_start_time = timer.getTimeSeconds();
                    startChambering();
                }
            }
        }
        if (chambering) {
            if (timer.getTimeSeconds() - chamber_start_time > CHAMBERING_TIME) {
                stopChambering();
                chambering = false;
            }
        }
        if (gamepad.xDown) {
            if ((slidesUp || !receivingPixel)) { // set them down
                if (somewhatEquals(Dropper.getPosition(),OUTTAKE_OPEN_POSITION)) {
                    slidesUp = false;
                    receivingPixel = true;
                    itsGoingDownForReal = true;
                    setReceivePosition();
                } else if (somewhatEquals(Dropper.getPosition(),OUTTAKE_CLOSED_POSITION)) {
                    Dropper.setPosition(OUTTAKE_OPEN_POSITION);
                }
            } else { // if we are recieving the pixel then as a sanity check we make sure the slides are down
                // here since the use hasn't done anything to point that they want to go up we assume that they just want to deposit them down
                receivingPixel = false;
                setDropLowerPosition();
            }
        }
        float right_stick_y = -gamepad.right_stick_y;

        if (!slidesUp) { // means we are at are either recieving or dropping from the lower position, either way we now want to
            if (right_stick_y > STARTING_JOYSTICK_THRESHOLD) {
                slidesUp = true;
                receivingPixel = false;
                setDropNormalPosition();
            }
        } else {
            if (right_stick_y != 0) {
            //slidesMotorRight.motor.setPower(gamepad.right_stick_y);
            //slidesMotorLeft.motor.setPower(-gamepad.right_stick_y);
                int targetLeft = slidesMotorLeft.getTarget() - (int) (gamepad.right_stick_y * OVERRIDE_SPEED);
                int targetRight = slidesMotorRight.getTarget() + (int) (gamepad.right_stick_y * OVERRIDE_SPEED);
                int clippedRight = Range.clip(targetRight,-DROP_PIXEL_MAX_POSITION,-DROP_PIXEL_MIN_POSITION);
                int clippedLeft = Range.clip(targetLeft,DROP_PIXEL_MIN_POSITION,DROP_PIXEL_MAX_POSITION);

                slidesMotorLeft.setTarget(clippedLeft);
                slidesMotorRight.setTarget(clippedRight);
            }

        }
        slidesMotorLeft.Update();
        slidesMotorRight.Update();
    }

    public void update(Telemetry telemetry){
        update();
        //telemetry.addData("LinearSlides State", state.toString());
        telemetry.addData("slidesLeftTarget",slidesMotorLeft.getTarget());
        telemetry.addData("slidesRightTarget",slidesMotorRight.getTarget());
        //telemetry.addData("Dropper Boolean", (Dropper.getPosition() == OUTTAKE_OPEN_POSITION));
    }

    public boolean somewhatEquals(double one, double two){
        double difference = Math.abs(one - two);
        return difference < tolerance;
    }
}
