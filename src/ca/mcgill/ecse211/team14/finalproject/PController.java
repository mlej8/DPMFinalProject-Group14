package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * Class that implements the Wall Follower using Proportional Control controller to avoid obstacles during navigation.
 * 
 * @author Michael Li and Cecilia Jiang
 */

public class PController extends UltrasonicController {

	public static enum State {
		INIT, // When robot has not encountered a block
		TURNING, // When robot is turning aroung a block
		FOLLOWING_WALL, // When robot has completed turning
		PASSED
	};

	//
	public static State state = State.INIT;
	private static final int PROPORTION_GAIN_SCALE = 4;

	// Private booleans to keep track of which way we turned
	private static boolean turnedRight = false;
	private static boolean turnedLeft = false;

	public PController() {
	}

	/**
	 * Perform an action based on the US data input using the proportional control
	 * scheme, where the gain is proportional to the error. The magnitude of change
	 * in rotation is proportional to the magnitude of the error. Scaling the
	 * correction, i.e. DELTASPD, according to error (this is called proportional
	 * control).
	 * 
	 * @param distance: the distance between the US sensor and an obstacle in cm.
	 */
	@Override
	public void processUSData(int distance) {

		filter(distance);

		if (state == State.INIT && this.distance < THRESHOLD) {
			// If distance is smaller than threshold change to TURNING state
			state = State.TURNING;

			// Log current state
			System.out.println("Switched state to TURNING");

		} else if (state == State.TURNING) {
			// Get the sensor to look forward
			rotateMotor.rotate(-rotateMotor.getTachoCount(), false);
			navigatorObstacle.setTraveling(true);

			// Store variables
			double x = odometer.getXYT()[0];
			double y = odometer.getXYT()[1];
			double theta = odometer.getXYT()[2];

			if (x > 2 * TILE_SIZE && y > 2 * TILE_SIZE) {
				// If robot is on the first quadrant, i.e. top right

				if ((theta >= CLOCKWISE_LOWER_BOUND - RIGHT_ANGLE && theta <= 360)
						|| (theta >= 0 && theta <= CLOCKWISE_UPPER_BOUND - RIGHT_ANGLE)) {
					// If robot is traveling counterclockwise, turn left
					sharpTurnLeft();

				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND - RIGHT_ANGLE
						&& theta <= COUNTERCLOCKWISE_UPPER_BOUND - RIGHT_ANGLE) {
					// If going clockwise, turn right
					sharpTurnRight();
				}

			} else if (x < 2 * TILE_SIZE && y > 2 * TILE_SIZE) {
				// If robot is on the second quadrant, i.e. top left

				if ((theta >= CLOCKWISE_LOWER_BOUND && theta <= 360)
						|| (theta >= 0 && theta <= CLOCKWISE_UPPER_BOUND)) {
					// If robot is traveling clockwise, turn right
					sharpTurnRight();
				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND) {
					// If going counter clockwise, turn left
					sharpTurnLeft();
				}

			} else if (x < 2 * TILE_SIZE && y < 2 * TILE_SIZE) {
				// If robot is on the third quadrant, i.e. bottom left

				if ((theta >= CLOCKWISE_LOWER_BOUND - RIGHT_ANGLE && theta <= 360)
						|| (theta >= 0 && theta <= CLOCKWISE_UPPER_BOUND - RIGHT_ANGLE)) {
					// If robot is traveling clockwise, turn right
					sharpTurnRight();

				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND - RIGHT_ANGLE
						&& theta <= COUNTERCLOCKWISE_UPPER_BOUND - RIGHT_ANGLE) {
					// If going counter clockwise, turn left
					sharpTurnLeft();
				}

			} else if (x > 2 * TILE_SIZE && y < 2 * TILE_SIZE) {
				// If robot is on the forth quadrant, i.e. bottom right

				if ((theta >= CLOCKWISE_LOWER_BOUND && theta <= 360)
						|| (theta >= 0 && theta <= CLOCKWISE_UPPER_BOUND)) {
					// If robot is traveling counterclockwise, turn left
					sharpTurnLeft();

				} else if (theta >= COUNTERCLOCKWISE_LOWER_BOUND && theta <= COUNTERCLOCKWISE_UPPER_BOUND) {
					// If robot is traveling clockwise, turn right
					sharpTurnRight();
				}
			}

			// Log current state
			System.out.println("Navigation state switched to FOLLOWING_WALL");

			// Switch state to following wall
			state = State.FOLLOWING_WALL;

		} else if (state == State.FOLLOWING_WALL) {

			// Compute error
			int error = BAND_CENTER - this.distance; // (distance between the US sensor and an obstacle in cm) -
														// (Standard offset from the wall cm). We need to tweak
														// BAND_CENTER and BAND_WIDTH in order to make the robot
														// smooth.

			// Compute low and high speed using the calcGain function.
			int lowSpeed = MOTOR_SPEED - calcGain(error);
			int highSpeed = MOTOR_SPEED + calcGain(error);

			if (turnedRight) {
				if (Math.abs(error) <= BAND_WIDTH) {
					forward();
				} else if (error > 0) {
					turnRightR(highSpeed);
				} else if (error < 0) {
					turnLeftR(lowSpeed, highSpeed);
				}
			} else if (turnedLeft) {
				if (Math.abs(error) <= BAND_WIDTH) {
					forward();
				} else if (error > 0) {
					turnLeftL(highSpeed);
				} else if (error < 0) {
					turnRightL(lowSpeed, highSpeed);
				}
			}

			if (stopFollowing()) {
				// Switch state back to INIT
				state = State.PASSED;

				// Log current state
				System.out.println("Navigation state switched to PASSED");

				// Stop motors
				LEFT_MOTOR.stop(true);
				RIGHT_MOTOR.stop(false);
			}
		} else if (state == State.PASSED) {

			// Rotate sensor back to looking forward
			rotateMotor.rotate(-rotateMotor.getTachoCount(), false);

			// Reset tachocount to 0
			rotateMotor.resetTachoCount();

			// Reset turned right or turned left to false
			turnedRight = false;
			turnedLeft = false;

			// Navigate to waypoint
			navigatorObstacle.setTraveling(false);
			
			// Stop motors 
			LEFT_MOTOR.stop(true);
			RIGHT_MOTOR.stop(false);

			// Change state back to INIT
			state = State.INIT;

			// Log current state
			System.out.println("Navigation state switched to INIT");
		}
	}

