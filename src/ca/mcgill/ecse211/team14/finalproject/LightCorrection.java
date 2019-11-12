package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * Class that implements light correction for navigation using two light
 * sensors. 
 * 
 * @author Michael Li
 */
public class LightCorrection {
	
	/**
	 * Maximum ratio for last intensity compared to current intensity when there is no black line. 
	 */
	protected static final double INTENSITY_RATIO = 1.3;
	
	/**
	 * Boolean to specify if right motor has touched black line.
	 */
	protected boolean rightMotorTouched = false; 
	
	/**
	 * Boolean to specify if left motor has touched black line.
	 */
	protected boolean leftMotorTouched = false; 
	
	/**
	 * Initial left light sensor intensity.
	 */
	protected double leftLastIntensity = -1;
	
	/**
	 * Initial right light sensor intensity.
	 */
	protected double rightLastIntensity = -1;
	
	/**
	 * Variable to keep track if 
	 */

	/**
	 * Perform an action based on the light data input. Method that corrects the robot's position during navigation. 
	 * If the light sensors detect the black lines asynchronously, it will stop the motor that
	 * has detected the black line first until the second motor detects the black line.
	 * 
	 * @param leftCurIntensity: Intensity measured by left light sensor 
	 * @param rightCurIntensity: Intensity measured by right light sensor 
	 */
	public void processLightData(double leftCurIntensity, double rightCurIntensity) {
			
			// See if left motor has touched line. 
			if (leftLastIntensity / leftCurIntensity > INTENSITY_RATIO) {
				leftMotorTouched = true;
			} 
			leftLastIntensity = leftCurIntensity;
			
			if (leftMotorTouched) {
				// Stop left motor 
				LEFT_MOTOR.stop();
				leftMotorTouched = false;
			}
			
			// See if right motor has touched line.
			if (rightLastIntensity / rightCurIntensity > INTENSITY_RATIO ) {
				rightMotorTouched = true; 
			}
			rightLastIntensity = rightCurIntensity;
			
			if (rightMotorTouched) {
				// Stop right motor 
				RIGHT_MOTOR.stop();
				rightMotorTouched = false;
			}		
		}			
	}

