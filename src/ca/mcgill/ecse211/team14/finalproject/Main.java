package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.SensorPoller.Mode;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main class controls the flow of the application.
 */
public class Main {

	/**
	 * Global instance of WIFI class
	 */
	public static WIFI wifi;

	// Threads used through out the flow of the application
	static Thread odometerThread = new Thread(odometer);
	static Thread sensorPollerThread = new Thread(sensorPoller);

	public static void main(String args[]) {

		wifi = new WIFI();

		// Start odometer and sensor poller thread
		odometerThread.start();
		sensorPollerThread.start();

		// Falling Edge
		ultrasonicLocalizer.fallingEdge();
		sensorPoller.setMode(Mode.LIGHT);

		// Travel to closest grid intersection
		navigator.travelToGridIntersection();

		// Set startPoint (x,y,t) to odometer (e.g. at corner 0, the angle is 90)
		odometer.setXYT(wifi.getStartX() * TILE_SIZE, wifi.getStartY() * TILE_SIZE, wifi.getStartT() * TILE_SIZE);
		navigator.setCurrX(wifi.getStartX());
		navigator.setCurrY(wifi.getStartY());

		// Beep three times after US Localization
		stopAndBeep(3);

		// Navigate to the Tunnel entrance
		navigator.travelTo(wifi.getTunnelEnX(), wifi.getTunnelEnY());

		// Traverse the Tunnel to the Island
		if (wifi.getTunnelHeight() != wifi.getTunnelWidth()) {
			navigator.traverseTunnel(wifi.getTunnelExX(), wifi.getTunnelExY(), 2);
		} else {
			navigator.traverseTunnel(wifi.getTunnelExX(), wifi.getTunnelExY(), 1);
		}

		// Set launch position
		wifi.findLaunchPosition();
		Main.sleepFor(SLEEPINT);

		// If there's a possible launch point residing in the island
		if (wifi.getlaunchX() != 0 && wifi.getlaunchY() != 0) {

			// Travel to grid intersection near launch point
			sensorPoller.setMode(Mode.LIGHT);
			navigator.travelTo(wifi.getLaunchIntersectionPointX(), wifi.getLaunchIntersectionPointY());
			Main.sleepFor(SLEEPINT);

			// Travel to exact launch point
			navigator.travelToExactLaunchPoint();
			Main.sleepFor(SLEEPINT);

			// Turn to launch angle
			navigator.turnToExactTheta(Main.wifi.getBinAngle(), false);

			// Launch the ball
			stopAndBeep(5);
			BallLauncher ballLauncher = new BallLauncher();
			ballLauncher.launch();
			Main.sleepFor(SLEEPINT);

			// Travel back to tunnel exit
			navigator.stop();
			Main.sleepFor(SLEEPINT);

			// Navigate to latest intersection
			navigator.travelBackToLatestGridIntersection();
		}

		// Travel back to tunnel
		navigator.travelTo(wifi.getTunnelExX(), wifi.getTunnelExY());

		// Pass Tunnel
		if (wifi.getTunnelHeight() != wifi.getTunnelWidth()) {
			navigator.traverseTunnel(wifi.getTunnelEnX(), wifi.getTunnelEnY(), 2);
		} else {
			navigator.traverseTunnel(wifi.getTunnelEnX(), wifi.getTunnelEnY(), 1);
		}

		// Navigate back to starting point
		navigator.travelTo(wifi.getStartX() * TILE_SIZE, wifi.getStartY() * TILE_SIZE);
		navigator.turnToExactTheta(0, true);

		// Sleep before final beep
		Main.sleepFor(TUNNEL_SLEEP);

		// Stop and beep for 5 times
		stopAndBeep(5);

		// Do nothing until exit button is pressed, then exit.
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);

	}

	/**
	 * Thread sleeps for a time period specified by sleepFor.
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
	 * @param numberOfTimes:
	 *            number of times to beep.
	 */
	private static void stopAndBeep(int numberOfTimes) {
		// Stop robot
		navigator.stop();

		// Beep for specified number of times
		for (int i = 0; i < numberOfTimes; i++) {
			Sound.beep();
		}

		Main.sleepFor(SLEEPINT);
	}
}
