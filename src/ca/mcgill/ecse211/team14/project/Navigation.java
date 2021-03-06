package ca.mcgill.ecse211.team14.project;

import static ca.mcgill.ecse211.team14.project.Resources.*;

import ca.mcgill.ecse211.team14.finalproject.Converter;
import ca.mcgill.ecse211.team14.finalproject.Main;
import ca.mcgill.ecse211.team14.finalproject.Navigation;
import ca.mcgill.ecse211.team14.project.SensorPoller.Mode;

/**
 * The class which contains all method necessary for navigation.
 * 
 * @author Michael Li
 */
public class Navigation {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false;

	/**
	 * Current destination's x coordinate.
	 */
	private double destX;

	/**
	 * Current destination's y coordinate.
	 */
	private double destY;

	/**
	 * Variable that tracks the current tile's x.
	 */
	private int currX = 0;

	/**
	 * Variable that tracks the current tile's y.
	 */
	private int currY = 0;

	/**
	 * Variable that tracks if the robot has detected an obstacle.
	 */
	private boolean detectedObstacle = false;

	/**
	 * Navigation class implements the singleton pattern.
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
	 * Method that travels to the closest grid intersection.
	 */
	public void travelToGridIntersection() {

		// Set motor speed
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);

		// Go forward until a black line is detected
		LEFT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		sleepNavigation();

		// Travel light sensor distance
		travelLightSensorDistance();

		// Turn 90 degrees
		turnToExactTheta(90, false);

		// Set both line touched boolean variables to false
		lightCorrector.setBothMotorsToFalse();

		// Go forward until another black line is detected
		LEFT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		sleepNavigation();

		// Travel light sensor distance
		travelLightSensorDistance();

