package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

public class WIFI {

	/**
	 * Variable target's x coordinate.
	 */
	private double binX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double binY;

	/**
	 * Variable destination's x coordinate.
	 */
	private double launchX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double launchY;
	
	/**
	 * This method uses the given target position (binX,binY) to find the
	 * ideal launching position.
	 */
	public void findLaunchPosition() {
		double currentX = odometer.getXYT()[0];
		double currentY = odometer.getXYT()[1];
		double[] curPosition = new double[] { currentX, currentY };
		double[] throwTo = new double[] { binX, binY };

		double theta = Math.atan2(currentX - binX, currentY - binY);

		double dx, dy;
		// calculate the intersection of the circle and the line
		if (theta < 0) { // when the robot is in 2nd/3rd quadrant
			dy = LAUNCH_RANGE * Math.cos(-theta);
			dx = -LAUNCH_RANGE * Math.sin(-theta);
			this.launchY = binY + dy;
			this.launchX = binX + dx;
		} else { // in 1st/4th quadrant
			dy = LAUNCH_RANGE * Math.cos(theta);
			dx = LAUNCH_RANGE * Math.sin(theta);
			this.launchY = binY + dy;
			this.launchX = binX + dx; 
		}

		if (launchX <= 15 || launchY <= 15) {
			double[] target = findCircle(curPosition, throwTo);
			this.launchX = target[0];
			this.launchY = target[1];
		}
	}
	
	public void findDestination2() {

		// Compute angle towards the destination
		// Compute displacement
		double dx = binX - odometer.getXYT()[0];
		double dy = binY - odometer.getXYT()[1];

		// Compute the angle needed to turn; dx and dy are intentionally switched in
		// order to compute angle w.r.t. the y-axis and not w.r.t. the x-axis
		double theta = Math.toDegrees(Math.atan2(dx, dy)) - odometer.getXYT()[2];
		// Turn to destination
		navigator.turnTo(theta);
	}

	/**
	 * 
	 * @param curPos
	 * @param center
	 * @return
	 */
	private static double[] findCircle(double[] curPos, double[] center) {
		double[] target = new double[2];
		if (center[0] > center[1]) { // upper half
			double tX = curPos[0];
			double tY = Math.sqrt(Math.pow(LAUNCH_RANGE, 2) - Math.pow((curPos[0] - center[0]), 2)) + center[1];
			target = new double[] { tX, tY };
		} else { // lower half
			double tY = curPos[1];
			double tX = Math.sqrt(Math.pow(LAUNCH_RANGE, 2) - Math.pow((curPos[1] - center[1]), 2)) + center[0];
			target = new double[] { tX, tY };
		}
		return target;
	}
	
	public double getBinX() {
		return binX;
	}

	public void setBinX(double binX) {
		this.binX = binX;
	}

	public double getBinY() {
		return binY;
	}

	public void setBinY(double binY) {
		this.binY = binY;
	}
	
	/**
	 * @return launch point's x coordinate.
	 */
	public double getlaunchX() {
		return this.launchX;
	}

	/**
	 * @return launch point's y coordinate.
	 */
	public double getlaunchY() {
		return this.launchY;
	}

}
