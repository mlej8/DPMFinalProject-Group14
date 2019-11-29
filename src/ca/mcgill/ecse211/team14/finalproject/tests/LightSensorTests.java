package ca.mcgill.ecse211.team14.finalproject.tests;

import static ca.mcgill.ecse211.team14.finalproject.Resources.LCDScreen;
import static ca.mcgill.ecse211.team14.finalproject.Resources.LEFT_MOTOR;
import static ca.mcgill.ecse211.team14.finalproject.Resources.RIGHT_MOTOR;
import static ca.mcgill.ecse211.team14.finalproject.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.team14.finalproject.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.team14.finalproject.Resources.odometer;
import static ca.mcgill.ecse211.team14.finalproject.Resources.sensorPoller;
import ca.mcgill.ecse211.team14.finalproject.Converter;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;
import lejos.hardware.Button;

public class LightSensorTests {
	public static void main(String[] args) {
	      int buttonChoice = -1;
	      
	      showText("Press Right      ","Button to start  ");
	      
	      while(buttonChoice != Button.ID_RIGHT) {
	        buttonChoice = Button.waitForAnyPress();
	      }
	      new Thread(odometer).start();
	      new Thread(sensorPoller).start();
	      sensorPoller.setMode(Mode.LIGHT);
	      
	      LEFT_MOTOR.setSpeed(ROTATE_SPEED);
	      RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
	      LEFT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), true);
	      RIGHT_MOTOR.rotate(Converter.convertDistance(TILE_SIZE), false);
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
