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
  
  public static String findDestAtan(double targetX, double targetY) {
    double[] curPosition = new double[] {currentX, currentY};
    double[] throwTo = new double[] {targetX, targetY};
    
    double theta = Math.atan2(currentX-targetX, currentY-targetY);
    
    double launchX, launchY;
    double dx,dy;
    
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
  /**
   * This method uses the given target position (targetX,targetY) to find the ideal launching
   * position. x and y are in unit cm. 
   * 
   * @param targetX
   * @param targetY
   */
  public static String findLaunchPoint(double targetX, double targetY, Point ll, Point ur) {
    double[] curPosition = new double[] {currentX, currentY};
    double[] throwTo = new double[] {targetX, targetY};
    double binX = targetX;
    double binY = targetY;
    double launchX, launchY;
    double dx,dy;

    double theta = Math.atan2(currentX - binX * TILE_SIZE, currentY - binY * TILE_SIZE);

    // calculate the intersection of the circle and the line
    dy = LAUNCH_RANGE * Math.cos(-theta) * TILE_SIZE;
    dx = LAUNCH_RANGE * Math.sin(theta) * TILE_SIZE;
    launchY = binY * TILE_SIZE + dy;
    launchX = binX * TILE_SIZE + dx;                  
    
    
    double top = ur.y * TILE_SIZE;
    double bottom = ll.y * TILE_SIZE;
    double left = ll.x * TILE_SIZE;
    double right = ur.x * TILE_SIZE;

    Point center = new Point(binX * TILE_SIZE, binY * TILE_SIZE);
    ArrayList<Point> intersections = new ArrayList<Point>();

    if (launchX <= left || launchX >= right || launchY <= bottom || launchY >= top) {
      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, left, intersections);
      calculateIntersectionX(center, LAUNCH_RANGE * TILE_SIZE, right, intersections);
      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, top, intersections);
      calculateIntersectionY(center, LAUNCH_RANGE * TILE_SIZE, bottom, intersections);
      
      int index = 0;
      int size = intersections.size();
      for (int i=0;i<size;i++) {
        System.out.println("index: "+index+" "+intersections.toString());
        Point p = intersections.get(index);
        if (p.x < left || p.x > right || p.y < bottom || p.y > top) {   //TODO: How to avoid hitting wall
          System.out.println("Removed a point");
          intersections.remove(index);
        }else {
          index++;
        }
      }
      System.out.println(intersections.toString());
      double minDist = distance(intersections.get(0),new Point(currentX, currentY));
      Point nearestPoint = intersections.get(0);
      for (Point p : intersections) {
        double d = distance(p,new Point(currentX, currentY));
        if (d < minDist) {
          minDist = d;
          nearestPoint = p;
        }
      }

      launchX = nearestPoint.x;
      launchY = nearestPoint.y;

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
  
  private static void calculateIntersectionY(Point center, double radius, double y, ArrayList<Point> intersects) {
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
  
  private static void calculateIntersectionX(Point center, double radius, double x, ArrayList<Point> intersects) {
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
  
  private static double distance(Point p1,Point p2) {
    return Math.hypot(p1.x-p2.x, p1.y-p2.y);
  }

  private static double keep3Digits(double number) {
    double n = Math.abs(number);
    int temp = (int)(n * 1000);
    //Add 1 if the difference is larger than 0.5
    if (n * 1000 - temp > 0.5) {
      temp += 1;
    }
    n = temp / 1000.0 + 0.000;
    if(number < 0) {
      number = -n;
    }else {
      number = n;
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
    calculated.add(new Point(1.000, 5.916));
    calculated.add(new Point(1.000, -5.916));
    assertEquals(calculated.toString(), points.toString());
  }
  
  @Test
  public void testDestination2() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionX(new Point(0,0), LAUNCH_RANGE, 2, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    calculated.add(new Point(2.000, 5.657));
    calculated.add(new Point(2.000, -5.657));
    assertEquals(calculated.toString(), points.toString());
  }
  
  @Test
  public void testDestination3() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionX(new Point(0,0), LAUNCH_RANGE, 8, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    assertEquals(calculated.toString(), points.toString());
  }
  
  @Test
  public void testDestination4() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionY(new Point(0,0), LAUNCH_RANGE, 4, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    calculated.add(new Point(4.472, 4.000));
    calculated.add(new Point(-4.472, 4.000));
    assertEquals(calculated.toString(), points.toString());
  }
  
  @Test
  public void testDestination5() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionY(new Point(0,0), LAUNCH_RANGE, 4, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    calculated.add(new Point(4.472, 4.000));
    calculated.add(new Point(-4.472, 4.000));
    assertEquals(calculated.toString(), points.toString());
  }
  
  @Test
  public void testDestination6() {
    ArrayList<Point> points = new ArrayList<Point>();
    calculateIntersectionY(new Point(0,0), LAUNCH_RANGE, -4, points);
    for(Point p: points) {
      p.x = keep3Digits(p.x);
      p.y = keep3Digits(p.y);
    }
    ArrayList<Point> calculated = new ArrayList<Point>();
    calculated.add(new Point(4.472, -4.000));
    calculated.add(new Point(-4.472, -4.000));
    assertEquals(calculated.toString(), points.toString());
  }
  
//  @Test
//  public void testIslandIntersections1() {
//    currentX = 0;
//    currentY = 2*TILE_SIZE;
//    ArrayList<Point> points = new ArrayList<Point>();
//    Point ll = new Point(-7,1);
//    Point ur = new Point(2,4);
//    findLaunchPoint(0, 0, ll, ur);
//    assertEquals("I am going to X position: " + keep3Digits(1.9) + " Y position: " + keep3Digits(2.0),
//         points.toString());
//  }
  
  @Test
  public void testIslandIntersections2() {
    currentX = 2.5*TILE_SIZE;
    currentY = 3*TILE_SIZE;
    ArrayList<Point> points = new ArrayList<Point>();
    Point ll = new Point(2,1);
    Point ur = new Point(8,4);
    findLaunchPoint(0, 0, ll, ur);
    assertEquals("I am going to X position: " + keep3Digits(1.9) + " Y position: " + keep3Digits(2.0),
         points.toString());
  }
  
  @Test
  public void testAngleY() {
    Point a = new Point (0,0);
    Point b = new Point (-1,-1);
    double theta = 0;
    double x = Math.abs(b.x - a.x);
    double y = Math.abs(b.y - a.y);
    if(b.x > a.x && b.y > a.y) {
      theta = - (Math.PI - Math.atan(x/y));
    }else if(b.x < a.x && b.y > a.y) {
      theta = Math.PI - Math.atan(x/y);
    }else if(b.x < a.x && b.y < a.y) {
      theta = Math.atan(x/y);
    }else if(b.x > a.x && b.y < a.y) {
      theta = - Math.atan(x/y);
    }
    System.out.println("binAngle = "+Math.toDegrees(theta));
  }
  
}
