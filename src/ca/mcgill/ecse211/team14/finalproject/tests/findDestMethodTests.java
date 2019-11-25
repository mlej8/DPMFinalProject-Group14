package ca.mcgill.ecse211.team14.finalproject.tests;

import ca.mcgill.ecse211.team14.finalproject.WIFI;
import ca.mcgill.ecse211.team14.finalproject.Resources.Point;
import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;

public class findDestMethodTests {
  static double currentX;
  static double currentY;
  static double launchRange;
  final static double BOUNDARY=0;
  
  /**
   * This method uses the given target position (targetX,targetY) to find the ideal launching
   * position. x and y are in unit cm. 
   * 
   * @param targetX
   * @param targetY
   */
  public static String findDestAtan(double targetX, double targetY) {
    double[] curPosition = new double[] {currentX, currentY};
    double[] throwTo = new double[] {targetX, targetY};
    
    double theta = Math.atan2(currentX-targetX, currentY-targetY);
    
    double launchX, launchY;
    double dx,dy;
    // calculate the intersection of the circle and the line
//    if(theta < 0) { // when the robot is in 2nd/3rd quadrant
//      dy =   launchRange * Math.cos(-theta);
//      dx = - launchRange * Math.sin(-theta);
//      launchY = targetY + dy;
//      launchX = targetX + dx;
//    } else {  // in 1st/4th quadrant
//      dy =   launchRange * Math.cos(theta);
//      dx =   launchRange * Math.sin(theta);
//      launchY = targetY + dy;
//      launchX = targetX + dx; // TODO: test later
//    }
    
    dy =   launchRange * Math.cos(-theta);
    dx =   launchRange * Math.sin(theta);
    launchY = targetY + dy;
    launchX = targetX + dx;
    if(launchX <= BOUNDARY || launchY <= BOUNDARY) {
      double[] target = findCircle(curPosition, throwTo);
      launchX = target[0];
      launchY = target[1];
    }
    System.out.println("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY));
    return "I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY);
    
  }
  
  private static double[] findCircle (double[] curPos, double[] center) {
    double[] target = new double[2];
    if(center[0] > center[1]) { // upper half
      double tX = curPos[0];
      double tY = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos[0] - center[0]),2)) + center[1];
      target = new double[]{tX, tY};
    }else {  // lower half
      double tY = curPos[1];
      double tX = Math.sqrt(Math.pow(launchRange, 2) - Math.pow((curPos[1] - center[1]),2)) + center[0];
      target = new double[] {tX, tY};
    }
    return target;
  }
  
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
   * This method uses the given target position (binX,binY) to find the ideal launching position.
   */
//  public void findLaunchPosition() {
//
//    double currentX = odometer.getXYT()[0];
//    double currentY = odometer.getXYT()[1];
//
//    double theta = Math.atan2(currentX - binX * TILE_SIZE, currentY - binY * TILE_SIZE);
//
//    double dx, dy;
//    // calculate the intersection of the circle and the line
//    dy = LAUNCH_RANGE * Math.cos(-theta) * TILE_SIZE;
//    dx = LAUNCH_RANGE * Math.sin(theta) * TILE_SIZE;
//    this.launchY = binY * TILE_SIZE + dy;
//    this.launchX = binX * TILE_SIZE + dx;
//
//    double top = (island.ur.y-1) * TILE_SIZE;
//    double bottom = (island.ll.y+1) * TILE_SIZE;
//    double left = (island.ll.x+1) * TILE_SIZE;
//    double right = (island.ur.x-1) * TILE_SIZE;
//
//    Point center = new Point(this.binX * TILE_SIZE, this.binY * TILE_SIZE);
//    ArrayList<Point> intersections = new ArrayList<Point>();
//
//    if (launchX <= left || launchX >= right || launchY <= bottom || launchY >= top) {
//      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, currentX, intersections);
//      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, currentY, intersections);
//      int index = 0;
//      for (Point p : intersections) {
//        if (p.x <= left || p.x >= right || p.y <= bottom || p.y >= top) {
//          intersections.remove(index);
//        }
//        index++;
//      }
//      double minDist = distance(intersections.get(0));
//      Point nearestPoint = intersections.get(0);
//      for (Point p : intersections) {
//        double d = distance(p);
//        if (d < minDist) {
//          minDist = d;
//          nearestPoint = p;
//        }
//      }
//
//      this.launchX = nearestPoint.x;
//      this.launchY = nearestPoint.y;
//      
//      // test whether launchX/Y on the circle
//      double diffX = Math.abs(this.binX - this.launchX);
//      double diffY = Math.abs(this.binY - this.launchY);
//      double dist = Math.hypot(diffX, diffY);
//      if(Math.abs(dist - LAUNCH_RANGE*TILE_SIZE) < 10) {
//        System.out.println("On the circle!!!");
//      }else {
//        System.out.println("!!!!!!NOT ON THE CIRCLE!!!!!");
//      }
//    }
//
//  }
  
  private static double keep3Digits(double number) {
    double n = 0;
    if(number < 0) {
      n = number;
    }
    number = Math.abs(number);
    int temp = (int)(number * 1000);
    //Add 1 if the difference is larger than 0.5
    if (number * 1000 - temp > 0.5) {
      temp += 1;
    }
    number = temp / 1000.0 + 0.000;
    if(n < 0) {
      number = -number;
    }
    return number;
  }
  
  
  
  @Test
  public void testQuadrant1ViaAtan() {
    currentX = 5;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant2ViaAtan() {
    currentX = 1;
    currentY = 5;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(135));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(135));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant3ViaAtan() {
    currentX = 1;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3-launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testQuadrant4ViaAtan() {
    currentX = 5;
    currentY = 1;
    launchRange = Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 3;
    double launchX = 3+launchRange*Math.sin(Math.toRadians(45));
    double launchY = 3-launchRange*Math.cos(Math.toRadians(45));
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testBelowBoundaryGoRight() {
    currentX = 1;
    currentY = 1;
    launchRange = 3 * Math.pow(2, 0.5);
    double targetX = 2;
    double targetY = 3;
    double launchY = currentY;
    double launchX = Math.pow(Math.pow(launchRange, 2) - Math.pow(currentY-targetY, 2),0.5)+targetX;
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testBelowBoundaryGoUp() {
    currentX = 1;
    currentY = 1;
    launchRange = 3 * Math.pow(2, 0.5);
    double targetX = 3;
    double targetY = 2;
    double launchY = Math.pow(Math.pow(launchRange, 2) - Math.pow(currentX-targetX, 2),0.5)+targetY;
    double launchX = currentX;
    assertEquals("I am going to X position: " + keep3Digits(launchX) + " Y position: " + keep3Digits(launchY), 
        findDestAtan(targetX,targetY));
  }
  
  @Test
  public void testDestination() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionX(new Point(0,0), LAUNCH_RANGE, 1, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    calculated.add(new Point(1, 5.916));
    calculated.add(new Point(1, -5.916));
    assertEquals(calculated, points);
  }
}
