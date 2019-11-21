package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import java.math.BigDecimal;
import java.util.Map;
import ca.mcgill.ecse211.wificlient.WifiConnection;

public class WIFI {

  /**
   * Variable start point x coodinate.
   */
  private double startX;

  /**
   * Variable start point y coodinate.
   */
  private double startY;

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
    
    double currentX = odometer.getXYT()[0];
    double currentY = odometer.getXYT()[1];
    double[] curPosition = new double[] { currentX, currentY };
    double[] throwTo = new double[] { bin.x, bin.y };

    double theta = Math.atan2(currentX - bin.x, currentY - bin.y);

    double dx, dy;
    // calculate the intersection of the circle and the line
    if (theta < 0) { // when the robot is in 2nd/3rd quadrant
        dy = LAUNCH_RANGE * Math.cos(-theta);
        dx = -LAUNCH_RANGE * Math.sin(-theta);
        this.launchY = bin.y + dy;
        this.launchX = bin.x + dx;
    } else { // in 1st/4th quadrant
        dy = LAUNCH_RANGE * Math.cos(theta);
        dx = LAUNCH_RANGE * Math.sin(theta);
        this.launchY = bin.y + dy;
        this.launchX = bin.x + dx; 
    }

    double left = island.ll.x;
    double right = island.ur.x;
    double top = island.ur.y;
    double bottom = island.ll.y;
    
    if (launchX <= left || launchX >= right || launchY <= bottom || launchY >= top) {
        double[] target = findCircle(curPosition, throwTo);
        this.launchX = target[0];
        this.launchY = target[1];
    } //TODO: keep this?
    
  }
  
  
  /**
   * 
   * @param curPos
   * @param center
   * @return a point on the circle centered at center.
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
        startX = TILE_SIZE;
        startY = TILE_SIZE;                         // (1,1)
        startT = 90;
        break;
      case 1:
        startX = (mapWidth - 1) * TILE_SIZE;
        startY = TILE_SIZE;                         // (14, 1)
        startT = 0;
        break;
      case 2:
        startX = (mapWidth - 1) * TILE_SIZE;
        startY = (mapHeight - 1) * TILE_SIZE;       // (14, 8)
        startT = 180;
        break;
      case 3:
        startX = TILE_SIZE;
        startY = (mapHeight - 1) * TILE_SIZE;       // (1, 8)
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
        width = island.ll.x - startArea.ll.x;
        height = island.ll.y - startArea.ll.y;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 0.5)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY + tunnelArea.ur.y - tunnelArea.ll.y + 1)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX + tunnelArea.ur.x - tunnelArea.ll.x + 1)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
        
      case 1:
        width = startArea.ur.x - island.ur.x;
        height = island.ll.y - startArea.ll.y;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 0.5)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY + tunnelArea.ur.y - tunnelArea.ll.y + 1)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX - tunnelArea.ur.x + tunnelArea.ll.x - 1)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
        
      case 2:
        width = island.ur.x - startArea.ur.x;
        height = island.ur.y - startArea.ur.y;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 0.5)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY - tunnelArea.ur.y + tunnelArea.ll.y - 1)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX - tunnelArea.ur.x + tunnelArea.ll.x - 1)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
       
      case 3:
        width = island.ll.x - startArea.ll.x;
        height = startArea.ur.y - island.ur.y;
        if(width < height) {
          // Go vertically
          tunnelEnX = (tunnelArea.ll.x + 0.5)*TILE_SIZE;
          tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          tunnelExX = (tunnelEnX) * TILE_SIZE;
          tunnelExY = (tunnelEnY - tunnelArea.ur.y + tunnelArea.ll.y - 1)*TILE_SIZE;
        } else {
          // Go horizontally
          tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
          tunnelEnY = (tunnelArea.ll.y + 0.5)*TILE_SIZE;
          tunnelExX = (tunnelEnX + tunnelArea.ur.x - tunnelArea.ll.x + 1)*TILE_SIZE;
          tunnelExY = (tunnelEnY) * TILE_SIZE;
        }
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

  public double getStartX() {
    return startX;
  }

  public double getStartY() {
    return startY;
  }

  public double getStartT() {
    return startT;
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

  public void isTunnelHorizontal(boolean isHorizontal) {
    this.isTunnelHorizontal = isHorizontal;
  }

}
