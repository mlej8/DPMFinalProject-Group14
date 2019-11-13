package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;

public class Navigation {

	private static Navigation navigator;

	/**
	 * {@code true} when robot is traveling.
	 */
	private boolean traveling = false; // false by default
	
	/**
	 * Current destination's x coordinate.
	 */
	private double destX;

	/**
	 * Current destination's y coordinate.
	 */
	private double destY;
	
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
	 * Method that travels to the closest grid intersection
	 */
	public void travelToGridIntersection() {
		
		// Ensure sensor poller pools data for light sensor 
		sensorPoller.setMode(Mode.LIGHT);
		
		// Go forward until a black line is detected 
		LEFT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE),true);
		
		while(LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
			Main.sleepFor(SLEEPINT);
		}
		
		// Correct odometer when first line is encountered
		lightCorrector.setCurrY(1);
		System.out.println("Current y" +  lightCorrector.getCurrY());
		odometer.setY(lightCorrector.getCurrY()*TILE_SIZE-LIGHT_SENSOR_DISTANCE);
		odometer.setTheta(0);
		
		System.out.println("Corrected Y and Theta " + odometer.getXYT()[0] + " "+ odometer.getXYT()[1] + " "+odometer.getXYT()[2]);
		
		travelLightSensorDistance();
		
		// Turn 90 degrees 
		turnToExactTheta(90);		
		
		// Go forward until another black line is detected 
		LEFT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
		RIGHT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE),true);
		while(LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
			Main.sleepFor(SLEEPINT);
		}
		// Correct X 
		lightCorrector.setCurrX(1);
		odometer.setX(lightCorrector.getCurrX()*TILE_SIZE - LIGHT_SENSOR_DISTANCE);
		odometer.setTheta(90);
		
		travelLightSensorDistance();
		
		System.out.println("Final coordinates x " + odometer.getXYT()[0] + " y "+ odometer.getXYT()[1] + " theta "+odometer.getXYT()[2]);
	}
	
	
	/**
	 * Method that travels the distance between the light sensor and the wheel. 
	 */
	public void travelLightSensorDistance() {
	  lightCorrector.setCorrection(false);        
      LEFT_MOTOR.rotate(Converter.convertDistance(LIGHT_SENSOR_DISTANCE), true);
      RIGHT_MOTOR.rotate(Converter.convertDistance(LIGHT_SENSOR_DISTANCE),false);
      lightCorrector.setCorrection(true);
	}
	
	/**
	 * This method causes the robot to travel to the absolute field location (x, y),
	 * specified in tile points. This method should continuously call turnTo(double
	 * theta) and then set the motor speed to forward (straight). This will make
	 * sure that your heading is updated until you reach your exact goal. This
	 * method will poll the odometer for information.
	 * @param x: destination x coordinate.
	 * @param y: destination y coordinate.
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
	 * @param x: destination x coordinates 
	 * @param y: destination y coordinates 
	 */
	public void travelTo(double x, double y) {
		
	    int xChange, yChange = 0;
	  
		// Traveling
		this.traveling = true;

		// Compute displacement
		double dx = x - odometer.getXYT()[0];
		double dy = y - odometer.getXYT()[1];

		// First travel along X-axis, then travel along Y-axis
		if (dx >= 0) {
			turnToExactTheta(90);	
			xChange = 1;
		} else {
			turnToExactTheta(270);
			xChange = -1;
		}
		
		while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) { 
		  lightCorrector.setCorrection(false);
		  navigateForward(x-odometer.getXYT()[0]);
		  // wait for certain amount of time, set it to true
		  while(LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
		    Main.sleepFor(SLEEPINT);
		    if(!lightCorrector.isCorrection()) {
              lightCorrector.setCorrection(true);
            }
		  }
		  lightCorrector.setCurrX(lightCorrector.getCurrX()+xChange);
		  odometer.setX(lightCorrector.getCurrX()*TILE_SIZE-LIGHT_SENSOR_DISTANCE);
		  travelLightSensorDistance();
		}
		
		// Second travel along Y-axis
		if (dy >= 0) {
			turnToExactTheta(0);
			yChange = 1;
		} else {
			turnToExactTheta(180);
			yChange = -1;
		}
		
		while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) { 
          lightCorrector.setCorrection(false);
	        navigateForward(y-odometer.getXYT()[1]);      
	        while(LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
	            Main.sleepFor(SLEEPINT);
	            if(!lightCorrector.isCorrection()) {
	            lightCorrector.setCorrection(true);
	            }
	        }
	        lightCorrector.setCurrY(lightCorrector.getCurrY()+yChange);
	        odometer.setY(lightCorrector.getCurrY()*TILE_SIZE-LIGHT_SENSOR_DISTANCE);
	        travelLightSensorDistance();
	    }
		
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
		RIGHT_MOTOR.rotate(Converter.convertDistance(distance), true);
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta w.r.t. its current orientation. This method should turn using the MINIMAL angle to its target.
	 */
	public void turnTo(double theta) {
		
		// Do not correct x and y while turning 
		lightCorrector.setCorrection(false);

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
		
		// Set back light correction to true
		lightCorrector.setCorrection(true);
	}
	
	/**
	 * Method that causes the robot to turn to an exact angle with respect to the Y axis.
	 *
	 * @param theta
	 */
	public void turnToExactTheta(double theta) {	

		// Do not correct x and y while turning 
		lightCorrector.setCorrection(false);

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

		// Set back light correction to true
		lightCorrector.setCorrection(true);
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
}
