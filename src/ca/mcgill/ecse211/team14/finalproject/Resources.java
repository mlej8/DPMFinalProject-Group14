package ca.mcgill.ecse211.team14.finalproject;

import ca.mcgill.ecse211.team14.finalproject.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

import ca.mcgill.ecse211.wificlient.WifiConnection;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Class that stores all constants for lab 4 (localization).
 */
public class Resources {
	
	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.10; 

	/**
	 * The robot's width in centimeters.
	 */
	public static final double TRACK = 15.10; 

	/**
	 * Turning 90 degrees to start position parallel the wall. 
	 */
	public static final double RIGHT_ANGLE = 90.0;
	
	/**
	 * Fixed Motor Speed.
	 */
	public static final int MOTOR_SPEED = 100;
	
	/**
	 * Variable that keeps track of the distance between the light sensors and the motors. 
 	 */
	public static final double LIGHT_SENSOR_DISTANCE = 4.25;

	/**
	 * The speed at which the robot rotates in degrees per second.
	 */
	public static final int ROTATE_SPEED = 100;

	/**
	 * The acceleration.
	 */
	public static final int ACCELERATION = 3000;

	/**
	 * Number of times to filter out (ignore) data.
	 */
	public static final int FILTER_OUT = 30;

//	/**
//	 * Offset (standoff distance) from the wall (cm).
//	 */
//	public static final int BAND_CENTER = 30;  
//
//	/**
//	 * Width of dead band (cm) i.e. error threshold.
//	 */
//	public static final int BAND_WIDTH = 8;

	/**
	 * Sleep interval = 50ms = 20 Hz.
	 */
	public static final int SLEEPINT = 50;

   /**
     * Sleep interval for light sensor.
     */
    public static final int LIGHT_SLEEPINT = 35;
	
	/**
	 * The tile size in centimeters.
	 */
	public static final double TILE_SIZE = 30.48;

	/**
	 * The LCD.
	 */
	public static final TextLCD LCDScreen = LocalEV3.get().getTextLCD();

	/**
	 * The left motor.
	 */
	public static final EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	/**
	 * The right motor.
	 */
	public static final EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		
	/**
	 * The ultrasonic sensor.
	 */
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S3);

	/**
     * The left light sensor.
     */
    public static final EV3ColorSensor leftLightSensor = new EV3ColorSensor(SensorPort.S1);

	/**
     * The left light sensor.
     */
    public static final EV3ColorSensor rightLightSensor = new EV3ColorSensor(SensorPort.S4);
    
	/**
	 * The ultrasonic poller.
	 */
	public static SensorPoller sensorPoller = new SensorPoller();

	/**
	 * Instance of the Navigation class.
	 */
	public static Navigation navigator = Navigation.getNavigator();

	/**
	 * The odometer.
	 */
	public static Odometer odometer = Odometer.getOdometer();
	
	/**
	 * Instance of US Localizer
	 */
	public static UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer();
	
	/**
	 * d constant for ultrasonic localizer representing the threshold distance from the wall 
	 *
	 */
	public static final double d = 40.0; 
	
	/**
	 * Degree at which to rotate right when executing ultrasonic sensor localization.
	 */
	public static final double ROTATION_RIGHT = 5.0;
	
	/**
	 * Degree at which to rotate left when executing ultrasonic sensor localization.
	 */
	public static final double ROTATION_LEFT = -7.5;
    
    /**
     * The rotation speed of launch motor.
     */
    public static final int LAUNCH_MOTOR_SPEED = 800;
    
    /**
     * Acceleration of launch motor.
     */
    public static final int LAUNCH_MOTOR_ACCELERATOR = 2475;  
    
    /**
     * Launch rotation.
     */
    public static final int LAUNCH_ROTATION = 200;
    
    /**
     * Angle that launch motors rotates to shoot the ping-pong ball.
     */
    public static final double LAUNCH_ANGLE = 180;
    
    /**
     * Create light corrector 
     */
    public static LightCorrection lightCorrector = new LightCorrection();
    
}
