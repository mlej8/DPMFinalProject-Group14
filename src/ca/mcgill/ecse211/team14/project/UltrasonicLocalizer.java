package ca.mcgill.ecse211.team14.project;

import static ca.mcgill.ecse211.team14.project.Resources.*;

import ca.mcgill.ecse211.team14.finalproject.Main;
import ca.mcgill.ecse211.team14.finalproject.UltrasonicController;

/**
 * Class that uses the ultrasonic sensor to localize itself assuming the robot
 * will start with its center of rotation in the bottom left tile of the field.
 * Its initial orientation (heading) is assumed to be unknown. Angles will be
 * measured clockwise w.r.t. the positive y-axis. Use the ultrasonic sensor to
 * measure the distance to the two walls nearest the robot to determine the
 * initial orientation of the robot by rotating 360 degrees on itself.
 * 
 * @author Michael Li
 */
public class UltrasonicLocalizer extends UltrasonicController {

	/**
	 * Method that executes ultrasonic localization by using falling edge technique.
	 */
	public void fallingEdge() {

		Main.sleepFor(SLEEPINT);

		// Falling edge is the point at which the measured distance is smaller than d -
		// NOISE_MARGIN
		double backWall, leftWall, correctionAngle;

		// Get angle at which the back and left walls are detected.
		backWall = findFallingEdgeA();

		navigator.sleepNavigation();

		// Detect left wall
		leftWall = findFallingEdgeB();

		// Get the angle to be added to the heading reported by the odometer to correct
		// the robot's current orientation
		correctionAngle = getCorrectionAngle(backWall, leftWall);

		// Correct odometer's current orientation
		if (correctionAngle + odometer.getXYT()[2] > 0) {
			odometer.setTheta(correctionAngle + odometer.getXYT()[2]);
		} else {
			// if currentAngle + odometer.getXYT()[2] is negative, rescale it on 360
			// degrees.
			odometer.setTheta(360 + correctionAngle + odometer.getXYT()[2]);
		}

		// Turn to 0 degrees w.r.t. the Y-axis using the minimal angle.
		if (odometer.getXYT()[2] > 180) {
			navigator.turnTo(360 - odometer.getXYT()[2]);
		} else {
			navigator.turnTo(0 - odometer.getXYT()[2]);
		}
	}

	/**
	 * Method that returns the angle at which the back wall is detected for the
	 * falling edge implementation.
	 * 
	 * @return average of the angle at which the robot enters the noise margin and
	 *         the angle at which the falling edge is detected.
	 */
	private double findFallingEdgeA() {

		double fallingEdge;

		// Rotate counter clockwise until the robot is out of the noise margin zone to
		// not be facing the wall
		while (readUSDistance() < d) {
			navigator.rotate(ROTATION_LEFT);
			Main.sleepFor(SLEEPINT);
		}

		navigator.sleepNavigation();

		// Turn right until the robot detects the falling edge for the back wall.
		while (readUSDistance() > d) {
			navigator.rotate(ROTATION_RIGHT);
			Main.sleepFor(SLEEPINT);
		}

		// Stop robot when it detects the falling edge.
		navigator.stop();

		// Store the angle at which the falling edge is detected.
		fallingEdge = odometer.getXYT()[2];

		return fallingEdge;
	}

	/**
	 * Method that returns the angle at which the left wall is detected for the
	 * falling edge implementation.
	 * 
	 * @return average of the angle at which the robot enter the noise margin and
	 *         the angle at which the falling edge is detected.
	 */
	private double findFallingEdgeB() {

		double fallingEdge;

		// Rotate counterclockwise to get out of noise margin.
		while (readUSDistance() < d + US_LOCALIZATION_THRESHOLD) {
			navigator.rotate(ROTATION_LEFT);
			Main.sleepFor(SLEEPINT);
		}

		navigator.sleepNavigation();

		// Turn left until the falling edge is detected for the left wall.
		while (readUSDistance() > d) {
			navigator.rotate(ROTATION_LEFT);
			Main.sleepFor(SLEEPINT);
		}

		// Stop robot when it detects the falling edge.
		navigator.stop();

		// Store the angle at which the falling edge is detected.
		fallingEdge = odometer.getXYT()[2];

		return fallingEdge;
	}

	/**
	 * Method that computes the angle to be added to the heading reported by the
	 * odometer to orient the robot correctly.
	 * 
	 * @param a:
	 *            angle at which the back wall is detected.
	 * @param b:
	 *            angle at which the left wall is detected.
	 * @return
	 */
	public double getCorrectionAngle(double a, double b) {
		if (a > b) {
			return (40 - ((a + b) / 2.0));
		} else {
			return (220 - ((a + b) / 2.0));
		}
	}

	@Override
	public void processUSData(int distance) {
		filter(distance);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
