package org.firstinspires.ftc.teamcode.customclasses.webcam;



import static org.firstinspires.ftc.teamcode.customclasses.preMeet3.CustomOpMode.sleep;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.concurrent.TimeUnit;

public class Webcam {
    public OpenCvWebcam camera;
    private final int WEBCAM_WIDTH = 960;
    private final int WEBCAM_HEIGHT = 544;
    HardwareMap hardwareMap = null;
    public OpenCVPipeline pipeline = null;
    public volatile boolean isOpened = false;
    private Telemetry telemetry;


    public Webcam(HardwareMap hwMap) { initialize(hwMap);}

    public Webcam(HardwareMap hwMap, Telemetry telemetry, boolean openAsync) {
        this.telemetry = telemetry;

        initialize(hwMap);
        telemetry.update();

        if (openAsync) {
            openCameraAsync();
        } else {
            openCamera();
        }
    }

    private void initialize(HardwareMap hwMap){
        hardwareMap = hwMap;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        telemetry.addData("Camera ID:", cameraMonitorViewId);
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"), cameraMonitorViewId);
    }
    public void openCamera() {
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                isOpened = true;
            }
            @Override
            public void onError(int errorCode) {}
        });
        while (!isOpened) {
            sleep(50);
        }
        camera.startStreaming(WEBCAM_WIDTH,WEBCAM_HEIGHT, OpenCvCameraRotation.UPSIDE_DOWN);//should block until the camera is created
        camera.getExposureControl().setMode(ExposureControl.Mode.Manual);
        setExposure(16);
    }
    public void openCameraAsync() {

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(WEBCAM_WIDTH,WEBCAM_HEIGHT, OpenCvCameraRotation.UPSIDE_DOWN);
                camera.getExposureControl().setMode(ExposureControl.Mode.Manual);
                isOpened = true;
            }

            @Override
            public void onError(int errorCode)
            {
                telemetry.addLine("error in the webcam");
                telemetry.update();
            }
        });
    }
    public void closeCamera() {
        camera.closeCameraDevice();
    }
    public void stopStreaming() {
        camera.stopStreaming();
    }
    public void resumeStreaming() {
        camera.startStreaming(WEBCAM_WIDTH,WEBCAM_HEIGHT, OpenCvCameraRotation.UPRIGHT);
    }
    public void UseCustomPipeline(OpenCvPipeline pipeline)
    {
        this.pipeline = (OpenCVPipeline) pipeline;
        this.pipeline.setCameraResolution(WEBCAM_WIDTH,WEBCAM_HEIGHT);
        camera.setPipeline(pipeline); //THIS IS THE LINE CAUSING THE ISSUE (ILLEGAL ARGUMENT / NULL POINTER)
    }

    public boolean setExposure(long milli) {
        return camera.getExposureControl().setExposure(milli,TimeUnit.MILLISECONDS);
    }
    public long getExposure() {
        return camera.getExposureControl().getExposure(TimeUnit.MILLISECONDS);
    }
    public boolean setGain(int gain) {
        return camera.getGainControl().setGain(gain);
    }
    public int getGain() {
        return camera.getGainControl().getGain();
    }
    public boolean setWhiteBalanceMode(WhiteBalanceControl.Mode mode) {
        return camera.getWhiteBalanceControl().setMode(mode);
    }
    public boolean setWhiteBalanceTemp(int temp) {
        return camera.getWhiteBalanceControl().setWhiteBalanceTemperature(temp);
    }
    public int getWhiteBalanceTemp() {
        return camera.getWhiteBalanceControl().getWhiteBalanceTemperature();
    }

}
