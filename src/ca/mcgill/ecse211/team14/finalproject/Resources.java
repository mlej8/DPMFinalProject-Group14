package ca.mcgill.ecse211.team14.finalproject;

import ca.mcgill.ecse211.team14.finalproject.Odometer;
import lejos.hardware.Wifi;
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
 * Class that stores all constants.
 */
public class Resources {
//Set these as appropriate for your team and current situation
  /**
   * The default server IP used by the profs and TA's.
   */
  public static final String DEFAULT_SERVER_IP = "192.168.2.3"; 
  
  /**
   * The IP address of the server that transmits data to the robot. Set this to the default for the
   * beta demo and competition.
   */
  public static final String SERVER_IP = "192.168.43.171";    //TODO: change this
  
  /**
   * Your team number.
   */
  public static final int TEAM_NUMBER = 14;
  
  /** 
   * Enables printing of debug info from the WiFi class. 
   */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = false;
  
  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   */
  public static boolean RECEIVE_WIFI_PARAMS = true;
	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.05; 

	/**
	 * The robot's width in centimeters.
	 */
	public static final double TRACK = 15.45; // 15.10

	/**
	 * Turning 90 degrees to start position parallel the wall. 
	 */
	public static final double RIGHT_ANGLE = 90.0;
	
	/**
	 * Fixed Motor Speed.
	 */
	public static final int MOTOR_SPEED = 60;

    /**
     * Fixed Motor Speed.
     */
    public static final int DASH_SPEED = 150;
	
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
	public static final int ACCELERATION = 1000;
	
	/**
	 * Tunnel speed. 
	 */
	public static final int TUNNEL_SPEED = 300;
	
	/**
	 * Tunnel sleep.
	 */
	public static final int TUNNEL_SLEEP= 500;
    
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
	 * Error margin at each waypoint.
	 */
	public static final double ERROR_MARGIN = 4.0;
	
	/**
	 * Sleep interval = 50ms = 20 Hz.
	 */
	public static final int SLEEPINT = 50;

   /**
     * Sleep interval for light sensor.
     */
    public static final int LIGHT_SLEEPINT = 40;
	
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
	 * The front ultrasonic sensor.
	 */
	public static final EV3UltrasonicSensor usSensorFront = new EV3UltrasonicSensor(SensorPort.S3);
	
	/**
     * The left ultrasonic sensor.
     */
//    public static final EV3UltrasonicSensor usSensorLeft= new EV3UltrasonicSensor(SensorPort.S2);

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
     * Threshold to activate object avoidance.
     */
    public static double THRESHOLD = 20;
	
	/**
	 * d constant for ultrasonic localizer representing the threshold distance from the wall 
	 *
	 */
	public static final double d = 60.0; 
	
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
    public static final int LAUNCH_MOTOR_SPEED = 350;
    
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
     * Launch range of the ball launcher
     */
    public static final double LAUNCH_RANGE = 6.5;    //TODO: change the range
    
    /**
     * Create light corrector 
     */
    public static LightCorrection lightCorrector = new LightCorrection();
    
    /**
     * Container for the Wi-Fi parameters.
     */
    public static Map<String, Object> wifiParameters;
    
    // This static initializer MUST be declared before any Wi-Fi parameters.
    static {
      receiveWifiParameters();
    }
    
    /**
     * Red team number.
     */
    public static int redTeam = get("RedTeam");

    /**
     * Red team's starting corner.
     */
    public static int redCorner = get("RedCorner");

    /**
     * Height of the whole map. Currently fits beta demo: 15 for competition.
     */
    public static int mapWidth = 15;

    /**
     * Width of the whole map. Currently fits beta demo: 9 for competition.
     */
    public static int mapHeight = 9;

    /**
     * Green team number.
     */
    public static int greenTeam = get("GreenTeam");

    /**
     * Green team's starting corner.
     */
    public static int greenCorner = get("GreenCorner");

    /**
     * Actual Starting Corner of Robot
     */
    public static int startCorner;
    /**
     * The Red Zone.
     */
    public static Region red = new Region("Red_LL_x", "Red_LL_y", "Red_UR_x", "Red_UR_y");

    /**
     * The Green Zone.
     */
    public static Region green = new Region("Green_LL_x", "Green_LL_y", "Green_UR_x", "Green_UR_y");

    /**
     * The Island.
     */
    public static Region island =
        new Region("Island_LL_x", "Island_LL_y", "Island_UR_x", "Island_UR_y");

