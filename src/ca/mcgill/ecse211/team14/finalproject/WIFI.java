package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

import java.math.BigDecimal;
import java.util.Map;

import ca.mcgill.ecse211.wificlient.WifiConnection;

public class WIFI {

	/**
	 * Variable target's x coordinate.
	 */
	private double binX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double binY;

	/**
	 * Variable start point x coodinate.
	 */
	private double startX;
 
	/**
	 * Variable start point y coodinate.
	 */
	private double startY;

	/**
	 * Variable destination's x coordinate.
	 */
	private double launchX = bin.x;

	/**
	 * Variable destination's y coordinate.
	 */
	private double launchY = bin.y;

	/**
	 * Array that stores the lower left and upper right corners of islands.
	 */
	private double[] islandCoordinates;

	/**
	 * Array that stores the tunnel coordinates.
	 */
	private double[] tunnelCoordinates;

	/**
	 * Tunnel entrance X coordinate.
	 */
	private double tunnelEnX;

	/**
	 * Tunnel entrance Y coordinate.
	 */
	private double tunnelEnY;

	/**
	 * Tunnel exit X coordinate.
	 */
	private double tunnelExX;
	
	/**
	 * Tunnel exit Y coordinate.
	 */
	private double tunnelExY;
	/**
	 * This method uses the given target position (binX,binY) to find the ideal
	 * launching position.
	 */
	private void findLaunchPosition() {
		this.launchX = binX;
		this.launchY = binY;	
	}

	/**
	 * Changes the starting position (x,y).
	 */
	public void findStartPoint() {
		startCorner = 0;
		if (redTeam == TEAM_NUMBER) {
			startCorner = redCorner;
		} else {
			startCorner = greenCorner;
		}
		// For beta demo
		switch (startCorner) {
		case 0:
			startX = 0.5 * TILE_SIZE;
			startY = 0.5 * TILE_SIZE;
			break;
		case 1:
			startX = (mapWidth - 0.5) * TILE_SIZE;
			startY = 0.5 * TILE_SIZE;
			break;
		case 2:
			startX = (mapWidth - 0.5) * TILE_SIZE;
			startY = (mapHeight - 0.5) * TILE_SIZE;
			break;
		case 3:
			startX = 0.5 * TILE_SIZE;
			startY = (mapHeight - 0.5) * TILE_SIZE;
			break;
		}
	}

	/**
	 * Returns the double array [startX, startY]
	 */
	public double[] getStartPoint() {
		double[] startPoint = new double[] { startX, startY };
		return startPoint;
	}

	/**
	 * Method that calculates and returns the coordinates at which the robot needs
	 * to travel to in order to enter the tunnel and exit the tunnel. 
	 * In final competition, we might split it into getTunnelEntrance and getTunnelExit
	 */
	public void findTunnelEnEx() {
		Region tunnelArea = null;
		Region startArea = green; // assume green team in beta-demo

		if(greenTeam == 14) {
		  	tunnelArea = tng;
		}       

		double x = 0;
		double y = 0;
		if (x <= y) {
			// go vertically
			if (startY < tunnelArea.ll.y) { // below
				x = tunnelArea.ll.x + 0.5;
				y = tunnelArea.ll.y - 1.0;
			} else { // upper
				x = tunnelArea.ur.x - 0.5;
				y = tunnelArea.ur.y + 1.0;
			}
			
		} else {
			// go horizontally
			if (startX < tunnelArea.ll.x) { // left
				x = tunnelArea.ll.x - 1.0;
				y = tunnelArea.ll.y + 0.5;
			} else {
				x = tunnelArea.ur.x + 1.0; // right
				y = tunnelArea.ur.y - 0.5;
			}
			// 1. turn to the same angle every time?
			// 2. Avoid touching the river/wall in edge cases	
		}
		tunnelEnX = x*TILE_SIZE;
		tunnelEnY = y*TILE_SIZE;
		if (x <= y) {
			tunnelExX = x * TILE_SIZE;
			tunnelExY = (y + 4) * TILE_SIZE;
		} else {
			tunnelExX = (x + 4) * TILE_SIZE;
			tunnelExY = y;
		}
	}

	/**
	 * Method that calculates and returns the coordinates at which the robot needs
	 * to travel to in order to exit the tunnel.
	 * 
	 * In beta-demo, this is combined with findTunnelEntrance method
	 */
//	public double[] getTunnelExit() {
//		// TODO: merge into getEntrance
//		System.out.println(" ");
//		return islandCoordinates;
//	}

	public double getBinX() {
		return binX;
	}

	public double getBinY() {
		return binY;
	}

	/**
	 * @return launch point's x coordinate.
	 */
	public double getlaunchX() {
		return this.launchX;
	}

	/**
	 * @return launch point's y coordinate.
	 */
	public double getlaunchY() {
		return this.launchY;
	}

	public double getStartX() {
		return startX;
	}

	public double getStartY() {
		return startY;
	}

	public double[] getIslandCoordinates() {
		return islandCoordinates;
	}

	public double[] getTunnelCoordinates() {
		return tunnelCoordinates;
	}

	public double getTunnelEnX() {
		return tunnelEnX;
	}

	public void setTunnelEnX(double tunnelEnX) {
		this.tunnelEnX = tunnelEnX;
	}

	public double getTunnelEnY() {
		return tunnelEnY;
	}

	public void setTunnelEnY(double tunnelEnY) {
		this.tunnelEnY = tunnelEnY;
	}

	public double getTunnelExX() {
		return tunnelExX;
	}

	public void setTunnelExX(double tunnelExX) {
		this.tunnelExX = tunnelExX;
	}

	public double getTunnelExY() {
		return tunnelExY;
	}

	public void setTunnelExY(double tunnelExY) {
		this.tunnelExY = tunnelExY;
	}
	
    // Set these as appropriate for your team and current situation
    /**
     * The default server IP used by the profs and TA's.
     */
    public static final String DEFAULT_SERVER_IP = "192.168.2.3";
    
    /**
     * The IP address of the server that transmits data to the robot. Set this to the default for the
     * beta demo and competition.
     */
    public static final String SERVER_IP = "192.168.2.12"; //TODO: change when testing
    
    /**
     * Your team number.
     */
    public static final int TEAM_NUMBER = 14;
    
    /** 
     * Enables printing of debug info from the WiFi class. 
     */
    public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
    
    /**
     * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
     */
    public static final boolean RECEIVE_WIFI_PARAMS = true;
    
    // DECLARE YOUR CURRENT RESOURCES HERE
    // eg, motors, sensors, constants, etc
    //////////////////////////////////////
    
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
    public static int mapWidth = 8;

    /**
     * Width of the whole map. Currently fits beta demo: 9 for competition.
     */
    public static int mapHeight = 8;

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
    // public static Region tnr = new Region("TNR_LL_x", "TNR_LL_y", "TNR_UR_x", "TNR_UR_y");
    
    /**
     * Beta-demo: the given rotation angle before launching
     */
    public static double targetAngle = Math.max(get("TNR_LL_x"), get("TNR_UR_x"));

    /**
     * The green tunnel footprint.
     */
    public static Region tng = new Region("TNG_LL_x", "TNG_LL_y", "TNG_UR_x", "TNG_UR_y");

    /**
     * The location of the target bin.
     */
    public static Point bin = new Point(get("BIN_x"), get("BIN_y"));    
    
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
