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
	private double launchX = bin.x;

	/**
	 * Variable destination's y coordinate.
	 */
	private double launchY = bin.y;

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
	 * Tunnel exit X coordinate.
	 */
	private double tunnelExX;
	
	/**
	 * Tunnel exit Y coordinate.
	 */
	private double tunnelExY;
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
	public void findTunnelEntrance() {
		Region tunnelArea = null;
		Region startArea = null;

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
				y = tunnelArea.ll.y - 1.0;
			} else { // upper
				x = tunnelArea.ur.x - 0.5;
				y = tunnelArea.ur.y + 1.0;
			}
			
		} else {
			// go horizontally
			if (startX < tunnelArea.ll.x) { // left
				x = tunnelArea.ll.x - 1.0;
				y = tunnelArea.ll.y + 0.5;
			} else {
				x = tunnelArea.ur.x + 1.0; // right
				y = tunnelArea.ur.y - 0.5;
			}
			// TODO: 1. turn to the same angle every time?
			// 2. Avoid touching the river/wall in edge cases	
		}
		tunnelEnX = x*TILE_SIZE;
		tunnelEnY = y*TILE_SIZE;
		if (x <= y) {
			tunnelExX = x * TILE_SIZE;
			tunnelExY = (y + 4) * TILE_SIZE;
		} else {
			tunnelExX = (x + 4) * TILE_SIZE;
			tunnelExY = y;
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

	public double getTunnelExX() {
		return tunnelExX;
	}

	public void setTunnelExX(double tunnelExX) {
		this.tunnelExX = tunnelExX;
	}

	public double getTunnelExY() {
		return tunnelExY;
	}

	public void setTunnelExY(double tunnelExY) {
		this.tunnelExY = tunnelExY;
	}

}
