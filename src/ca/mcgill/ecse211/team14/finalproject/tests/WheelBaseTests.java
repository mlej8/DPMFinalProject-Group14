package ca.mcgill.ecse211.team14.finalproject.tests;
import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.Converter;
import lejos.hardware.Button;

public class WheelBaseTests {
    public static void main(String args[]) { 
//      int buttonChoice = -1;
//      
//      showText("Press Right      ","Button to start  ");
//      
//      while(buttonChoice != Button.ID_RIGHT) {
//        buttonChoice = Button.waitForAnyPress();
//      }
      
      // Test wheel base by rotating the robot 360 degrees.
      LEFT_MOTOR.setSpeed(ROTATE_SPEED);
      RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
      navigator.turnToExactTheta(90, false);
      
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
