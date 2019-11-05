package ca.mcgill.ecse211.team14.finalproject;

/**
 * Class that implements light correction for navigation using two light
 * sensors.
 * 
 * @author Michael Li
 */
public class LightCorrection {

    // Motor-related variables
	private int leftMotorTachoCount;  // current left wheel's tachometer count
	private int rightMotorTachoCount; // current right wheel's tachometer count
	private int lastTachoCountL = 0;  // left wheel's last tachometer count
	private int lastTachoCountR = 0;  // right wheel's last tachometer count
	
	/**
	 * Method that corrects the robot's position during navigation. If the light
	 * sensors detect the black lines asynchronously, it will stop the motor that
	 * has detected the black line first until the second motor detects the black line.
	 * If both detect the black line within a time threshold, they will continue to travel normally.
	 */
	public void correctNavigation() {
		System.out.println("pass");
	}
}
