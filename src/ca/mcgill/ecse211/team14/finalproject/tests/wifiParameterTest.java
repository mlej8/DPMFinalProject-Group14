package ca.mcgill.ecse211.team14.finalproject.tests;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;
import ca.mcgill.ecse211.team14.finalproject.WIFI;

public class wifiParameterTest {
 
  public static void main(String args[]) {
     System.out.println("Running ");
     
     WIFI wifi = new WIFI();
     System.out.println("Finished");
     System.out.println("WIFI class launch point is ("+wifi.getlaunchX()+", "+wifi.getlaunchY()+")");
     if(wifi.getlaunchX()==bin.x&&wifi.getlaunchY()==bin.y) {
       System.out.println("Correct");
     }
     System.out.println("WIFI class start point is ("+wifi.getStartX()+", "+wifi.getStartY()+")");
     System.out.println("WIFI class tunnel entrance point is ("+wifi.getTunnelEnX()+", "+
     wifi.getTunnelEnY()+")");
     System.out.println("WIFI class tunnel exit point is ("+wifi.getTunnelExX()+", "+wifi.getTunnelExY()+")");
  }
}
