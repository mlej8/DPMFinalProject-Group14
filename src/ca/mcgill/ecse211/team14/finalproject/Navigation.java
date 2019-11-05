package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

public class Navigation {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false; // false by default

	/**
	 * Variable destination's x coordinate.
	 */
	private double launchX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double launchY;

	/**
	 * Variable target's x coordinate.
	 */
	private double targetX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double targetY;

	/**
	 * Origin (1,1) coordinates.
	 */
	private static final double originX = 1 * TILE_SIZE;
	private static final double originY = 1 * TILE_SIZE;

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
	 * Method that travels to the origin (1,1).
	 */
	public void travelToOrigin() {

		// Navigate to origin (1,1)
		navigator.travelTo(originX, originY);

		// Sleep while it is traveling
		while (navigator.isNavigating()) {
			Main.sleepFor(10 * SLEEPINT);
		}
	}

	/**
	 * Method that travels to the launch point (launchX, launchY).
	 */
	public void travelToLaunchPoint() {
		navigator.travelTo(this.launchX, this.launchY);
	}

	/**
	 * Find current robot position after ultrasonic localization by detecting the
	 * grid lines assuming the robot is currently on the diagonal line (45 degrees)
	 * of a tile.
	 */
	public void findRobotPosition() { // TODO CHANGE 

		// Set robot speeds
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);

		// Reset tacho counts
		LEFT_MOTOR.resetTachoCount();
		RIGHT_MOTOR.resetTachoCount();

		// Travel forward until a line is detected
		while (!lightLocalizer.getLineTouched()) {
			LEFT_MOTOR.forward();
			RIGHT_MOTOR.forward();
		}
		// Stop robot once it detects the black line
		stop();

		// Note the distance it traveled
		int tachCountLeft = LEFT_MOTOR.getTachoCount();
		int tachCountRight = RIGHT_MOTOR.getTachoCount();

		// Go Back to starting position
		LEFT_MOTOR.rotate(-tachCountLeft, true);
		RIGHT_MOTOR.rotate(-tachCountRight, false);

		// Position of the center of rotation
		double distToGridLine = Math.PI * WHEEL_RAD * (tachCountLeft) / 180 - DIST_CENTRE_TO_LIGHT_SENSOR;

		// Assuming the robot is on the diagonalize line of the tile, the horizontal
		// distance is equal to the vertical distance
		odometer.setX(TILE_SIZE - distToGridLine);
		odometer.setY(TILE_SIZE - distToGridLine);
		lightLocalizer.setlocalizerStarted(true);
	}

	/**
	 * Method that stops the robot completely.
	 */
	public void stop() {
		LEFT_MOTOR.stop(true);
		RIGHT_MOTOR.stop(false);
	}

	public void travelTo(double x, double y) {
		/**
		 * This method causes the robot to travel to the absolute field location (x, y),
		 * specified in tile points. This method should continuously call turnTo(double
		 * theta) and then set the motor speed to forward (straight). This will make
		 * sure that your heading is updated until you reach your exact goal. This
		 * method will poll the odometer for information.
		 */
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
	 * This method causes the robot to navigate foward
	 * 
	 * @Param distance to travel
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
	 * This method causes the robot to turn to an exact angle.
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
	 * This method uses the given target position (targetX,targetY) to find the
	 * ideal launching position.
	 */
	public void findDestination() {
		double currentX = odometer.getXYT()[0];
		double currentY = odometer.getXYT()[1];
		double[] curPosition = new double[] { currentX, currentY };
		double[] throwTo = new double[] { targetX, targetY };

		double theta = Math.atan2(currentX - targetX, currentY - targetY);

		double dx, dy;
		// calculate the intersection of the circle and the line
		if (theta < 0) { // when the robot is in 2nd/3rd quadrant
			dy = LAUNCH_RANGE * Math.cos(-theta);
			dx = -LAUNCH_RANGE * Math.sin(-theta);
			this.launchY = targetY + dy;
			this.launchX = targetX + dx;
		} else { // in 1st/4th quadrant
			dy = LAUNCH_RANGE * Math.cos(theta);
			dx = LAUNCH_RANGE * Math.sin(theta);
			this.launchY = targetY + dy;
			this.launchX = targetX + dx; 
		}

		if (launchX <= 15 || launchY <= 15) {
			double[] target = findCircle(curPosition, throwTo);
			this.launchX = target[0];
			this.launchY = target[1];
		}
	}

	public void findDestination2() {

		// Compute angle towards the destination
		// Compute displacement
		double dx = targetX - odometer.getXYT()[0];
		double dy = targetY - odometer.getXYT()[1];

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];
		// Turn to destination
		navigator.turnTo(theta);
	}

	/**
	 * 
	 * @param curPos
	 * @param center
	 * @return
	 */
	private static double[] findCircle(double[] curPos, double[] center) {
		double[] target = new double[2];
		if (center[0] > center[1]) { // upper half
			double tX = curPos[0];
			double tY = Math.sqrt(Math.pow(LAUNCH_RANGE, 2) - Math.pow((curPos[0] - center[0]), 2)) + center[1];
			target = new double[] { tX, tY };
		} else { // lower half
			double tY = curPos[1];
			double tX = Math.sqrt(Math.pow(LAUNCH_RANGE, 2) - Math.pow((curPos[1] - center[1]), 2)) + center[0];
			target = new double[] { tX, tY };
		}
		return target;
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

	public double getTargetX() {
		return targetX;
	}

	public double getTargetY() {
		return targetY;
	}

}
