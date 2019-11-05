package ca.mcgill.ecse211.team14.finalproject;

import ca.mcgill.ecse211.team14.finalproject.Odometer;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Class that stores all constants for lab 4 (localization).
 */
public class Resources {
	
	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.150; 

	/**
	 * The robot's width in centimeters.
	 */
	public static final double TRACK = 15.025; 

	/**
	 * Turning 90 degrees to start position parallel the wall 
	 */
	public static final double RIGHT_ANGLE = 90.0;
	
	/**
	 * Fixed Motor Speed.
	 */
	public static final int MOTOR_SPEED = 100;

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

	/**
	 * Offset (standoff distance) from the wall (cm).
	 */
	public static final int BAND_CENTER = 30;  

	/**
	 * Width of dead band (cm) i.e. error threshold.
	 */
	public static final int BAND_WIDTH = 8;

	/**
	 * Sleep interval = 50ms = 20 Hz.
	 */
	public static final int SLEEPINT = 50;

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
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

	/**
     * The light sensor.
     */
    public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
    
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
	 * Error margin from destination waypoint.
	 */
	public static final double ERROR_MARGIN = 0.5;
	
	/**
	 * Instance of US Localizer
	 */
	public static UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer();
	
	/** 
	 * Instance of Light Localizer
	 */
	public static LightLocalizer lightLocalizer = new LightLocalizer();
	
	/**
	 * d constant for ultrasonic localizer representing the threshold distance from the wall 
	 *
	 */
	public static double d = 45.0; 
	
	/**
	 * Degree at which to rotate right when executing US sensor localization.
	 */
	public static double ROTATION_RIGHT = 5.0;
	
	/**
	 * Degree at which to rotate left when executing US sensor localization.
	 */
	public static double ROTATION_LEFT = -5.0;

	/**
     * The distance from the robot's wheelBase center to light sensor.
     */
    public static final double DIST_CENTRE_TO_LIGHT_SENSOR = 13.9;
    
    
    // Catapult constants
    /**
     * The rotation speed of launch motor.
     */
    public static final int LAUNCH_MOTOR_SPEED = 800;
    
    /**
     * Acceleration of launch motor.
     */
    public static final int LAUNCH_MOTOR_ACCELERATOR = 2475; // 2475 
    
    /**
     * Launching angle of launch motor
     */
    public static final int LAUNCH_ANGLE = -40;
    
    /**
     * Ball launch range;
     */
    public static final double LAUNCH_RANGE = 120 + 0.5 * TILE_SIZE;
    
}

