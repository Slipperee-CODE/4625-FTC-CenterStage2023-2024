package org.firstinspires.ftc.teamcode.opmodes.comp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.customclasses.CustomGamepad;
import org.firstinspires.ftc.teamcode.customclasses.CustomOpMode;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.ActiveIntake;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.IntakeAngler;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.LeosAprilTagFun;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.Mechanism;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.MechanismState;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.MissingHardware;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.Outtake;
import org.firstinspires.ftc.teamcode.customclasses.mechanisms.PlaneLauncher;
import org.firstinspires.ftc.teamcode.customclasses.webcam.Webcam;

@TeleOp(name="Meet2Teleop")
public class Meet2Teleop extends CustomOpMode
{
    CustomGamepad gamepad1;
    CustomGamepad gamepad2;
    Mechanism activeIntake;
    Mechanism intakeAngler;
    Mechanism outtake;
    LeosAprilTagFun tagAlign;
    Mechanism planeLauncher;

    public void init(){
        super.init();
        robot.SetSpeedConstant(0.85);
        gamepad1 = new CustomGamepad(this,1);
        gamepad2 = new CustomGamepad(this, 2);
        activeIntake = new ActiveIntake(hardwareMap, gamepad2);
        intakeAngler = new IntakeAngler(hardwareMap, gamepad2);
        outtake = new Outtake(hardwareMap, gamepad2);
        planeLauncher = new PlaneLauncher(hardwareMap,gamepad1);
        Webcam webcam = new Webcam(hardwareMap,telemetry,false);

        tagAlign = new LeosAprilTagFun(telemetry,hardwareMap,robot,webcam,false);
        tagAlign.init();
        MissingHardware.printMissing(telemetry);
        sleep(1000);
    }

    public void start() {

    }

    protected void onMainLoop() {
        gamepad1.update();
        gamepad2.update();
        robot.emulateController(gamepad1.left_stick_y,gamepad1.left_stick_x,gamepad1.right_stick_x);
        activeIntake.update();
        intakeAngler.update();
        outtake.update(telemetry);
        tagAlign.update();
        planeLauncher.update(telemetry);
        if (gamepad1.xDown) {
            tagAlign.setState(gamepad1.xToggle ? MechanismState.ON : MechanismState.OFF );
        }
    }

    protected void initLoop() {}

    protected void onNextLoop() {}

    protected void onIdleLoop() {}

    protected boolean handleState(RobotState state) { return true; }
}
