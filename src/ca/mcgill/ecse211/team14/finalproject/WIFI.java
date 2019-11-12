package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

public class WIFI {

	/**
	 * Variable target's x coordinate.
	 */
	private double binX = bin.x;

	/**
	 * Variable destination's y coordinate.
	 */
	private double binY = bin.y;

	/**
	 * Variable start point x coodinate.
	 */
	private double startX;
 
	/**
	 * Variable start point y coodinate.
	 */
	private double startY;

	/**
	 * Variable destination's x coordinate.
	 */
	private double launchX;

	/**
	 * Variable destination's y coordinate.
	 */
	private double launchY;

	/**
	 * Array that stores the lower left and upper right corners of islands.
	 */
	private double[] islandCoordinates;

	/**
	 * Array that stores the tunnel coordinates.
	 */
	private double[] tunnelCoordinates;

	/**
	 * Tunnel entrance X coordinate.
	 */
	private double tunnelEnX;

	/**
	 * Tunnel entrance Y coordinate.
	 */
	private double tunnelEnY;

	/**
	 * This method uses the given target position (binX,binY) to find the ideal
	 * launching position.
	 */
	private void findLaunchPosition() {
		this.launchX = binX;
		this.launchY = binY;	
	}

	/**
	 * Changes the starting position (x,y).
	 */
	public void findStartPoint() {
		startCorner = 0;
		if (redTeam == TEAM_NUMBER) {
			startCorner = redCorner;
		} else {
			startCorner = greenCorner;
		}
		// For beta demo
		switch (startCorner) {
		case 0:
			startX = 0.5 * TILE_SIZE;
			startY = 0.5 * TILE_SIZE;
			break;
		case 1:
			startX = (mapWidth - 0.5) * TILE_SIZE;
			startY = 0.5 * TILE_SIZE;
			break;
		case 2:
			startX = (mapWidth - 0.5) * TILE_SIZE;
			startY = (mapHeight - 0.5) * TILE_SIZE;
			break;
		case 3:
			startX = 0.5 * TILE_SIZE;
			startY = (mapHeight - 0.5) * TILE_SIZE;
			break;
		}
	}

	/**
	 * Returns the double array [startX, startY]
	 */
	public double[] getStartPoint() {
		double[] startPoint = new double[] { startX, startY };
		return startPoint;
	}

	/**
	 * Method that calculates and returns the coordinates at which the robot needs
	 * to travel to in order to enter the tunnel.
	 */
	public void getTunnelEntrance() {
		Region tunnelArea = null;
		Region startArea = null;
//		if (redTeam == 14) {
//			tunnelArea = tnr;
//			startArea = red;
//		} else {
//			
//		}
		if(greenTeam == 14) {
		  	tunnelArea = tng;
		  	startArea = green;
		}       

		double x = 0;
		double y = 0;
		if (x <= y) {
			// go vertically
			if (startY < tunnelArea.ll.y) { // below
				x = tunnelArea.ll.x + 0.5;
				y = tunnelArea.ll.y;
			} else { // upper
				x = tunnelArea.ur.x;
				y = tunnelArea.ur.y + 1;
			}
		} else {
			// go horizontally
			if (startX < tunnelArea.ll.x) { // left
				x = tunnelArea.ll.x;
				y = tunnelArea.ll.y + 0.5;
			} else {
				x = tunnelArea.ur.x + 1; // right
				y = tunnelArea.ur.y;
			}
			// TODO: 1. turn to the same angle every time?
			// 2. Avoid touching the river/wall in edge cases
			tunnelEnX = x*TILE_SIZE;
			tunnelEnY = y*TILE_SIZE;
		}
	}

	/**
	 * Method that calculates and returns the coordinates at which the robot needs
	 * to travel to in order to exit the tunnel.
	 */
	public double[] getTunnelExit() {
		// TODO: merge into getEntrance
		System.out.println(" ");
		return islandCoordinates;
	}

	/**
	 * Method that calculates the intersection between the line formed by the
	 * robot's current position and the target bin, and the circumference of the
	 * target formed with the launch radius range.
	 * 
	 * @param curPos: the current position of the robot
	 * @param center: the target position of the ball
	 * 
	 * @returnThe coordinates that represent the ideal launch point.
	 */
	private double[] findCircle(double[] curPos, double[] center) {
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

	public double getBinY() {
		return binY;
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

	public double getStartX() {
		return startX;
	}

	public double getStartY() {
		return startY;
	}

	public double[] getIslandCoordinates() {
		return islandCoordinates;
	}

	public double[] getTunnelCoordinates() {
		return tunnelCoordinates;
	}

	public double getTunnelEnX() {
		return tunnelEnX;
	}

	public void setTunnelEnX(double tunnelEnX) {
		this.tunnelEnX = tunnelEnX;
	}

	public double getTunnelEnY() {
		return tunnelEnY;
	}

	public void setTunnelEnY(double tunnelEnY) {
		this.tunnelEnY = tunnelEnY;
	}

}
