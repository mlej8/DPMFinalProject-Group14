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
