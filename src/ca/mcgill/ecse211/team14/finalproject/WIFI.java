package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import java.util.ArrayList;

/**
 * The class that process wifi data and calculates start point, tunnel entrance point, tunnel exit point
 * and launch point.
 * @author Cecilia Jiang, Lora Zhang
 *
 */
public class WIFI {

  /**
   * Variable start point x coordinate in unit TILE_SIZE.
   */
  private int startX;

  /**
   * Variable start point y coordinate in unit TILE_SIZE.
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
   * Variable target facing angle to throw the ball.
   */
  private double binAngle;

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
  private double tunnelWidth;

  /**
   * Tunnel height.
   */
  private double tunnelHeight;
  
  /**
   * X-coordinate of nearest intersection point of launch Point.
   */
  private double launchIntersectionPointX;
  
  /**
   * Y-coordinate of nearest intersection point of launch Point.
   */
  private double launchIntersectionPointY;

  /**
   * Constructor for WIFI Class.
   */
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
    // calculate the intersection of the circle circumference and the line
    dy = LAUNCH_RANGE  * Math.cos(theta) * TILE_SIZE;
    dx = LAUNCH_RANGE * Math.sin(theta) * TILE_SIZE;
    this.launchY = binY * TILE_SIZE + dy;
    this.launchX = binX * TILE_SIZE + dx;                                               

    double top = (island.ur.y-0.6) * TILE_SIZE;
    double bottom = (island.ll.y+0.6) * TILE_SIZE;
    double left = (island.ll.x+0.6) * TILE_SIZE;
    double right = (island.ur.x-0.6) * TILE_SIZE;

    Point center = new Point(this.binX * TILE_SIZE, this.binY * TILE_SIZE);
    ArrayList<Point> intersections = new ArrayList<Point>();

    if (launchX < left || launchX > right || launchY < bottom || launchY > top) {
      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, left, intersections);
      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, right, intersections);
      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, top, intersections);
      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, bottom, intersections);
      int index = 0;
      int size = intersections.size();
      for (int i=0;i<size;i++) {
        Point p = intersections.get(index);
        if (p.x < left || p.x > right || p.y < bottom || p.y > top) {
          intersections.remove(index);
        }else {
          index++;
        }
      }
      if(intersections.size()!=0) {
        double minDist = distance(intersections.get(0),new Point(currentX, currentY));
        Point nearestPoint = intersections.get(0);
        for (Point p : intersections) {
        double d = distance(p,new Point(currentX, currentY));
        if (d < minDist) {
          minDist = d;
          nearestPoint = p;
        }
        }

        this.launchX = nearestPoint.x;
        this.launchY = nearestPoint.y;
      }else {
        this.launchX = 0;
        this.launchY = 0;
      }
    }
    
      this.launchIntersectionPointX = approximate(launchX);
      this.launchIntersectionPointY = approximate(launchY);
      setBinAngle();
  }
  
  /**
   * This method rounds a x or y coordinate down or up to match a grid line.
   * @param coordinate
   * @return rounded coordinate result, which corresponds to a grid line
   */
  private double approximate(double coordinate) {
     double tile = coordinate / TILE_SIZE;
     int result = (int) tile;
     if(tile - (int)tile >= 0.5)
       result +=1;
     double r = result * TILE_SIZE;
     return r;
  }
  
  /**
   * Calculate and set the launch angle based on the relative position of calculated launch point and bin point.
   */
  private void setBinAngle() {
    Point a = new Point(this.binX * TILE_SIZE, this.binY * TILE_SIZE);
    Point b = new Point (this.launchX, this.launchY);
    double theta = 0;
    double x = a.x - b.x;
    double y = a.y - b.y;
    theta = Math.atan2(x,y);
    this.binAngle = Math.toDegrees(theta);
  }

  /**
   * Calculate the intersection points of a circle's circumference with a vertical line.
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
   * Calculate the intersection points of a circle's circumference with a horizontal line.
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
  
  /**
   * Calculates the distance between two points.
   * @param p1
   * @param p2
   * @return the distance between two points
   */
  private double distance(Point p1,Point p2) {
    return Math.hypot(p1.x-p2.x, p1.y-p2.y);
  }


  /**
   * Sets the starting position (x,y) and start andgle T, unit is in tile_size.
   */
  private void findStartPoint() {

    if (redTeam == TEAM_NUMBER) {
      startCorner = redCorner;
    } else {
      startCorner = greenCorner;
    }
    switch (startCorner) {
      case 0:
        startX = 1;
        startY = 1;
        startT = 90;
        break;
      case 1:
        startX = (mapWidth - 1);
        startY = 1;
        startT = 0;
        break;
      case 2:
        startX = (mapWidth - 1);
        startY = (mapHeight - 1);
        startT = 270;
        break;
      case 3:
        startX = 1;
        startY = (mapHeight - 1);
        startT = 180;
        break;
    }
  }

  /**
   * Method that calculates and returns the coordinates at which the robot needs to travel to in order to enter the
   * tunnel and exit the tunnel. In final competition, we might split it into getTunnelEntrance and getTunnelExit.
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
    this.tunnelWidth = tunnelArea.ur.x - tunnelArea.ll.x;
    this.tunnelHeight = tunnelArea.ur.y - tunnelArea.ll.y;
    if (this.tunnelWidth != this.tunnelHeight) {
      // 2*1 tunnel
      switch (startCorner) {
        case 0:
          if (this.tunnelWidth > this.tunnelHeight) {
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
          if (this.tunnelWidth > this.tunnelHeight) {
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
          if (this.tunnelWidth > this.tunnelHeight) {
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
          if (this.tunnelWidth > this.tunnelHeight) {
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

  public double getlaunchX() {
    return this.launchX;
  }

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

  public double getTunnelHeight() {
    return tunnelHeight;
  }

  public double getTunnelWidth() {
    return tunnelWidth;
  }
  public double getLaunchIntersectionPointX() {
    return launchIntersectionPointX;
  }

  public double getLaunchIntersectionPointY() {
    return launchIntersectionPointY;
  }
  
  public double getBinX() {
    return binX;
  }

  public double getBinY() {
    return binY;
  }
  
  public double getBinAngle() {
    return binAngle;
  }
}
