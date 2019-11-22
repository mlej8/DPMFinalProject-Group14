package ca.mcgill.ecse211.team14.finalproject.tests;

import static ca.mcgill.ecse211.team14.finalproject.Resources.LCDScreen;

import lejos.hardware.Button;

public class LightSensorTests {
	public static void main(String[] args) {
	      int buttonChoice = -1;
	      
	      showText("Press Right      ","Button to start  ");
	      
	      while(buttonChoice != Button.ID_RIGHT) {
	        buttonChoice = Button.waitForAnyPress();
	      }
	      
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
