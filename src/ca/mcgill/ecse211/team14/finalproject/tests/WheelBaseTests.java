package ca.mcgill.ecse211.team14.finalproject.tests;
import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import lejos.hardware.Button;

/**
 * Tester for testing TRACK constant.
 * @author Cecilia Jiang
 *
 */
public class WheelBaseTests {
    public static void main(String args[]) { 
      
      // Test wheel base by rotating the robot 360 degrees.
      LEFT_MOTOR.setSpeed(ROTATE_SPEED);
      RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
      navigator.turnToExactTheta(-90, false);
      
  	  // Do nothing until exit button is pressed, then exit.	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
    }
    
    
    /**
     * Shows the text on the LCD, line by line.
     * 
     * @param strings comma-separated list of strings, one per line
     */
    public static void showText(String... strings) {
      LCDScreen.clear();
      for (int i = 0; i < strings.length; i++) {
        LCDScreen.drawString(strings[i], 0, i);
      }
    }
}
