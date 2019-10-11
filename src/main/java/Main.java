import com.tinkerforge.*;
import com.tinkerforge.IPConnection;
import com.tinkerforge.BrickletAccelerometer;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 4223;

    private static final String ACC_UID = "v8P";
    private static final String SERVO_UID = "6rnbKm";
    private static final String POTI_UID = "r67";
    private static final short SERVO_NR = 6;


    private static final boolean DEBUG = false;


    // Note: To make the example code cleaner we do not handle exceptions. Exceptions
    //       you might normally want to catch are described in the documentation
    public static void main(String args[]) throws Exception {
        // config tinkerforge
        IPConnection ipcon = new IPConnection(); // Create IP connection

        BrickletAccelerometer accelerometer = new BrickletAccelerometer(ACC_UID, ipcon);
        BrickServo servo = new BrickServo(SERVO_UID, ipcon);
        BrickletLinearPoti poti = new BrickletLinearPoti(POTI_UID, ipcon);

        ipcon.connect(HOST, PORT); // Connect to brickd
        // Don't use device before ipcon is connected

        servo.enable(SERVO_NR);
        servo.setAcceleration(SERVO_NR, 65535);

        // config pid

        PID pid = new PID();

        pid.setPID(9.5, 0.1, 3);
        //pid.setPID(0.7, 0.02, 1);
        pid.setRequired(0);


        // start loop

        System.out.println("Start");

        while (true) {
            try {
                // Get current acceleration
                BrickletAccelerometer.Acceleration acceleration = accelerometer.getAcceleration();
                double y = acceleration.y / 10;
                double potiRel = (poti.getAnalogValue() - 2047) / 10;

                if(DEBUG) System.out.println("Acceleration [X]: \t" + acceleration.x / 1000.0 + " g \t [Y]: " + acceleration.y / 1000.0 + " g \t [Z]: " + acceleration.z / 1000.0 + " g");

                pid.setInput(y);
                double steuer = pid.getOutput();

                // apply correction to absolute
                double pos = servo.getPosition(SERVO_NR);
                double newPos = pos - steuer; // invert correction to counter act influence

                //newPos += potiRel; // konstante störgrösse

                servo.setPosition(SERVO_NR, (short) newPos);

                if(true) System.out.printf("Error %f\tCorrection %f\tErrSum %f\tStör %f%n", pid.getError(), pid.getOutput(), pid.getErrorSum(), potiRel);

                if(DEBUG) System.out.printf("Error %f\tSteuer %f\tPos %f\tNewPos %f\tP %f\tI %f\tD %f\tErrSum %f%n", pid.getError(), steuer, pos, newPos,pid.getProportional(), pid.getIntegral(), pid.getDifferencial(), pid.getErrorSum());

                try{
                    Thread.sleep(20);
                }catch(Exception e){

                }

            } catch (TimeoutException $ex) {
                System.out.println('.');
            }
        }

    }
}
