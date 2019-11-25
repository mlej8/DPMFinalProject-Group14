package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    double theta = Math.atan2(currentX - binX * TILE_SIZE, currentY - binY * TILE_SIZE);

    double dx, dy;
    // calculate the intersection of the circle and the line
    dy = LAUNCH_RANGE * Math.cos(-theta) * TILE_SIZE;
    dx = LAUNCH_RANGE * Math.sin(theta) * TILE_SIZE;
    this.launchY = binY * TILE_SIZE + dy;
    this.launchX = binX * TILE_SIZE + dx;

    double top = island.ur.y * TILE_SIZE;
    double bottom = island.ll.y * TILE_SIZE;
    double left = island.ll.x * TILE_SIZE;
    double right = island.ur.x * TILE_SIZE;

    Point center = new Point(this.binX * TILE_SIZE, this.binY * TILE_SIZE);
    ArrayList<Point> intersections = new ArrayList<Point>();

    if (launchX <= left || launchX >= right || launchY <= bottom || launchY >= top) {
      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, currentX, intersections);
      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, currentY, intersections);
      int index = 0;
      for (Point p : intersections) {
        if (p.x <= left || p.x >= right || p.y <= bottom || p.y >= top) {
          intersections.remove(index);
        }
        index++;
      }
      double minDist = distance(intersections.get(0));
      Point nearestPoint = intersections.get(0);
      for (Point p : intersections) {
        double d = distance(p);
        if (d < minDist) {
          minDist = d;
          nearestPoint = p;
        }
      }

      this.launchX = nearestPoint.x;
      this.launchY = nearestPoint.y;
    }

  }


  /**
   * 
   * @param center A point indicating the center of a circle.
   * @param radius The radius of the circle.
   * @param x The given x value
   * @param intersects The modified list to store all intersections
   */
  private void calculateIntersectionX(Point center, double radius, double x, ArrayList<Point> intersects) {
    if (Math.abs((x - center.x) / radius) > 1) {
      return;
    }
    double theta = Math.asin((x - center.x) / radius);
    double y1 = center.y + radius * Math.cos(theta);
    double y2 = center.y - radius * Math.cos(theta);
    Point p1 = new Point(x, y1);
    Point p2 = new Point(x, y2);
    intersects.add(p1);
    intersects.add(p2);
  }

  /**
   * 
   * @param center A point indicating the center of a circle.
   * @param radius The radius of the circle.
   * @param y The given y value
   * @param intersects The modified list to store all intersections
   */
  private void calculateIntersectionY(Point center, double radius, double y, ArrayList<Point> intersects) {
    if (Math.abs((y - center.y) / radius) > 1) {
      return;
    }
    double theta = Math.acos((y - center.y) / radius);
    double x1 = center.x + radius * Math.sin(theta);
    double x2 = center.x - radius * Math.sin(theta);
    Point p1 = new Point(x1, y);
    Point p2 = new Point(x2, y);
    intersects.add(p1);
    intersects.add(p2);
  }

  private double distance(Point p) {
    return Math.hypot(p.x, p.y);
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
