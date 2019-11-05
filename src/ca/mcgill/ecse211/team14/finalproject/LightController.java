package ca.mcgill.ecse211.team14.finalproject;

/**
 * Controller that controls the robot's movements based on light data.
 */
public abstract class LightController {
	
    /**
    * Lower bound for non-black line intensity. When intensity falls below this value, black lines are met.
    */
	protected static final int MINIMUM_NONBLACK_INTENSITY = 20; 
	
	/**
	 * Maximum ratio for last intensity compared to current intensity when there is no black line. 
	 */
	protected static final double INTENSITY_RATIO = 1.3;
	
	/**
	 *  An array to store theta readings when light sensor intersects 4 black line, X-negative, Y-positive, X-
	 *  positive, Y-negative, listed in intersection time order.
	 */
	protected double[] intersectionDegrees = new double[4];
	
	/**
	 * Index for tracing black lines already intersected by robot up to now.
	 */
	protected int lineCount = 0;
	
	/**
	 * A boolean to specify if black line is touched.
	 */
	protected boolean lineTouched = false; 
	
	/**
	 * Initial lastIntensity.
	 */
	protected double lastIntensity = -1;
	
	/**
	 * A boolean to specify if light localization has started.
	 */
	protected boolean localizerStarted = false; 
   
}
