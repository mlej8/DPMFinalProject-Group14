package ca.mcgill.ecse211.team14.finalproject;

/**
 * Controller that controls the robot's movements based on light data.
 */
public abstract class LightController {

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
	 * Perform an action based on the light data input.
	 * 
	 * @param leftCurIntensity: Intensity measured by left light sensor 
	 * @param rightCurIntensity: Intensity measured by right light sensor 
	 */
	public abstract void processLightData(int leftCurIntensity, int rightCurIntensity);
	
	/**
	 * Index for tracing black lines already intersected by robot up to now.
	 */
	protected int lineCount = 0;
	
	/**
	 * A boolean to specify if black line is touched.
	 */
	protected boolean lineTouched = false; 
	
	/**
	 * Initial left light sensor intensity.
	 */
	protected double leftLastIntensity = -1;
	
	/**
	 * Initial right light sensor intensity.
	 */
	protected double rightLastIntensity = -1;
	
	/**
	 * A boolean to specify if light localization has started.
	 */
	protected boolean localizerStarted = false; 
   
}
