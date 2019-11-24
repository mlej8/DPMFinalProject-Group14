package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;

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
   * Navigation class implements the singleton pattern
   */
  private Navigation() {

  }

  /**
   * Get instance of the Navigation class. Only allows one thread at a time calling this method.
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
    turnToExactTheta(90);

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
   * This method causes the robot to travel to the absolute field location (x, y), specified in tile points. This method
   * should continuously call turnTo(double theta) and then set the motor speed to forward (straight). This will make
   * sure that your heading is updated until you reach your exact goal. This method will poll the odometer for
   * information.
   * 
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
   * This method causes the robot to travel to the absolute field location (x, y), specified in tile points. This method
   * should continuously call turnTo(double theta) and then set the motor speed to forward (straight). This will make
   * sure that your heading is updated until you reach your exact goal. This method will poll the odometer for
   * information.
   * 
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

    if (Main.wifi.isTunnelHorizontal()) {

      // First travel along X-axis, then travel along Y-axis
      if (dx >= 0) {
        turnToExactTheta(90);
        xChange = 1;
      } else {
        turnToExactTheta(270);
        xChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
        navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
        while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
//          Main.sleepFor(SLEEPINT);
          // TODO : OBSTACLE BOI
          
          boolean obstacle  = true;
          break;
        }
        
        
        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrX(lightCorrector.getCurrX() + xChange);
          odometer.setX(lightCorrector.getCurrX() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
          if (xChange > 0) {
            odometer.setTheta(90);
          } else {
            odometer.setTheta(270);
          }
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
        }
      }

      // Second travel along Y-axis
      if (dy >= 0) {
        turnToExactTheta(0);
        yChange = 1;
      } else {
        turnToExactTheta(180);
        yChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
        navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
        sleepNavigation();

        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrY(lightCorrector.getCurrY() + yChange);
          odometer.setY(lightCorrector.getCurrY() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
          if (yChange > 0) {
            odometer.setTheta(0);
          } else {
            odometer.setTheta(180);
          }
        }

      }
    } else {

      // Travel along Y-axis
      if (dy >= 0) {
        System.out.println("Turned to 0");
        turnToExactTheta(0);
        yChange = 1;
      } else {
        System.out.println("Turned to 180");
        turnToExactTheta(180);
        yChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
        navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
        sleepNavigation();

        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrY(lightCorrector.getCurrY() + yChange);
          odometer.setY(lightCorrector.getCurrY() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
          if (yChange > 0) {
            odometer.setTheta(0);
          } else {
            odometer.setTheta(180);
          }
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
        }
      }

      // First travel along X-axis, then travel along Y-axis
      if (dx >= 0) {
        turnToExactTheta(90);
        xChange = 1;
      } else {
        turnToExactTheta(270);
        xChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
        navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
        // wait for certain amount of time, set it to true
        sleepNavigation();

        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrX(lightCorrector.getCurrX() + xChange);
          odometer.setX(lightCorrector.getCurrX() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
          if (xChange > 0) {
            odometer.setTheta(90);
          } else {
            odometer.setTheta(270);
          }
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
        }
      }
    }
    stop();
    this.traveling = false;
  }

  /**
   * Method that handles the process of traversing the tunnel
   * 
   * @param x: tunnel exit x coordinates
   * @param y: tunnel exit y coordinates
   */
  public void traverseTunnel(double x, double y) {

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
        turnToExactTheta(90);
        xChange = 1;
      } else {
        turnToExactTheta(270);
        xChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      // Travel one tile
      navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
      sleepNavigation();

      // Correct before entering tunnel.
      if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
        lightCorrector.setCurrX(lightCorrector.getCurrX() + xChange);
        odometer.setX(lightCorrector.getCurrX() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
        if (xChange > 0) {
          odometer.setTheta(90);
        } else {
          odometer.setTheta(270);
        }
        lightCorrector.setBothMotorsToFalse();
      }

      // Travel distance between light sensor and motor.
      travelLightSensorDistance();

      // Stop polling data.
      sensorPoller.setMode(Mode.IDLE);

      // Travel two tiles.
      navigateForward(2 * TILE_SIZE + 0.5*TILE_SIZE, TUNNEL_SPEED);
      sleepNavigation();
      stop();

      // Sleep thread.
      Main.sleepFor(TUNNEL_SLEEP);
      
      // Turn sensor polling on.
      sensorPoller.setMode(Mode.LIGHT);

      // Travel to tunnel exit.
      while (Math.abs(x - odometer.getXYT()[0]) > ERROR_MARGIN) {
        navigateForward(x - odometer.getXYT()[0], MOTOR_SPEED);
        sleepNavigation();
        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrX(lightCorrector.getCurrX() + 3 * xChange);
          odometer.setX(lightCorrector.getCurrX() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);          
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
          if (xChange > 0) {
            odometer.setTheta(90);
          } else {
            odometer.setTheta(270);
          }
        }
      }

      // Travel to latest recorded y.
      double lastY = lightCorrector.getCurrY() * TILE_SIZE - odometer.getXYT()[1];

      // Turn towards latest y.
      if (lastY >= 0) {
        turnToExactTheta(0);
        navigateForward(lightCorrector.getCurrY() * TILE_SIZE - odometer.getXYT()[1], MOTOR_SPEED);
      } else {
        turnToExactTheta(180);
        navigateForward(odometer.getXYT()[1] - lightCorrector.getCurrY() * TILE_SIZE, MOTOR_SPEED);
      }
      
      

      sleepNavigation();

      if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
        odometer.setY(lightCorrector.getCurrY() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
        travelLightSensorDistance();
        lightCorrector.setBothMotorsToFalse();
        if (lastY >= 0) {
          odometer.setTheta(0);
        } else {
          odometer.setTheta(180);
        }
      } 
    } else {
      // First travel along X-axis, then travel along Y-axis
      if (dy >= 0) {
        turnToExactTheta(0);
        yChange = 1;
      } else {
        turnToExactTheta(180);
        yChange = -1;
      }

      // Travel light sensor distance.
      travelLightSensorDistance();

      // Travel one tile
      navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
      sleepNavigation();

      // Correct before entering tunnel.
      if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
        lightCorrector.setCurrY(lightCorrector.getCurrY() + yChange);
        odometer.setY(lightCorrector.getCurrY() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
        if (yChange > 0) {
          odometer.setTheta(0);
        } else {
          odometer.setTheta(180);
        }
        lightCorrector.setBothMotorsToFalse();
      }

      // Travel distance between light sensor and motor.
      travelLightSensorDistance();

      // Stop polling data.
      sensorPoller.setMode(Mode.IDLE);

      // Travel two tiles.
      navigateForward(2 * TILE_SIZE + 0.5*TILE_SIZE, TUNNEL_SPEED);
      sleepNavigation();
      stop();

      // Sleep thread.
      Main.sleepFor(TUNNEL_SLEEP);
      
      // Turn sensor polling on.
      sensorPoller.setMode(Mode.LIGHT);

      // Travel to tunnel exit.
      while (Math.abs(y - odometer.getXYT()[1]) > ERROR_MARGIN) {
        navigateForward(y - odometer.getXYT()[1], MOTOR_SPEED);
        sleepNavigation();
        if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
          lightCorrector.setCurrY(lightCorrector.getCurrY() + 3 * yChange);
          odometer.setY(lightCorrector.getCurrY() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
          travelLightSensorDistance();
          lightCorrector.setBothMotorsToFalse();
          if (yChange > 0) {
            odometer.setTheta(0);
          } else {
            odometer.setTheta(180);
          }
        }
      }

      // Travel to latest recorded x.
      double lastX = lightCorrector.getCurrX() * TILE_SIZE - odometer.getXYT()[0];
      
      // Turn towards latest y.
      if (lastX >= 0) {
        turnToExactTheta(90);
        navigateForward(lightCorrector.getCurrX() * TILE_SIZE - odometer.getXYT()[0], MOTOR_SPEED);
      } else {
        System.out.println("Turned 270");
        turnToExactTheta(270);
        navigateForward(odometer.getXYT()[0] - lightCorrector.getCurrX() * TILE_SIZE, MOTOR_SPEED);
      }

      sleepNavigation();

      if (lightCorrector.isLeftMotorTouched() && lightCorrector.isRightMotorTouched()) {
        odometer.setX(lightCorrector.getCurrX() * TILE_SIZE - LIGHT_SENSOR_DISTANCE);
        travelLightSensorDistance();
        lightCorrector.setBothMotorsToFalse();
        if (lastX >= 0) {
          odometer.setTheta(90);
        } else {
          odometer.setTheta(270);
        }
      }
    }

    stop();
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
   * This method returns true if another thread has called travelTo() or turnTo() and the method has yet to return;
   * false otherwise.
   */
  public boolean isNavigating() {
    return this.traveling;
  }

  /**
   * This method causes the robot to navigate forward for a set amount of distance.
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
   * This method causes the robot to turn (on point) to the absolute heading theta w.r.t. its current orientation. This
   * method should turn using the MINIMAL angle to its target.
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
   * Method that rotates continuously (without waiting for the move to complete) according to the input theta.
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