	/**
	 * Method that moves to robot forward
	 */
	private static void forward() {
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
	}

	/**
	 * Method used to turn right when sensor is on the right of the wall.
	 * 
	 * @param speed
	 */
	private static void turnRightR(int speed) {
		// if error is bigger than 0, this means that the vehicle is too close from the
		// wall which means we need to turn right
		LEFT_MOTOR.setSpeed(speed);
		RIGHT_MOTOR.setSpeed(speed);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.backward();
	}

	/**
	 * Method used to turn left when the sensor is on the left of the wall.
	 * 
	 * @param lowSpeed
	 * @param highSpeed
	 */
	private static void turnLeftR(int lowSpeed, int highSpeed) {
		// if error is smaller than 0, this means that the vehicle is too far from the
		// wall, which
		// means we need to turn left
		LEFT_MOTOR.setSpeed(lowSpeed);
		RIGHT_MOTOR.setSpeed(highSpeed);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
	}

	/**
	 * Method used to turn right when the sensor is on the left of the wall.
	 * 
	 * @param speed
	 */
	private static void turnRightL(int lowSpeed, int highSpeed) {
		// if error is bigger than 0, this means that the vehicle is too close from the
		// wall which means we need to turn right
		LEFT_MOTOR.setSpeed(highSpeed);
		RIGHT_MOTOR.setSpeed(lowSpeed);
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
	}

