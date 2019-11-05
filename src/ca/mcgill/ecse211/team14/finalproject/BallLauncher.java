package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * This class contains method for launching a ping-pong ball using the launching mechanism designed by the hardware team.
 * 
 * @author Yilin Jiang, Michael Li, Lora Zhang
 *
 */
public class BallLauncher {

    public BallLauncher() {}

    /**
     * Triggers the launching mechanism.
     */
    public void launch() {

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



}
