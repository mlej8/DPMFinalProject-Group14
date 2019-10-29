package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * This class contains method for launching a ball using catapult and reload ball.
 * 
 * @author Yilin Jiang
 *
 */
public class BallLauncher {

    public BallLauncher() {}

    /**
     * Launch the catapult.
     */
    public void catapultlaunch() {

      // Set motors' acceleration and speed
      leftLaunchMotor.setAcceleration(LAUNCH_MOTOR_ACCELERATOR);
      rightLaunchMotor.setAcceleration(LAUNCH_MOTOR_ACCELERATOR);
      leftLaunchMotor.setSpeed(LAUNCH_MOTOR_SPEED);
      rightLaunchMotor.setSpeed(LAUNCH_MOTOR_SPEED);
  
      // Make it rotote
      leftLaunchMotor.rotate(Converter.convertAngle(LAUNCH_ANGLE), true);
      rightLaunchMotor.rotate(Converter.convertAngle(LAUNCH_ANGLE), false);
 
      // Stop it
      leftLaunchMotor.stop(true);
      rightLaunchMotor.stop(true);
      reload();
    }

    /**
     * Allow for manual reload by rotating the arm backward.
     */
    public void reload() {
      // Set reload speed
      leftLaunchMotor.setAcceleration(ACCELERATION);
      rightLaunchMotor.setAcceleration(ACCELERATION);
      leftLaunchMotor.setSpeed(MOTOR_SPEED);
      rightLaunchMotor.setSpeed(MOTOR_SPEED);
  
      // Rotate catapult back to its original position
      leftLaunchMotor.rotate(-Converter.convertAngle(LAUNCH_ANGLE), true);
      rightLaunchMotor.rotate(-Converter.convertAngle(LAUNCH_ANGLE), false);
      Main.sleepFor(7000);
    }

}