	/**
	 * Method used to turn left when the sensor is on the left of the wall.
	 * 
	 * @param lowSpeed
	 * @param highSpeed
	 */
	private static void turnLeftL(int speed) {
		LEFT_MOTOR.setSpeed(speed);
		RIGHT_MOTOR.setSpeed(speed);
		LEFT_MOTOR.backward();
		RIGHT_MOTOR.forward();
	}

	/**
	 * Returns the distance between the US sensor and an obstacle in cm.
	 * 
	 * @return the distance between the US sensor and an obstacle in cm
	 */
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	/**
	 * Calculates and returns the proportional gain according to proportion control
	 * scheme A correction is applied to the controlled variable (gain) which is
	 * corresponds to the difference between the offset and the measured measure
	 * distance. The gain constant is proportional to the error (r - y). This gain
	 * will then be used to correct the rotation of the EV3 Robot.
	 * 
	 * @return proportional gain
	 */
	private static int calcGain(int error) {

		// Filter out error
		if (error < -200) {
			error = -200;
		}

		// Proportional control: Scaling the correction, i.e. DELTASPD, according to
		// error
		error = Math.abs(error);

		// Compute correction using ERROR_ROTATION SCALE
		int DELTASPD = error * PROPORTION_GAIN_SCALE;

		// Set a maximum correction, filter out corrections that are too big to an upper
		// bound
		if (DELTASPD > 85) { // 70
			DELTASPD = 85;
		}
		return DELTASPD;
	}

	/**
	 * Method that makes a sharp turn right and turns the sensor left to face the
	 * wall.
	 */
	private static void sharpTurnRight() {

		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		// Turn right
		LEFT_MOTOR.rotate(NavigationWithObstacles.convertAngle(RIGHT_ANGLE), true);
		RIGHT_MOTOR.rotate(-NavigationWithObstacles.convertAngle(RIGHT_ANGLE), false);

		// Turn sensor 90 degrees to the left to face the wall
		rotateMotor.rotate(NavigationWithObstacles.convertAngle(SENSOR_ROTATION));

		// Set variable to track we turned right
		turnedRight = true;
	}

	/**
	 * Method that makes a sharp turn left and turns the sensor right to face the
	 * wall.
	 */
	private static void sharpTurnLeft() {

		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);

		// Turn left
		LEFT_MOTOR.rotate(-NavigationWithObstacles.convertAngle(RIGHT_ANGLE), true);
		RIGHT_MOTOR.rotate(NavigationWithObstacles.convertAngle(RIGHT_ANGLE), false);

		// Turn sensor 90 degrees to the right to face the wall
		rotateMotor.rotate(-NavigationWithObstacles.convertAngle(SENSOR_ROTATION));

		// Set variable to track we turned left
		turnedLeft = true;
	}

	/**
	 * Compute difference between the orientation to the next waypoint and the
	 * current Odometer's orientation.
	 * 
	 * @return true if the absolute value of this difference is smaller than the
	 *         stopping threshold and false otherwise
	 */
	public static boolean stopFollowing() {
		// Compute displacement
		double dx = navigatorObstacle.getDestX() - odometer.getXYT()[0];
		double dy = navigatorObstacle.getDestY() - odometer.getXYT()[1];

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = scaleTo360Degrees(Math.toDegrees(Math.atan2(dx, dy))) - odometer.getXYT()[2];

		if (Math.abs(theta) <= STOP_THRESHOLD) {
			return true;
		}
		return false;
	}

	/**
	 * Method that returns the angle to the next waypoint in the range of [0,360].
	 * 
	 * @param degree in the range of [-pi, pi]
	 * @return angle scaled to [0, 360]
	 */
	public static double scaleTo360Degrees(double degree) {
		if (degree < 0) {
			degree += 360;
		}
		return degree;
	}

	public static double distanceToWaypoint() {
		double dx = navigatorObstacle.getDestX() - odometer.getXYT()[0];
		double dy = navigatorObstacle.getDestY() - odometer.getXYT()[1];
		return Math.hypot(dx, dy);
	}
}
