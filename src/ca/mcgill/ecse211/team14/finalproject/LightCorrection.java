package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * Class that implements light correction for navigation using two light sensors.
 * 
 * @author Michael Li
 */
public class LightCorrection {

  /**
   * Maximum ratio for last intensity compared to current intensity when there is no black line.
   */
  protected static final double LEFT_INTENSITY_RATIO = 1.15;
  
  /**
   * Maximum ratio for last intensity compared to current intensity when there is no black line.
   */
  protected static final double RIGHT_INTENSITY_RATIO = 1.1;

  /**
   * Boolean to specify if right motor has touched black line.
   */
  private boolean rightMotorTouched = false;

  /**
   * Boolean to specify if left motor has touched black line.
   */
  private boolean leftMotorTouched = false;

  /**
   * Initial left light sensor intensity.
   */
  protected double leftLastIntensity = -1;

  /**
   * Initial right light sensor intensity.
   */
  protected double rightLastIntensity = -1;

  /**
   * Variable that tracks the current tile's x.
   */
  private int currX = 0;

  /**
   * Variable that tracks the current tile's y.
   */
  private int currY = 0;

  /**
   * Variable that records if corrections is needed.
   */
  private boolean correction = true;


  /**
   * Perform an action based on the light data input. Method that corrects the robot's position during navigation. If
   * the light sensors detect the black lines asynchronously, it will stop the motor that has detected the black line
   * first until the second motor detects the black line.
   * 
   * @param leftCurIntensity: Intensity measured by left light sensor
   * @param rightCurIntensity: Intensity measured by right light sensor
   */
  public void processLightData(double leftCurIntensity, double rightCurIntensity) {

    if (correction) {

      double leftDiff = leftLastIntensity / leftCurIntensity;
      double rightDiff = rightLastIntensity / rightCurIntensity;

      // See if left motor has touched line.
      if (leftDiff > LEFT_INTENSITY_RATIO) {
        leftMotorTouched = true;
      }
      leftLastIntensity = leftCurIntensity;

      // See if right motor has touched line.
      if (rightDiff > RIGHT_INTENSITY_RATIO) {
        rightMotorTouched = true;
      }
      rightLastIntensity = rightCurIntensity;


      if (leftMotorTouched && rightMotorTouched) {
        navigator.stop();
//        leftMotorTouched = false;
//        rightMotorTouched = false;
      } else if (leftMotorTouched) {
        // Stop left motor
        LEFT_MOTOR.stop(false);
//        leftMotorTouched = false;
      } else if (rightMotorTouched) {
        // Stop right motor
        RIGHT_MOTOR.stop(false);
//        rightMotorTouched = false;
      }
    }
  }

  public boolean isCorrection() {
    return correction;
  }

  public void setCorrection(boolean correction) {
    this.correction = correction;
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

  public boolean isRightMotorTouched() {
    return rightMotorTouched;
  }

  public void setRightMotorTouched(boolean rightMotorTouched) {
    this.rightMotorTouched = rightMotorTouched;
  }

  public boolean isLeftMotorTouched() {
    return leftMotorTouched;
  }

  public void setLeftMotorTouched(boolean leftMotorTouched) {
    this.leftMotorTouched = leftMotorTouched;
  }
  
  public void setBothMotorsToFalse() {
    setRightMotorTouched(false);
    setLeftMotorTouched(false);
  }
}

