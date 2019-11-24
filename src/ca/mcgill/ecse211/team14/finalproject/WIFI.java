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
   * Variable target launch position x coordinate.
   */

  private double binX;

  /**
   * Variable target launch position y coordinate.
   */
  private double binY;

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

  /**
   * Tunnel width.
   */
  private int tunnelWidth; // TODO: keep this?

  /**
   * Tunnel height.
   */
  private int tunnelHeight;

  public WIFI() {
    setBinPosition();
    findStartPoint();
    findTunnelEnEx();
  }

  /**
   * This methods sets the bin of current run.
   */
  private void setBinPosition() {
    if (redTeam == TEAM_NUMBER) {
      binX = redBin.x;
      binY = redBin.y;
    } else if (greenTeam == TEAM_NUMBER) {
      binX = greenBin.x;
      binY = greenBin.y;
    }
  }

  /**
   * This method uses the given target position (binX,binY) to find the ideal launching position.
   */
  public void findLaunchPosition() {

    double currentX = odometer.getXYT()[0];
    double currentY = odometer.getXYT()[1];

    double theta = Math.atan2(currentX - binX, currentY - binY);

    double dx, dy;
    // calculate the intersection of the circle and the line
    dy = LAUNCH_RANGE * Math.cos(-theta);
    dx = LAUNCH_RANGE * Math.sin(theta);
    this.launchY = binY + dy;
    this.launchX = binX + dx;

    int launchx = (int) this.launchX;
    int launchy = (int) this.launchY;

    this.launchX = launchx * TILE_SIZE;
    this.launchY = launchy * TILE_SIZE;
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
        startX = 1;
        startY = 1; // (1,1)
        startT = 90;
        break;
      case 1:
        startX = (mapWidth - 1);
        startY = 1; // (14, 1)
        startT = 0;
        break;
      case 2:
        // startX = (mapWidth - 0.5) * TILE_SIZE;
        // startY = (mapHeight - 0.5) * TILE_SIZE;
        startX = (mapWidth - 1);
        startY = (mapHeight - 1); // (14, 8)
        startT = 180;
        break;
      case 3:
        // startX = 0.5 * TILE_SIZE;
        // startY = (mapHeight - 0.5) * TILE_SIZE;
        startX = 1;
        startY = (mapHeight - 1); // (1, 8)
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
      tunnelArea = tnr;
      startArea = red;
    } else if (greenTeam == TEAM_NUMBER) {
      tunnelArea = tng;
      startArea = green;
    }

    // TODO: Considering current start area corner's coordinate and tunnel's coordinates, compute whether tunnel is
    // horizontal or vertical
    // TODO: Compute tunnel's width and tunnel's height: width = 1, height = 2?


    double w = tunnelArea.ur.x - tunnelArea.ll.x;
    double h = tunnelArea.ur.y - tunnelArea.ll.y;
    if (w != h) {
      // 2*1 tunnel
      switch (startCorner) {
        case 0:
          if (w > h) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          }
          break;
        case 1:
          if (w > h) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          }
          break;
        case 2:
          if (w > h) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          }
          break;
        case 3:
          if (w > h) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          }
          break;
      }

    } else {
      // 1*1 tunnel
      switch (startCorner) {
        case 0:
          if (tunnelArea.ll.x == startArea.ur.x) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else if (tunnelArea.ll.y == startArea.ur.y) {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          }
          break;
        case 1:
          if (tunnelArea.ur.x == startArea.ll.x) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else if (tunnelArea.ll.y == startArea.ur.y) {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y - 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ur.y + 1) * TILE_SIZE;
          }
          break;
        case 2:
          if (tunnelArea.ur.x == startArea.ll.x) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else if (tunnelArea.ur.y == startArea.ll.y) {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          }
          break;
        case 3:
          if (tunnelArea.ll.x == startArea.ur.x) {
            isTunnelHorizontal = true;
            tunnelEnX = (tunnelArea.ll.x - 1) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ll.y + 0.5) * TILE_SIZE;
            tunnelExX = (tunnelArea.ur.x + 1) * TILE_SIZE;
            tunnelExY = tunnelEnY;
          } else if (tunnelArea.ur.y == startArea.ll.x) {
            isTunnelHorizontal = false;
            tunnelEnX = (tunnelArea.ll.x + 0.5) * TILE_SIZE;
            tunnelEnY = (tunnelArea.ur.y + 1) * TILE_SIZE;
            tunnelExX = tunnelEnX;
            tunnelExY = (tunnelArea.ll.y - 1) * TILE_SIZE;
          }
          break;
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

  public int getStartX() {
    return startX;
  }

  public int getStartY() {
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

  public int getTunnelHeight() {
    return tunnelHeight;
  }

  public int getTunnelWidth() {
    return tunnelWidth;
  }

}
