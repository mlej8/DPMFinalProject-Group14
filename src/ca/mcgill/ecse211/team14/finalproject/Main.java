package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main class controls the flow of the application.
 */
public class Main {

	public static void main(String args[]) {
		
		// Initiate a WIFI object
		WIFI wifi = new WIFI();
		// TODO: Tell wifi class to collect data and calculate everything it needs (launch, tunnelEn, tunnelEx, etc.)
		System.out.println("Fetching map data...");
		// wifi.findStartPoint();
		wifi.getTunnelEntrance();
		
		waitForPress();

		// TODO: Cecilia and Lora Zhang - Process WIFI info (starting position, bin,
		// tunnel, island left bottom corner and right corner -> Create map)

		// TODO: Start odometer thread
		new Thread(odometer).start();

		// TODO: Cecilia and Lora Zhang: Start sensor poller thread (add condition to
		// operate both sensors at the same time, and add state for IDLE)

		// TODO: Choose Falling vs Rising depending on hardware. Execute ultrasonic
		// localization
		ultrasonicLocalizer.fallingEdge();

		// TODO: Switch to light mode in Sensor Poller
		sensorPoller.setMode(Mode.LIGHT);

		// TODO: Michael: Do light snesor correction to navigate to closest point
		// i.e. navigate to first line and turn right 90 degrees and stop when detect
		// both lines again, then turn right.

		// TODO: Stop and beeps for 3 times
		stopAndBeep(3);


		// TODO: Travel to tunnel
		navigator.travelTo(wifi.getTunnelEnX(), wifi.getTunnelEnY());

		// TODO: Pass the tunnel (Go straight until detected 4 lines? (Travel through a
		// certain amount of distance). Think about a way to do it... 
		navigator.travelTo(wifi.getTunnelExX(), wifi.getTunnelExY());

		// turn off light correction
		leftMotor.rotate(Converter.convertDistance(2.5*TILE_SIZE));
		rightMotor.rotate(Converter.convertDistance(2.5*TILE_SIZE));
		// turn on the light correction
		leftMotor.rotate(Converter.convertDistance(0.5*TILE_SIZE));
		rightMotor.rotate(Converter.convertDistance(0.5*TILE_SIZE));
		
		// TODO: Set to BOTH mode (LIGHT and US mode for Sensor Poller)		
		// TODO: Start obstacle avoidance thread...? or nah Should make into a thread or not? 
		
		// TODO: Find launch point
		// TODO: Travel to launch point
		navigator.travelTo(wifi.getlaunchX(), wifi.getlaunchY());
		

		// TODO: Start obstacle avoidance

		
		// TODO: If object has been detected, trigger object avoidance, during object
		// avoidance, if robot intersects with bin circumferance circle, stop there.
		// Otherwise, finish object avoidance until facing destination point.

		// TODO: If no object has been detected, stop and beep for 3 times
		stopAndBeep(3);
		
		// TODO: Launch all five balls
		BallLauncher ballLauncher = new BallLauncher();
		
		// TODO: Travel to tunnel exX and exY
		// navigator.travelTo(wifi.getTunnelExX(), wifi.getTunnelExY());
		
		// TODO: Pass through the tunnel
		// navigator.travelTo(wifi.getTunnelEnX(), wifi.getTunnelEnY());

		// TODO: To starting point
		// navigator.travelTo(wifi.getStartX(), wifi.getStartY());

		// TODO: Stop and beep for 5 times
		stopAndBeep(5);

		// Do nothing until exit button is pressed, then exit.	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);

	}

	/**
	 * Wait till center button is clicked before localizing and navigating to the
	 * computed launch position.
	 * 
	 * @return Button choice
	 */
	private static int waitForPress() {
		int buttonChoice;

		System.out.println("Press the center button to start.");
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

	/**
	 * Method that stops the robot and beeps a specified number of times. 
	 * 
	 * @param numberOfTimes: number of times to beep. 
	 */
	private static void stopAndBeep(int numberOfTimes) {
		// Stop robot 
		navigator.stop();
		 
		// Beep for specified number of times
		for (int i = 0; i < numberOfTimes; i++) {
			Sound.beep();
		}
	}
}
