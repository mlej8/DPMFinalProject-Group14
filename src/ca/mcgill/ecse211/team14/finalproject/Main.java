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



//	private static Thread displayThread = new Thread(new Display()); // Display information on LCD screen

	public static void main(String args[]) {

		int buttonChoice = chooseStationaryOrMobile();
		if (buttonChoice == Button.ID_LEFT) {
			for(int i = 0; i<5; i++) {
			ballLauncher.catapultlaunch();
			}
		} else if (buttonChoice == Button.ID_RIGHT) {
			navigator.findDestination();			
			
			waitToStart();
			
			// Running sensorPoller and odometer threads.
			new Thread(sensorPoller).start(); // Running a thread controlling which sensor to fetch from
			new Thread(odometer).start(); // Running a continuous thread for odometer

			// Execute Falling Edge implementation of ultrasonic localization
			ultrasonicLocalizer.fallingEdge();

			// Stop fetching data from ultrasonic sensor and switch to fetch data from light
			// sensor
			sensorPoller.setMode(Mode.LIGHT);

			// Find current robot's position
			navigator.findRobotPosition();
			System.out.println("X: " + odometer.getXYT()[0] + " Y: " + odometer.getXYT()[1] + " Theta: " + odometer.getXYT()[2]);
			odometer.setTheta(0);
			sleepFor(1000);
		
			// Navigate to origin (1,1) approximately
			navigator.travelTo(TILE_SIZE, TILE_SIZE);

			// Execute light sensor localization
			lightLocalizer.localize();
			
			// Navigator to true origin (1,1) after light localization
			navigator.travelToOrigin();
			sleepFor(1000);
			
			// Orient back to 0 degree w.r.t. the Y-axis after correction
			lightLocalizer.orientTo0();
			
			// Find launch position after odometer has been corrected
			navigator.findDestination();

			// Navigate to to target position
			navigator.travelToLaunchPoint();
			
			// Arrived at destination
			Sound.twoBeeps();
			
			// turn to face the target
            navigator.findDestination2();
            sleepFor(1000);
                
			// Launch the ball
			for(int i = 0; i<5; i++) {
				ballLauncher.catapultlaunch();
				sleepFor(5000);
				}
			}
			
			// Do nothing until exit button is pressed, then exit.
			while (Button.waitForAnyPress() != Button.ID_ESCAPE)
				System.exit(0);
	}
	

	/**
	 * Choose with stationary launch or mobile launch.
	 * 
	 * @return Button choice
	 */
	private static int chooseStationaryOrMobile() {
		int buttonChoice;
		Display.showText("< Left  | Right >", "        |        ", "Stationa| Mobile ", "   ry   | Launch ",
				" Launch |        ");

		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		LCDScreen.clear();

		return buttonChoice;
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

	private static int initiatePauseToReload() {
		int buttonChoice;
		
		Display.showText(" 	  Press The	   ", 
						 "  Center Button  ", 
						 "     to Resume   ",
						 "   once catapult ",
						 "    is reloaded  ");

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