		// Set both line touched boolean variables to false
		lightCorrector.setBothMotorsToFalse();
	}

	/**
	 * Method that travels the distance between the light sensor and the wheel.
	 */
	public void travelLightSensorDistance() {
		lightCorrector.setCorrection(false);
		LEFT_MOTOR.rotate(Converter.convertDistance(LIGHT_SENSOR_DISTANCE), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(LIGHT_SENSOR_DISTANCE), false);
		lightCorrector.setCorrection(true);
	}

	/**
	 * This method causes the robot to travel to the absolute field location (x, y),
	 * specified in tile points. This method should continuously call turnTo(double
	 * theta) and then set the motor speed to forward (straight). This will make
	 * sure that your heading is updated until you reach your exact goal. This
	 * method will poll the odometer for information.
	 * 
	 * @param x:
	 *            destination x coordinate.
	 * @param y:
	 *            destination y coordinate.
	 */
	public void travelShortestPath(double x, double y) {

		// Do not correct x and y while turning
		lightCorrector.setCorrection(false);

		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		// Calculate the distance to waypoint
		double distance = Math.hypot(dx, dy);

		// Compute the angle needed to turn; dx and dy are intentionally switched in)
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];

		// Turn to the correct angle
		turnTo(theta);

		// Turn on motor
		LEFT_MOTOR.setSpeed(MOTOR_SPEED);
		RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
		LEFT_MOTOR.rotate(Converter.convertDistance(distance), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), false);

		// Once the destination is reached, stop both motors
		stop();
		this.traveling = false;
		lightCorrector.setCorrection(true);
	}

	/**
	 * This method causes the robot to travel to the absolute field location (x, y),
	 * specified in tile points. This method should continuously call turnTo(double
	 * theta) and then set the motor speed to forward (straight). This will make
	 * sure that your heading is updated until you reach your exact goal. This
	 * method will poll the odometer for information.
	 * 
	 * @param x:
	 *            destination x coordinates
	 * @param y:
	 *            destination y coordinates
	 */
	public void travelTo(double x, double y) {

		int xChange, yChange = 0;

		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		navigation: if (Main.wifi.isTunnelHorizontal()) {

			if (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
				// First travel along X-axis, then travel along Y-axis
				if (dx >= 0) {
					turnToExactTheta(90, true);
					xChange = 1;
				} else {
					turnToExactTheta(270, true);
					xChange = -1;
				}

				while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
					// Travel light sensor distance.
					// travelLightSensorDistance();
					if (xChange > 0) {
						dash(dashDistance(((x - odometer.getXYT()[0]) * 5) / (double) 7));
						navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
					} else {
						dash(dashDistance(xChange * ((x - odometer.getXYT()[0]) * 5) / (double) 7));
						navigateForward(xChange * (x - odometer.getXYT()[0]), MOTOR_SPEED);
					}
					while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
						Main.sleepFor(SLEEPINT);
						if (detectedObstacle()) {
							stop();
							this.detectedObstacle = true;
							break navigation;
						}
					}
					if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
						travelLightSensorDistance();
						this.currX += xChange;
						odometer.setX(this.currX * TILE_SIZE);
						if (xChange > 0) {
							odometer.setTheta(90);
						} else {
							odometer.setTheta(270);
						}
						lightCorrector.setBothMotorsToFalse();
					}
				}
			}
			if (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
				// Second travel along Y-axis
				if (dy >= 0) {
					turnToExactTheta(0, true);
					yChange = 1;
				} else {
					turnToExactTheta(180, true);
					yChange = -1;
				}
				while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
					// Travel light sensor distance.
					// travelLightSensorDistance();
					if (yChange > 0) {
						dash(dashDistance(((y - odometer.getXYT()[1]) * 5) / (double) 7));
						navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
					} else {
						dash(dashDistance((yChange * (y - odometer.getXYT()[1]) * 5) / (double) 7));
						navigateForward(yChange * (y - odometer.getXYT()[1]), MOTOR_SPEED);
					}
					while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
						Main.sleepFor(SLEEPINT);
						if (detectedObstacle()) {
							stop();
							this.detectedObstacle = true; // Consider case where it detects wall
							break navigation;
						}
					}
					if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
						travelLightSensorDistance();
						this.currY += yChange;
						odometer.setY(this.currY * TILE_SIZE);
						if (yChange > 0) {
							odometer.setTheta(0);
						} else {
							odometer.setTheta(180);
						}
						lightCorrector.setBothMotorsToFalse();
					}
				}
			}
		} else { // If tunnel is vertical, navigate along Y-axis first
			if (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
				// Travel along Y-axis
				if (dy >= 0) {
					turnToExactTheta(0, true);
					yChange = 1;
				} else {
					turnToExactTheta(180, true);
					yChange = -1;
				}

				while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
					// Travel light sensor distance.
					// travelLightSensorDistance();
					if (yChange > 0) {
						dash(dashDistance(((y - odometer.getXYT()[1]) * 5) / (double) 7));
						navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
					} else {
						dash(dashDistance((yChange * (y - odometer.getXYT()[1]) * 5) / (double) 7));
						navigateForward(yChange * (y - odometer.getXYT()[1]), MOTOR_SPEED);
					}
					while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
						Main.sleepFor(SLEEPINT);

						if (detectedObstacle()) {
							stop();
							this.detectedObstacle = true; // Consider case where it detects wall
							break navigation;
						}
					}
					if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
						travelLightSensorDistance();
						this.currY += yChange;
						odometer.setY(this.currY * TILE_SIZE);
						if (yChange > 0) {
							odometer.setTheta(0);
						} else {
							odometer.setTheta(180);
						}
						lightCorrector.setBothMotorsToFalse();
					}
				}
			}

			if (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
				// First travel along X-axis, then travel along Y-axis
				if (dx >= 0) {
					turnToExactTheta(90, true);
					xChange = 1;
				} else {
					turnToExactTheta(270, true);
					xChange = -1;
				}

				while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
					// Travel light sensor distance.
					// travelLightSensorDistance();
					if (xChange > 0) {
						dash(dashDistance(((x - odometer.getXYT()[0]) * 5) / (double) 7));
						navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
					} else {
						dash(dashDistance(xChange * ((x - odometer.getXYT()[0]) * 5) / (double) 7));
						navigateForward(xChange * (x - odometer.getXYT()[0]), MOTOR_SPEED);
					}
					while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
						Main.sleepFor(SLEEPINT);
						if (detectedObstacle()) {
							stop();
							this.detectedObstacle = true; // Consider case where it detects wall
							break navigation;
						}
					}

					if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
						travelLightSensorDistance();
						this.currX += xChange;
						odometer.setX(this.currX * TILE_SIZE);
						if (xChange > 0) {
							odometer.setTheta(90);
						} else {
							odometer.setTheta(270);
						}
						lightCorrector.setBothMotorsToFalse();
					}
				}
			}
		}
		// Obstacle avoidance
		if (detectedObstacle) {
			Main.sleepFor(SLEEPINT);

			// Turn away from the obstacle
			avoidObstacle();
		}
		stop();
		this.traveling = false;
		Main.sleepFor(SLEEPINT);
	}

	/**
	 * Method that returns distance to burst.
	 * 
	 * @param d
	 * @return
	 */
	private double dashDistance(double d) {
		// Adding upper bound
		if (Math.abs(d) > TILE_SIZE) {
			return (TILE_SIZE * 5) / 7;
		}
		return d;
	}

	/**
	 * Method to allow navigation to be done faster by traveling a certain amount of
	 * distance faster.
	 * 
	 * @param distance:
	 *            distance to travel
	 */
	private void dash(double distance) {
		LEFT_MOTOR.setSpeed(DASH_SPEED);
		RIGHT_MOTOR.setSpeed(DASH_SPEED);
		LEFT_MOTOR.rotate(Converter.convertDistance(distance), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), true);
		sleepNavigation();
		Main.sleepFor(SLEEPINT);
	}

	/**
	 * Method that handles the traveling from the closest grid intersection point to
	 * the exact launch point. Then, it turn towards the bin.
	 */
	public void travelToExactLaunchPoint() {
		// Traveling
		this.traveling = true;
		Main.sleepFor(SLEEPINT);

		// Compute displacement
		double launchX = Main.wifi.getlaunchX();
		double launchY = Main.wifi.getlaunchY();
		double dx = launchX - odometer.getXYT()[0];
		double dy = launchY - odometer.getXYT()[1];

		if (Math.abs(launchX - odometer.getXYT()[0]) > ERROR_MARGIN) {
			// First travel along X-axis, then travel along Y-axis
			if (dx >= 0) {
				turnToExactTheta(90, true);
				sleepNavigation();
				navigateForward(launchX - odometer.getXYT()[0], MOTOR_SPEED);
			} else {
				turnToExactTheta(270, true);
				sleepNavigation();
				navigateForward(-1 * (launchX - odometer.getXYT()[0]), MOTOR_SPEED);
			}
			sleepNavigation();
		} if (Math.abs(launchY - odometer.getXYT()[1]) > ERROR_MARGIN) {
			// Second travel along Y-axis
			if (dy >= 0) {
				turnToExactTheta(0, true);
				odometer.setTheta(0);
				sleepNavigation();
				navigateForward(launchY - odometer.getXYT()[1], MOTOR_SPEED);
			} else {
				turnToExactTheta(180, true);
				odometer.setTheta(180);
				sleepNavigation();
				navigateForward(-1 * (launchY - odometer.getXYT()[1]), MOTOR_SPEED);
			}
			sleepNavigation();
		}
		stop();

		// Set traveling to false
		this.traveling = false;
	}

	/**
	 * The method that navigates the robot back to the latest intersection point.
	 */
	public void travelBackToLatestGridIntersection() {
		// Travel back to closest grid intersection by traveling y first, then x.
		// Traveling
		this.traveling = true;

		// Compute displacement
		double destX = this.currX * TILE_SIZE;
		double destY = this.currY * TILE_SIZE;
		double dx = destX - odometer.getXYT()[0];
		double dy = destY - odometer.getXYT()[1];

		if (Math.abs(destY - odometer.getXYT()[1]) > ERROR_MARGIN) {
			// Second travel along Y-axis
			if (dy >= 0) {
				turnToExactTheta(0, false);
				Main.sleepFor(SLEEPINT);
				travelOneTileSize(false);
				odometer.setTheta(0);
			} else {
				turnToExactTheta(180, false);
				Main.sleepFor(SLEEPINT);
				travelOneTileSize(false);
				odometer.setTheta(180);
			}
		}
		sleepNavigation();
		if (Math.abs(destX - odometer.getXYT()[0]) > ERROR_MARGIN) {
			// First travel along X-axis, then travel along Y-axis
			if (dx >= 0) {
				turnToExactTheta(90, false);
				Main.sleepFor(SLEEPINT);
				travelOneTileSize(false);
				odometer.setTheta(90);
			} else {
				turnToExactTheta(270, false);
				Main.sleepFor(SLEEPINT);
				travelOneTileSize(false);
				odometer.setTheta(270);
			}
		}
		sleepNavigation();
		stop();

		// Set traveling to false
		this.traveling = false;
		Main.sleepFor(SLEEPINT);
	}

	/**
	 * The method which determines how the robot avoids the obstacle.
	 */
	private void avoidObstacle() {

		// Store current positions
		double x = odometer.getXYT()[0];
		double y = odometer.getXYT()[1];
		double theta = odometer.getXYT()[2];

		// Correct every time it turns
		navigateForward(-TILE_SIZE, MOTOR_SPEED);
		sleepNavigation();
		travelLightSensorDistance();

		// Set both motors to False
		lightCorrector.setBothMotorsToFalse();

		// Reference to the four island coordinates
		double islandLowerLeftX = island.ll.x * TILE_SIZE;
		double islandLowerLeftY = island.ll.y * TILE_SIZE;
		double islandUpperRightX = island.ur.x * TILE_SIZE;
		double islandUpperRightY = island.ur.y * TILE_SIZE;

		// Determine X and Y axis position
		double yAxis = (island.ur.x + island.ll.x) * TILE_SIZE / (double) 2;
		double xAxis = (island.ur.y + island.ll.y) * TILE_SIZE / (double) 2;
		if ((x >= yAxis && x <= islandUpperRightX) && (y >= xAxis && y <= islandUpperRightY)) {
			// If robot is on the first quadrant, i.e. top right
			if ((theta >= 345 && theta <= 360) || (theta >= 0 && theta <= 15)) { // allowing threshold of 15 degrees
				// Current orientation is 0
				turnToExactTheta(270, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX -= 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(270);
			} else if ((theta >= 75 && theta <= 105)) {
				// Current orientation is 90
				turnToExactTheta(180, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY -= 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(180);
			} else if ((theta >= 165 && theta <= 195)) {
				// Current orientation is 180
				turnToExactTheta(270, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX -= 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(270);
			} else if ((theta >= 255 && theta <= 285)) {
				// Current orientation is 270
				turnToExactTheta(180, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY -= 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(180);
			}
		} else if ((x >= islandLowerLeftX && x <= yAxis) && (y >= xAxis && y <= islandUpperRightY)) {
			// If robot is on the second quadrant, i.e. top left
			if ((theta >= 345 && theta <= 360) || (theta >= 0 && theta <= 15)) { // allowing threshold of 15 degrees
				// Current orientation is 0
				turnToExactTheta(90, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX += 1;
				odometer.setTheta(90);
				odometer.setX(this.currX * TILE_SIZE);
			} else if ((theta >= 75 && theta <= 105)) {
				// Current orientation is 90
				turnToExactTheta(180, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY -= 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(180);
			} else if ((theta >= 165 && theta <= 195)) {
				// Current orientation is 180
				turnToExactTheta(90, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX += 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(90);
			} else if ((theta >= 255 && theta <= 285)) {
				// Current orientation is 270
				turnToExactTheta(180, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY -= 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(180);
			}
		} else if ((x >= islandLowerLeftX && x <= yAxis) && (y >= islandLowerLeftY && y <= xAxis)) {
			// If robot is on the third quadrant, i.e. bottom left
			if ((theta >= 345 && theta <= 360) || (theta >= 0 && theta <= 15)) { // allowing threshold of 15 degrees
				// Current orientation is 0
				turnToExactTheta(90, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX += 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(90);
			} else if ((theta >= 75 && theta <= 105)) {
				// Current orientation is 90
				turnToExactTheta(0, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY += 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(0);
			} else if ((theta >= 165 && theta <= 195)) {
				// Current orientation is 180
				turnToExactTheta(90, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX += 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(90);
			} else if ((theta >= 255 && theta <= 285)) {
				// Current orientation is 270
				turnToExactTheta(0, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY += 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(0);
			}
		} else if ((x >= yAxis && x <= islandUpperRightX) && (y >= islandLowerLeftY && y <= xAxis)) {
			// If robot is on the forth quadrant, i.e. bottom right
			if ((theta >= 345 && theta <= 360) || (theta >= 0 && theta <= 15)) { // allowing threshold of 15 degrees
				// Current orientation is 0
				turnToExactTheta(270, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX -= 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(270);
			} else if ((theta >= 75 && theta <= 105)) {
				// Current orientation is 90
				turnToExactTheta(0, true); // turn left
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY += 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(0);
			} else if ((theta >= 165 && theta <= 195)) {
				// Current orientation is 180
				turnToExactTheta(270, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currX -= 1;
				odometer.setX(this.currX * TILE_SIZE);
				odometer.setTheta(270);
			} else if ((theta >= 255 && theta <= 285)) {
				// Current orientation is 270
				turnToExactTheta(0, true); // turn right
				// Navigate forward until robot detects a Grid Line
				travelOneTileSize(true);
				// Correct odometer
				this.currY += 1;
				odometer.setY(this.currY * TILE_SIZE);
				odometer.setTheta(0);
			}
		}

		// Set correction to false
		this.detectedObstacle = false;

		// Find new launching position
		Main.wifi.findLaunchPosition();

		// Sleep
		Main.sleepFor(2 * SLEEPINT);

		// Set corrector back to false after detecting the lines.
		lightCorrector.setBothMotorsToFalse();

		// Travel to new launch point.
		travelTo(Main.wifi.getlaunchX(), Main.wifi.getlaunchY());
	}

	/**
	 * This method takes lightDistance boolean as an input, if it is true, travel
	 * lightSensor distance then navigate forward one tile. If it is false, directly
	 * navigate forward one tile.
	 * 
	 * @param lightDistance
	 */
	private void travelOneTileSize(boolean lightDistance) {

		if (lightDistance) {
			travelLightSensorDistance();
		}

		// Navigate Forward One Tile
		navigateForward(TILE_SIZE, MOTOR_SPEED);

		sleepNavigation();

		// Travel light sensor distance.
		travelLightSensorDistance();
	}

	/**
	 * Method that returns a boolean depending on whether the robot has detected an
	 * obstacle or not.
	 * 
	 * @return boolean indicating if an obstacle has been detected.
	 */
	private boolean detectedObstacle() {
		if (sensorPoller.getMode() == Mode.BOTH && ultrasonicLocalizer.readUSDistance() < THRESHOLD) { // sensor poller
																										// will
																										// only be in
																										// mode
																										// both when it
																										// is on
																										// the launching
																										// island
			return true;
		}
		return false;
	}

	/**
	 * Method that handles the process of traversing the tunnel.
	 * 
	 * @param x:
	 *            Tunnel exit x coordinates
	 * @param y:
	 *            Tunnel exit y coordinates
	 * @param n:
	 *            Tunnel length
	 */
	public void traverseTunnel(double x, double y, int n) {

		// Variables that increments
		int xChange, yChange = 0;

		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		if (Main.wifi.isTunnelHorizontal()) {
			// First travel along X-axis, then travel along Y-axis
			if (dx >= 0) {
				turnToExactTheta(90, true);
				xChange = 1;
			} else {
				turnToExactTheta(270, true);
				xChange = -1;
			}

			// Travel one tile
			dash(dashDistance((TILE_SIZE * 5) / (double) 7));
			travelOneTileSize(false);

			// Correct before entering tunnel.
			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				this.currX += xChange;
				odometer.setX(this.currX * TILE_SIZE);
				if (xChange > 0) {
					odometer.setTheta(90);
				} else {
					odometer.setTheta(270);
				}
				lightCorrector.setBothMotorsToFalse();
			}

			sensorPoller.setMode(Mode.IDLE);

			// Travel n tiles.
			navigateForward(n * TILE_SIZE + 0.5 * TILE_SIZE, TUNNEL_SPEED);
			sleepNavigation();
			stop();

			// Sleep thread.
			Main.sleepFor(TUNNEL_SLEEP);

			// Turn sensor polling on.
			sensorPoller.setMode(Mode.LIGHT);

			// Travel one tile
			travelOneTileSize(false);

			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				this.currX += (n + 1) * xChange;
				odometer.setX(this.currX * TILE_SIZE);
				if (xChange > 0) {
					odometer.setTheta(90);
				} else {
					odometer.setTheta(270);
				}
				lightCorrector.setBothMotorsToFalse();
			}

			// Travel to latest recorded y.
			double lastY = this.currY * TILE_SIZE - odometer.getXYT()[1];

			// Turn towards latest y.
			if (lastY >= 0) {
				turnToExactTheta(0, false);
				travelOneTileSize(false);
			} else {
				turnToExactTheta(180, false);
				travelOneTileSize(false);
			}

			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				if (lastY >= 0) {
					odometer.setTheta(0);
				} else {
					odometer.setTheta(180);
				}
				odometer.setY(this.currY * TILE_SIZE);
				lightCorrector.setBothMotorsToFalse();
			}
		} else {
			// First travel along X-axis, then travel along Y-axis
			if (dy >= 0) {
				turnToExactTheta(0, true);
				yChange = 1;
			} else {
				turnToExactTheta(180, true);
				yChange = -1;
			}

			// Travel one tile
			dash(dashDistance((TILE_SIZE * 5) / (double) 7));
			travelOneTileSize(false);

			// Correct before entering tunnel.
			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				this.currY += yChange;
				odometer.setY(this.currY * TILE_SIZE);
				if (yChange > 0) {
					odometer.setTheta(0);
				} else {
					odometer.setTheta(180);
				}
				lightCorrector.setBothMotorsToFalse();
			}

			// Stop polling data.
			sensorPoller.setMode(Mode.IDLE);

			// Travel n tiles.
			navigateForward(n * TILE_SIZE + 0.5 * TILE_SIZE, TUNNEL_SPEED);
			sleepNavigation();
			stop();

			// Sleep thread.
			Main.sleepFor(TUNNEL_SLEEP);

			// Turn sensor polling on.
			sensorPoller.setMode(Mode.LIGHT);

			travelOneTileSize(false);

			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				this.currY += (n + 1) * yChange;
				odometer.setY(this.currY * TILE_SIZE);
				if (yChange > 0) {
					odometer.setTheta(0);
				} else {
					odometer.setTheta(180);
				}
				lightCorrector.setBothMotorsToFalse();
			}

			// Travel to latest recorded x.
			double lastX = this.currX * TILE_SIZE - odometer.getXYT()[0];

			// Turn towards latest y.
			if (lastX >= 0) {
				turnToExactTheta(90, false);
				travelOneTileSize(false);
			} else {
				turnToExactTheta(270, false);
				travelOneTileSize(false);
			}

			if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
				if (lastX >= 0) {
					odometer.setTheta(90);
				} else {
					odometer.setTheta(270);
				}

				odometer.setX(this.currX * TILE_SIZE);
				lightCorrector.setBothMotorsToFalse();
			}
		}
		stop();
		Main.sleepFor(SLEEPINT);
		this.traveling = false;
	}

	/**
	 * Method that sleeps a thread when the robot is traveling.
	 */
	public void sleepNavigation() {
		while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
			Main.sleepFor(SLEEPINT);
		}
	}

	/**
	 * This method returns true if another thread has called travelTo() or turnTo()
	 * and the method has yet to return; false otherwise.
	 */
	public boolean isNavigating() {
		return this.traveling;
	}

	/**
	 * This method causes the robot to navigate forward for a set amount of
	 * distance.
	 * 
	 * @Param distance: distance to travel.
	 */
	public void navigateForward(double distance, int speed) {
		LEFT_MOTOR.setSpeed(speed);
		RIGHT_MOTOR.setSpeed(speed);
		LEFT_MOTOR.rotate(Converter.convertDistance(distance), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), true);
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading theta
	 * w.r.t. its current orientation. This method should turn using the minimal
	 * angle to its target.
	 */
	public void turnTo(double theta) {

		// Do not correct x and y while turning
		lightCorrector.setCorrection(false);

		// Set traveling to true when the robot is turning
		this.traveling = true;

		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = -(360.0 - theta);
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Set motors' speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);

		// Set back light correction to true
		lightCorrector.setCorrection(true);
	}

	/**
	 * Method that causes the robot to turn to an exact angle with respect to the
	 * Y-axis.
	 *
	 * @param theta
	 */
	public void turnToExactTheta(double theta, boolean correct) {

		// Set both mtoors to false
		lightCorrector.setBothMotorsToFalse();

		// Do not correct x and y while turning
		lightCorrector.setCorrection(false);

		// Set traveling to true when the robot is turning
		this.traveling = true;

		// Angle needed to turn to the required angle
		theta -= odometer.getXYT()[2];

		// If theta is bigger than 180 or smaller than -180, set it to smallest minimal
		// turning angle
		if (theta > 180.0) {
			theta = -(360.0 - theta);
		} else if (theta < -180.0) {
			theta = 360.0 + theta;
		}

		// Set motors' speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), false);

		sleepNavigation();

		// Set back light correction to true
		lightCorrector.setCorrection(true);

		// Correct every time it turns
		if (correct) {
			navigateForward(-TILE_SIZE, MOTOR_SPEED);
			sleepNavigation();
			travelLightSensorDistance();
		}

		// Set both motors to False
		lightCorrector.setBothMotorsToFalse();
	}

	/**
	 * Method that rotates continuously (without waiting for the move to complete)
	 * according to the input theta.
	 * 
	 * @param theta:
	 *            angle at which the robot needs to turn
	 */
	public void rotate(double theta) {
		// Set rotate speed
		LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		LEFT_MOTOR.rotate(Converter.convertAngle(theta), true);
		RIGHT_MOTOR.rotate(-Converter.convertAngle(theta), true); // true
	}

	public boolean isTraveling() {
		return this.traveling;
	}

	public void setTraveling(boolean traveling) {
		this.traveling = traveling;
	}

	public double getDestX() {
		return destX;
	}

	public void setDestX(double destX) {
		this.destX = destX;
	}

	public double getDestY() {
		return destY;
	}

	public void setDestY(double destY) {
		this.destY = destY;
	}

	public int getCurrX() {
		return currX;
	}

	public void setCurrX(int currX) {
		this.currX = currX;
	}

	public int getCurrY() {
		return currY;
	}

	public void setCurrY(int currY) {
		this.currY = currY;
	}

	public boolean isDetectedObstacle() {
		return this.detectedObstacle;
	}
}
