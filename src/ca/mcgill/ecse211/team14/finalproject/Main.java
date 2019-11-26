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
	 * Private class variable for ballLauncher 
	 */
	private static BallLauncher ballLauncher;

    /**
     * Global instance of WIFI class
     */
    public static WIFI wifi;
    

	// Threads used through out the flow of the application
	static Thread odometerThread = new Thread(odometer);
	static Thread sensorPollerThread = new Thread(sensorPoller);
	
	public static void main(String args[]) {  
	  
	    wifi = new WIFI();
	    System.out.println("startX = "+ wifi.getStartX() + ", startY = "+wifi.getStartY() + ", startT = "+wifi.getStartT());
	    System.out.println("tunnelEn = "+"("+wifi.getTunnelEnX()+", "+wifi.getTunnelEnY()+")"+
	    ", tunnelEx = "+"("+wifi.getTunnelExX()+", "+wifi.getTunnelExY()+")");
	    
		// Start odometer and sensor poller thread
		odometerThread.start(); 
		sensorPollerThread.start();
        
		// Falling Edge	
		ultrasonicLocalizer.fallingEdge();
		sensorPoller.setMode(Mode.LIGHT);

		// Travel to closest grid intersection 
		navigator.travelToGridIntersection();
		
		// Set startPoint (x,y,t) to odometer (e.g. at corner 0, the angle is 90)
		odometer.setXYT(wifi.getStartX()*TILE_SIZE, wifi.getStartY()*TILE_SIZE, wifi.getStartT());
		navigator.setCurrX(wifi.getStartX()); 
		navigator.setCurrY(wifi.getStartY());
        System.out.println("Start X " + navigator.getCurrX() + " Y " + navigator.getCurrY());
		System.out.println("After US: X " + navigator.getCurrX() + " Y " + navigator.getCurrY());
				   
		// Beep three times after US Localization
		stopAndBeep(3);
		
		// Navigate to the Tunnel entrance 
		System.out.println("Tunnel EnX " + wifi.getTunnelEnX() + " EnY " + wifi.getTunnelEnY());
		navigator.travelTo(wifi.getTunnelEnX(), wifi.getTunnelEnY()); 
		System.out.println("x: " + navigator.getCurrX() + " y: " + navigator.getCurrY());
		System.out.println("Before entering tunnel Odometer Reading:"+odometer.getXYT()[0]+","+odometer.getXYT()[1]+","+odometer.getXYT()[2]);
			
		// Traverse the Tunnel to the Island 
		if (wifi.getTunnelHeight() != wifi.getTunnelWidth()) {		
		navigator.traverseTunnel(wifi.getTunnelExX(), wifi.getTunnelExY(),2);
		} else {
		  navigator.traverseTunnel(wifi.getTunnelExX(), wifi.getTunnelExY(),1);
		}
		
        System.out.println("Exit Tunnel Odometer Reading:"+odometer.getXYT()[0]+","+odometer.getXYT()[1]+","+odometer.getXYT()[2]);
        
		// Set launch position
		wifi.findLaunchPosition();
		Main.sleepFor(SLEEPINT);
		
		// TODO: Navigate to bin x and bin y
	    System.out.println("Launch intersection X " + wifi.getLaunchIntersectionPointX()/TILE_SIZE+ " Y " + wifi.getLaunchIntersectionPointY()/TILE_SIZE);
	    System.out.println("facing angle = "+wifi.getBinAngle());
	    
	    sensorPoller.setMode(Mode.BOTH);
	    
	    // TODO: Find launch point
        // TODO: Travel to launch point
        navigator.travelTo(wifi.getLaunchIntersectionPointX(), wifi.getLaunchIntersectionPointY());
        navigator.turnTo(wifi.getBinAngle());
        navigator.stop();

        // TODO TUNE WHEELBASE
        
		// TODO: Step 6. Launch the ball a minimum distance of 4 tiles, stop and beep 
		BallLauncher ballLauncher = new BallLauncher();
		ballLauncher.launch();	
		stopAndBeep(5);
		Main.sleepFor(SLEEPINT);
		
		// Travel back to tunnel        
        navigator.travelTo(wifi.getTunnelExX(), wifi.getTunnelExY());
        
		// Pass Tunnel 
        if (wifi.getTunnelHeight() != wifi.getTunnelWidth()) {      
          navigator.traverseTunnel(wifi.getTunnelEnX(), wifi.getTunnelEnY(),2);
          } else {
            navigator.traverseTunnel(wifi.getTunnelEnX(), wifi.getTunnelEnY(),1);
          }	
		System.out.println("Back to Tunnel Entrance X " +  odometer.getXYT()[0] + " Y " + odometer.getXYT()[1] + " T " + odometer.getXYT()[2]);
       
		// Navigate back to starting point 
		navigator.travelTo(wifi.getStartX()*TILE_SIZE, wifi.getStartY()*TILE_SIZE);
		System.out.println("start = ("+wifi.getStartX()+", "+wifi.getStartY()+")");

		Main.sleepFor(TUNNEL_SLEEP);  
		
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

		System.out.println("Press the right button to start.");
		do {
			buttonChoice = Button.waitForAnyPress();
			RECEIVE_WIFI_PARAMS = true;
		} while (buttonChoice != Button.ID_RIGHT);

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
		
		Main.sleepFor(SLEEPINT);
	}
}
