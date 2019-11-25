package ca.mcgill.ecse211.team14.finalproject.tests;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.Converter;
import ca.mcgill.ecse211.team14.finalproject.Main;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;
import lejos.hardware.Button;

public class WheelRadiusTests {
  public static void main(String args[]) {
    
    int buttonChoice = -1;
    showText("Press Right      ","Button to start  ");
    while(buttonChoice != Button.ID_RIGHT) {
      buttonChoice = Button.waitForAnyPress();
    }
    
    // Set sensor poller mode to Light Sensor
    sensorPoller.setMode(Mode.LIGHT);
    
    // Start sensor poller thread 
    
    // Test wheel radius by rotating the robot two tiles forward.
    LEFT_MOTOR.setSpeed(ROTATE_SPEED);
    RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
    LEFT_MOTOR.rotate(Converter.convertDistance(2*TILE_SIZE),true);
    RIGHT_MOTOR.rotate(Converter.convertDistance(2*TILE_SIZE),true);
    
    while (LEFT_MOTOR.isMoving() || RIGHT_MOTOR.isMoving()) {
      Main.sleepFor(SLEEPINT);
    }
    

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