    /**
     * The red tunnel footprint.
     */
     public static Region tnr = new Region("TNR_LL_x", "TNR_LL_y", "TNR_UR_x", "TNR_UR_y");
    
    /**
     * Beta-demo: the given rotation angle before launching
     */
    public static double targetAngle = Math.max(get("TNR_LL_x"), get("TNR_UR_x"));

    /**
     * The green tunnel footprint.
     */
    public static Region tng = new Region("TNG_LL_x", "TNG_LL_y", "TNG_UR_x", "TNG_UR_y");

    /**
     * The location of the red target bin.
     */
    public static Point redBin = new Point(get("Red_BIN_x"), get("Red_BIN_y"));

    /**
     * The location of the green target bin.
     */
    public static Point greenBin = new Point(get("Green_BIN_x"), get("Green_BIN_y")); 
    
    /**
     * Receives Wi-Fi parameters from the server program.
     */
    public static void receiveWifiParameters() {
      // Only initialize the parameters if needed
      if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
        return;
      }
      System.out.println("Waiting to receive Wi-Fi parameters.");

      // Connect to server and get the data, catching any errors that might occur
      try (WifiConnection conn =
          new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
        /*
         * getData() will connect to the server and wait until the user/TA presses the "Start" button
         * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
         * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
         * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
         * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
         * but receives corrupted data or a message from the server saying something went wrong. For
         * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
         * will receive a message saying an invalid team number was specified and getData() will throw
         * an exception letting you know.
         */
        wifiParameters = conn.getData();
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
    
    /**
     * Returns the Wi-Fi parameter int value associated with the given key.
     * 
     * @param key the Wi-Fi parameter key
     * @return the Wi-Fi parameter int value associated with the given key
     */
    public static int get(String key) {
      if (wifiParameters != null) {
        return ((BigDecimal) wifiParameters.get(key)).intValue();
      } else {
        return 0;
      }
    }
    
    /**
     * Represents a region on the competition map grid, delimited by its lower-left and upper-right
     * corners (inclusive).
     * 
     * @author Younes Boubekeur, Weijing Zhang, Yilin Jiang
     */
    public static class Region {
      /** The lower left corner of the region. */
      public Point ll;
      
      /** The upper right corner of the region. */
      public Point ur;
      
      /** The width of the region */
      public double width;

      /** The height of the region */
      public double height;

      /**
       * Constructs a Region.
       * 
       * @param lowerLeft the lower left corner of the region
       * @param upperRight the upper right corner of the region
       */
      public Region(Point lowerLeft, Point upperRight) {
        validateCoordinates(lowerLeft, upperRight);
        ll = lowerLeft;
        ur = upperRight;
        width = ur.x - ll.x;
        height = ur.y - ll.y;
      }
      
      /**
       * Helper constructor to make a Region directly from parameter names.
       * 
       * @param llX
       *     the Wi-Fi parameter key representing the lower left corner of the region x coordinate
       * @param llY
       *     the Wi-Fi parameter key representing the lower left corner of the region y coordinate
       * @param urX 
       *     the Wi-Fi parameter key representing the upper right corner of the region x coordinate
       * @param urY
       *     the Wi-Fi parameter key representing the upper right corner of the region y coordinate
       */
      public Region(String llX, String llY, String urX, String urY) {
        this(new Point(get(llX), get(llY)), new Point(get(urX), get(urY)));
      }
      
      /**
       * Validates coordinates.
       * 
       * @param lowerLeft the lower left corner of the region
       * @param upperRight the upper right corner of the region
       */
      private void validateCoordinates(Point lowerLeft, Point upperRight) {
        if (lowerLeft.x > upperRight.x || lowerLeft.y > upperRight.y) {
          throw new IllegalArgumentException(
              "Upper right cannot be below or to the left of lower left!");
        }
      }
      
      public String toString() {
        return "[" + ll + ", " + ur + "]";
      }
    }
    
    /**
     * Represents a coordinate point on the competition map grid.
     * 
     * @author Younes Boubekeur
     */
    public static class Point {
      /** The x coordinate. */
      public double x;
      
      /** The y coordinate. */
      public double y;
      
      /**
       * Constructs a Point.
       * 
       * @param x the x coordinate
       * @param y the y coordinate
       */
      public Point(double x, double y) {
        this.x = x;
        this.y = y;
      }
      
      public String toString() {
        return "(" + x + ", " + y + ")";
      }      
    }    
}
