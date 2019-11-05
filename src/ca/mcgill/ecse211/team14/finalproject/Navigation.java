package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

public class Navigation {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false; // false by default

	/**
	 * Navigation class implements the singleton pattern
	 */
	private Navigation() {

	}

	/**
	 * Get instance of the Navigation class. Only allows one thread at a time
	 * calling this method.
	 */
	public synchronized static Navigation getNavigator() {
		if (navigator == null) {
			navigator = new Navigation();
		}
		return navigator;
	}

	/**
	 * Method that stops the robot completely.
	 */
	public void stop() {
		LEFT_MOTOR.stop(true);
		RIGHT_MOTOR.stop(false);
	}

	/**
	 * This method causes the robot to travel to the absolute field location (x, y),
	 * specified in tile points. This method should continuously call turnTo(double
	 * theta) and then set the motor speed to forward (straight). This will make
	 * sure that your heading is updated until you reach your exact goal. This
	 * method will poll the odometer for information.
	 * @param x: destination x coordinates 
	 * @param y: destination y coordinates 
	 */
	public void travelTo(double x, double y) {
		
		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		// First travel along X-axis, then travel along Y-axis
		if (dx >= 0) {
			turnTo2(90);			
		} else {
			turnTo2(270);
		}
		
		navigateForward(dx);
		
		// Second travel along Y-axis
		if (dy >= 0) {
			turnTo2(0);
		} else {
			turnTo2(180);
		}
		
		navigateForward(dy);
		
//		// Compute the angle needed to turn; dx and dy are intentionally switched in
//		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
//		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];

		// Turn to the correct angle
//		turnTo(theta);

		// Turn on motor

		// Once the destination is reached, stop both motors
		stop();
		this.traveling = false;
	}

	/**
	 * This method returns true if another thread has called travelTo() or turnTo()
	 * and the method has yet to return; false otherwise.
	 */
	public boolean isNavigating() {
		return this.traveling;
	}
	
	/**
	 * This method causes the robot to navigate forward for a set amount of distance.
	 * 
	 * @Param distance: distance to travel.
	 */
	public void navigateForward(double distance) {
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.rotate(Converter.convertDistance(distance), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), false);
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn using the MINIMAL angle to its target.
	 */
	public void turnTo(double theta) {

		// Set traveling to true when the robot is turning
		this.traveling = true;

		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = 360.0 - theta;
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Set motors' speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);
	}
	
	/**
	 *
	 * Method that causes the robot to turn to an exact angle with respect to the Y axis.
	 *
	 * @param theta
	 */
	public void turnTo2(double theta) {

		// Set traveling to true when the robot is turning
		this.traveling = true;
		
		// Angle needed to turn to the required angle
		theta -= odometer.getXYT()[2];

		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = 360.0 - theta;
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Set motors' speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);
	}

	/**
	 * Method that rotates continuously (without waiting for the move to complete)
	 * according to the input theta.
	 * 
	 * @param theta that is the angle at which the robot needs to turn
	 */
	public void rotate(double theta) {
		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), true);
	}

	/**
	 * Getters method for traveling.
	 * 
	 * @return traveling
	 */
	public boolean isTraveling() {
		return this.traveling;
	}

	/**
	 * Setter method for traveling.
	 * 
	 * @param traveling
	 */
	public void setTraveling(boolean traveling) {
		this.traveling = traveling;
	}
}
