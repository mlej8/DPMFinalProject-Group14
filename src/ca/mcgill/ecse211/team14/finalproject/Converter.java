package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.TRACK;
import static ca.mcgill.ecse211.team14.finalproject.Resources.WHEEL_RAD;

/**
 * Utilities class that contains methods for converting distances and angles for
 * the motors.
 * 
 * @author Michael Li
 */

public class Converter {

	/**
	 * Converts input distance to the total rotation of each wheel needed to cover
	 * that distance.
	 * 
	 * @param distance
	 * @return the wheel rotations necessary to cover the distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the
	 * robot by that angle.
	 * 
	 * @param angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance(Math.PI * TRACK * angle / 360.0);
	}
}
