package org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.customclasses.preILT.CustomGamepad;

public class IntakeAngler extends MechanismBase implements Mechanism {
    private final Servo servo;

    private static final double LOW = 0.794;
    private static final double UP = 0.0;

    public IntakeAngler(HardwareMap hardwareMap, CustomGamepad gamepad)
    {
        servo = getHardware(Servo.class,"IntakeAngler",hardwareMap);
        this.gamepad = gamepad;
        servo.setPosition(UP);
    }

    public void update()
    {
        if (gamepad.gamepad.right_trigger != 0 || gamepad.gamepad.left_trigger != 0 ){
            double position =(servo.getPosition() + (gamepad.gamepad.right_trigger-gamepad.gamepad.left_trigger)* 0.007);
            if (position > LOW)
                position = LOW;
            else if (position < UP)
                position = UP;
            servo.setPosition(position);
        }
    }

    public void update(Telemetry telemetry)
    {
        this.update();
        //telemetry.addData("IntakeAngler State", state.toString());
        telemetry.addData("INTAKE ANGLER SERVO POS",servo.getPosition());
    }

    public void setState(MechanismState state){
        this.state = state;
        switch(state)
        {
            case HIGH:
                servo.setPosition(0);
                break;

            case MID:
                servo.setPosition(.5);
                break;

            case LOW:
                servo.setPosition(.85);
                break;

            case IDLE:
                //WAITING FOR NEXT STATE
                break;

            default:
                state = MechanismState.IDLE;
        }
    }
}
