package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import lejos.hardware.Sound;

/**
 * This class contains methods needed for light sensor localization. These methods guide the robot to waypoint
 * (1,1) more precisely after ultrasonic sensor has been used. The robot will drive around in a circle for 360
 * degrees and detect four black lines in the process. The Theta reading when these lines are touched are stored
 * into an array. Then error in X, Y, Theta odometer readings are adjusted by calculated values. Finally, the robot
 * navigates to (1,1) again in main class and the main class should use reOrient() method to orient itself towards
 * 0 degree axis.
 */
public class LightLocalizer {
    /**
     * Lower bound for non-black line intensity. When intensity falls below this value, black lines are met.
     */
	private static final int MINIMUM_NONBLACK_INTENSITY = 20; 
	
	/**
	 * Maximum ratio for last intensity compared to current intensity when there is no black line. 
	 */
	private static final double INTENSITY_RATIO = 1.3;
	
	// An array to store theta readings when light sensor intersects 4 black line, X-negative, Y-positive, X-
	// positive, Y-negative, listed in intersection time order
	private double[] intersectionDegrees = new double[4];
	
	// Index for tracing black lines already intersected by robot up to now
	private int lineCount = 0;
	
	// A boolean to specify if black line is touched
	private boolean lineTouched = false; 
	
	// Initial lastIntensity
	private double lastIntensity = -1;
	
	// A boolean to specify if light localization has started
	private boolean localizerStarted = false;  

	/**
	 * This method controls how the robot moves when using the light sensor to localize. The
	 * robot would turn around for 360 degrees and then stop. After that, it will
	 * adjust the odometer readings based on theta reading when black lines are met.
	 */
	public void localize() {
		
		// Set motor speed 
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		// Rotate 360 degrees 
		LEFT_MOTOR.rotate(Converter.convertAngle(360), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(360), false);
		
		// Stop motor 
		navigator.stop();
		
		// Adjust odometer reading
		adjustOdometer(intersectionDegrees);
	}

	/**
	 * At the end of light localization, orient robot back to 0 degrees w.r.t the
	 * Y-axis.
	 */
	public void orientTo0() {
		if (odometer.getXYT()[2] > 180) {
			navigator.turnTo(360 - odometer.getXYT()[2]); // Turn right
		} else {
			navigator.turnTo(0 - odometer.getXYT()[2]); // Turn Left
		}
	}

	/**
	 * Process data fetched from light sensor. The boolean lineTouched is set either true or false based on
	 * current intensity. And if the boolean localizerStarted is true, the theta reading will be stored into
	 * intersectionDegrees.
	 * @param curIntensity
	 */
	public void processData(int curIntensity) {
		
		// Trigger correction when a black line is detected
		if (curIntensity < MINIMUM_NONBLACK_INTENSITY) {
			lineTouched = true;
			Sound.beep();
		} else if (lastIntensity / (double) curIntensity > INTENSITY_RATIO) {
			lineTouched = true;
			Sound.beep();
		} else {
			lineTouched = false;
		}

		lastIntensity = curIntensity;
		
		// Store data into intersectionDegrees array
		if (lineTouched && localizerStarted) {
			intersectionDegrees[lineCount] = odometer.getXYT()[2];
			lineCount++;
			if (lineCount == 4)
				localizerStarted = false;
		}
	}

	/**
	 * THe input is a double array which stores the odometer's theta reading when
	 * black lines are met. This method will calculate delta theta, delta x and
	 * delta y.
	 * 
	 * @param intersectionDegrees
	 */
	private void adjustOdometer(double[] intersectionDegrees) {
		// Calculate and adjust theta
		double deltaTheta = -intersectionDegrees[3] + 276 + (intersectionDegrees[3] - intersectionDegrees[1]) / 2;
		double adjustedTheta = odometer.getXYT()[2] + deltaTheta;
		if (adjustedTheta > 360)
			adjustedTheta -= 360;
		else if (adjustedTheta < 0)
			adjustedTheta += 360;
		odometer.setTheta(adjustedTheta);

		// Calculate delta x and delta y.
		double angle_posXToNegX = intersectionDegrees[2] - intersectionDegrees[0];
		double angle_negYToPosY = intersectionDegrees[3] - intersectionDegrees[1];
		double[] deltaXY = new double[2];
		deltaXY[1] = Math.cos(Math.toRadians(normalizeTheta(angle_posXToNegX) / 2.0)) * DIST_CENTRE_TO_LIGHT_SENSOR;
		deltaXY[0] = Math.cos(Math.toRadians(normalizeTheta(angle_negYToPosY) / 2.0)) * DIST_CENTRE_TO_LIGHT_SENSOR;
		
		// Find quadrant
		int quadrant = findQuadrant(angle_posXToNegX, angle_negYToPosY);
		
		// Adjust X and Y reading based on quadrant
		switch (quadrant) {
		case 1:
			odometer.setX(TILE_SIZE + deltaXY[0]);
			odometer.setY(TILE_SIZE + deltaXY[1]);
			break;
		case 2:
			odometer.setX(TILE_SIZE + deltaXY[0]);
			odometer.setY(TILE_SIZE - deltaXY[1]);
			break;
		case 3:
			odometer.setX(TILE_SIZE - deltaXY[0]);
			odometer.setY(TILE_SIZE - deltaXY[1]);
			break;
		case 4:
			odometer.setX(TILE_SIZE - deltaXY[0]);
			odometer.setY(TILE_SIZE + deltaXY[1]);
			break;
		}
	}

	/**
	 * Find which quadrant the robot's center is currently in.
	 * 
	 * @param angleX
	 * @param angleY
	 * @return the quadrant where the robot is in, quadrant is represented in terms of int number 1, 2, 3, 4
	 */
	private int findQuadrant(double angleX, double angleY) {
		// Wheel Center in 1st quadrant
		if (!isWithin180Degrees(angleX) && !isWithin180Degrees(angleY))
			return 1;
		// Wheel Center in 2nd quadrant
		if (isWithin180Degrees(angleX) && !isWithin180Degrees(angleY))
			return 2;
		// Wheel Center in 3rd quadrant
		if (isWithin180Degrees(angleX) && isWithin180Degrees(angleY))
			return 3;
		// Wheel Center in 4th quadrant
		return 4;
	}

	/**
	 * Determine if an angle is within the range 0~180, important for finding out
	 * which quadrant robot is in.
	 * 
	 * @param angle
	 * @return a boolean true if an angle is within 180 degrees, false otherwise
	 */
	private boolean isWithin180Degrees(double angle) {
		return (angle >= 0 && angle <= 180);
	}

	/**
	 * Change angle that is either less than 0 or greater than 180 degrees to a
	 * range 0~180.
	 * 
	 * @param angle
	 * @return a normalized angle
	 */
	private double normalizeTheta(double angle) { // Based on cos property it may not be necessary
		if (angle < 0)
			return -angle;
		if (angle > 180)
			return 360 - angle;
		return angle;
	}
	
	/**
	 * Getter method for boolean lineTouched.
	 * 
	 * @return value of boolean lineTouched
	 */
	public boolean getLineTouched() {
		return this.lineTouched;
	}
	

	/**
	 * Setter method for boolean localizerStarted.
	 * 
	 * @param input
	 */
	public void setlocalizerStarted(boolean input) {
		this.localizerStarted = input;
	}
}
