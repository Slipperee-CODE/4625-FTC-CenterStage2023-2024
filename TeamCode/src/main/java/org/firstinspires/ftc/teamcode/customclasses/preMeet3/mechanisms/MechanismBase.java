package org.firstinspires.ftc.teamcode.customclasses.preMeet3.mechanisms;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.customclasses.preILT.CustomGamepad;

public abstract class MechanismBase implements Mechanism {
    public static final boolean STRICT = false;
    public MechanismState state;
    CustomGamepad gamepad;

    public abstract void update();

    public void update(Telemetry telemetry){
        update();
    }

    public void setState(MechanismState state) {
        this.state = state;
    }

    public <T> T getHardware(Class<? extends T> classOrInterface, String deviceName, HardwareMap hardwareMap) {
        T hw = hardwareMap.tryGet(classOrInterface, deviceName.trim());
        if (hw == null ) {
            if (STRICT) { throw new NullPointerException();  }
            MissingHardware.addMissingHardware(deviceName);

            if (Servo.class.equals(classOrInterface)) {
                return (T) new EmptyServo();
            } else if (DcMotor.class.equals(classOrInterface)) {
                return (T) new EmptyDcMotor();
            } else if (ColorSensor.class.equals(classOrInterface)) {
                return (T) new EmptyColorSensor();
            } else if (DistanceSensor.class.equals(classOrInterface)) {
                return (T) new EmptyDistanceSensor();
            } else if (TouchSensor.class.equals(classOrInterface)) {
                return (T) new EmptyTouchSensor();
            }
        }
        return hw;
    }
    private static class EmptyServo implements Servo {
        @Override
        public ServoController getController() {
            return null;
        }

        @Override
        public int getPortNumber() {
            return 0;
        }

        @Override
        public void setDirection(Servo.Direction direction) {

        }


        @Override
        public Servo.Direction getDirection() {
            return null;
        }

        @Override
        public void setPosition(double position) {

        }

        @Override
        public double getPosition() {
            return 0;
        }

        @Override
        public void scaleRange(double min, double max) {

        }

        @Override
        public Manufacturer getManufacturer() {
            return null;
        }

        @Override
        public String getDeviceName() {
            return null;
        }

        @Override
        public String getConnectionInfo() {
            return null;
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {

        }

        @Override
        public void close() {

        }
    }
    public static class EmptyDcMotor implements DcMotor {
        @Override
        public MotorConfigurationType getMotorType() {return null;}
        @Override
        public void setMotorType(MotorConfigurationType motorType) {}
        @Override
        public DcMotorController getController() {return null;}
        @Override
        public int getPortNumber() {return 0;}
        @Override
        public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {}
        @Override
        public ZeroPowerBehavior getZeroPowerBehavior() {return null;}
        @Override
        public void setPowerFloat() {}
        @Override
        public boolean getPowerFloat() {return false;}
        @Override
        public void setTargetPosition(int position) {}
        @Override
        public int getTargetPosition() {return 0;}
        @Override
        public boolean isBusy() {return false;}
        @Override
        public int getCurrentPosition() {return 0;}
        @Override
        public void setMode(RunMode mode) {}
        @Override
        public RunMode getMode() {return null;}
        @Override
        public void setDirection(Direction direction) {}
        @Override
        public Direction getDirection() {return null;}
        @Override
        public void setPower(double power) {}
        @Override
        public double getPower() {return 0;}
        @Override
        public Manufacturer getManufacturer() {return null;}
        @Override
        public String getDeviceName() {return null;}
        @Override
        public String getConnectionInfo() {return null;}
        @Override
        public int getVersion() {return 0;}
        @Override
        public void resetDeviceConfigurationForOpMode() {}
        @Override
        public void close() {}
    } //is public so other things can acces it
    private static class EmptyColorSensor implements ColorSensor {
        @Override
        public int red() {return 0;}
        @Override
        public int green() {return 0;}
        @Override
        public int blue() {return 0;}
        @Override
        public int alpha() {return 0;}
        @Override
        public int argb() {return 0;}
        @Override
        public void enableLed(boolean enable) {}
        @Override
        public void setI2cAddress(I2cAddr newAddress) {}
        @Override
        public I2cAddr getI2cAddress() {return null;}
        @Override
        public Manufacturer getManufacturer() {return null;}
        @Override
        public String getDeviceName() {return "DEVICE NOT FOUND: Empty";}
        @Override
        public String getConnectionInfo() {return null;}
        @Override
        public int getVersion() {return 0;}
        @Override
        public void resetDeviceConfigurationForOpMode() {}
        @Override
        public void close() {}
    }
    private static class EmptyDistanceSensor implements DistanceSensor {
        @Override
        public double getDistance(DistanceUnit unit) {return 0;}
        @Override
        public Manufacturer getManufacturer() {return null;}
        @Override
        public String getDeviceName() {return null;}
        @Override
        public String getConnectionInfo() {return null;}
        @Override
        public int getVersion() {return 0;}
        @Override
        public void resetDeviceConfigurationForOpMode() {}
        @Override
        public void close() {}
    }
    private static class EmptyTouchSensor implements DistanceSensor {
        public double getDistance(DistanceUnit unit) { return 0; }
        public Manufacturer getManufacturer() {return null;}
        public String getDeviceName() {return null;}
        public String getConnectionInfo() {return null;}
        public int getVersion() {return 0;}
        public void resetDeviceConfigurationForOpMode() {}
        public void close() {}
    }
}
