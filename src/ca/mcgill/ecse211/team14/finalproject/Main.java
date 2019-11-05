package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

import ca.mcgill.ecse211.team14.finalproject.Display;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main class controlling the flow of the application.
 */
public class Main {

	public static void main(String args[]) {

		waitToStart();
		
		// TODO: Process WIFI info (starting position, bin, tunnel, island left bottom corner and right corner -> Create map)
		
		// TODO: Start odometer thread
		new Thread(odometer).start();
		
		// TODO: Start sensor poller 
		
		// TODO: Execute Falling Edge implementation of ultrasonic localization		
		// ultrasonicLocalizer.fallingEdge();
		
		// TODO: Switch to light mode in Sensor Poller 
		
		// TODO: Do light snesor correction to navigate to closest point 
		// i.e. navigate to first line and turn right 90 degrees and stop when detect both lines again, then turn right. 
		
		// TODO: Stop and beeps for 3 times 
		
		// TODO: Find tunnel entrance and tunnel exit (exX, exY) (i.e. 
		
		// TODO: Travel to tunnel
		
		// TODO: Pass the tunnel (Go straight until detected 4 lines? (Travel through a certain amount of distance) 
		
		// TODO: Set to BOTH mode (LIGHT and US mode for Sensor Poller)
		
		// TODO: Start obstacle avoidance thread...? or nah
		
		// TODO: Find launch point 
		
		// TODO: Travel to launch point 
		
		// TODO: Start obstacle avoidance 
		
		// TODO: If object has been detected, trigger object avoidance, during object avoidance, if robot intersects with bin circumferance circle, stop there. Otherwise, finish object avoidance until facing destination point. 
		
		// TODO: If no object has been detected, stop and beep for 3 times 
		
		// TODO: Launch all five balls
		
		// TODO: Travel to tunnel exX and exY 
		
		// TODO: Pass through the tunnel 
		
		// TODO: To starting point 
		
		// TODO: Stop and beep for 5 times		
		
		System.out.println(odometer.getXYT()[2]);
		navigator.travelTo(1*TILE_SIZE,1*TILE_SIZE);
		// Do nothing until exit button is pressed, then exit.
	
		// public static BallLauncher ballLauncher = new BallLauncher();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

	/**
	 * Wait till center button is clicked before localizing and navigating to the
	 * computed launch position.
	 * 
	 * @return Button choice
	 */
	private static int waitToStart() {
		int buttonChoice;
		
		Display.showText(" 	  Press The	   ", 
						 "  Center Button  ", 
						 "     to Start    ",
						 "    Target X: " + navigator.getTargetX(),
						 "    Target Y: " + navigator.getTargetY(),
						 "    Launch X: " + navigator.getlaunchX(),
						 "    Launch Y: " + navigator.getlaunchY());

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ENTER);

		LCDScreen.clear();

		return buttonChoice;
	}

	/**
	 * Thread sleeps for a time period specified by sleepFor
	 * 
	 * @param duration
	 */
	public static void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// There is nothing to be done here
		}
	}

}
