package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import java.math.BigDecimal;
import java.util.Map;
import ca.mcgill.ecse211.wificlient.WifiConnection;

public class WIFI {

  /**
   * Variable start point x coodinate.
   */
  private int startX;

  /**
   * Variable start point y coodinate.
   */
  private int startY;

  /**
   * Variable start point angle theta.
   */
  private double startT;

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
   * Variable tracking if tunnel is horizontal or vertical.
   */
  private boolean isTunnelHorizontal;

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

  public WIFI() {
    findLaunchPosition();
    findStartPoint();
    findTunnelEnEx();
  }

  /**
   * This method uses the given target position (binX,binY) to find the ideal launching position.
   */
  private void findLaunchPosition() {
//    this.launchX = bin.x * TILE_SIZE;
//    this.launchY = bin.y * TILE_SIZE;
  }

  /**
   * Changes the starting position (x,y).
   */
  private void findStartPoint() {

    if (redTeam == TEAM_NUMBER) {
      startCorner = redCorner;
    } else {
      startCorner = greenCorner;
    }
    // For beta demo
    switch (startCorner) {
      case 0:
//        startX = 0.5 * TILE_SIZE;
//        startY = 0.5 * TILE_SIZE;
        startX = 1;
        startY = 1;                         // (1,1)
        startT = 90;
        break;
      case 1:
//        startX = (mapWidth - 0.5) * TILE_SIZE;
//        startY = 0.5 * TILE_SIZE;
        startX = (mapWidth - 1);
        startY = 1;                         // (14, 1)
        startT = 0;
        break;
      case 2:
//        startX = (mapWidth - 0.5) * TILE_SIZE;
//        startY = (mapHeight - 0.5) * TILE_SIZE;
        startX = (mapWidth - 1);
        startY = (mapHeight - 1);       // (14, 8)
        startT = 180;
        break;
      case 3:
//        startX = 0.5 * TILE_SIZE;
//        startY = (mapHeight - 0.5) * TILE_SIZE;
        startX = 1;
        startY = (mapHeight - 1);       // (1, 8)
        startT = 270;
        break;
    }
  }

  /**
   * Method that calculates and returns the coordinates at which the robot needs to travel to in order to enter the
   * tunnel and exit the tunnel. In final competition, we might split it into getTunnelEntrance and getTunnelExit
   */
  private void findTunnelEnEx() {
    Region tunnelArea = null;
    Region startArea = null; // assume green team in beta-demo

    if (redTeam == TEAM_NUMBER) {
      startCorner = redCorner;
      tunnelArea = tnr;
      startArea = red;
    } else if (greenTeam == TEAM_NUMBER){
      startCorner = greenCorner;
      tunnelArea = tng;
      startArea = green;
    } else {
      // do nothing as no data received (?)
    }
    
    double width = 0;   // go vertically if w<h, else horizontally
    double height = 0;

    switch (startCorner) {
      case 0:
        width = island.ll.x - startX;
        height = island.ll.y - startY;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 0.5)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY + tunnelArea.ur.y - tunnelArea.ll.y)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX + tunnelArea.ur.x - tunnelArea.ll.x)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
        
      case 1:
        width = island.ur.x - startX;
        height = island.ll.y - startY;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 1)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY + tunnelArea.ur.y - tunnelArea.ll.y)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX - tunnelArea.ur.x + tunnelArea.ll.x)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
        
      case 2:
       
      case 3:
    }
   
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

  public int getStartX() {
    return startX;
  }

  public int getStartY() {
    return startY;
  }

  public double getStartT() {
    return startT;
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

  public boolean isTunnelHorizontal() {
    return isTunnelHorizontal;
  }
}
