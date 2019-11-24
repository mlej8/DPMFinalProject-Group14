package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * Class that handles which sensor to use (US Sensor or Light Sensor) and fetch data from. 
 */
public class SensorPoller implements Runnable {
	
	/**
	 * Array to store ultrasonic data
	 */
    private float[] usDataFront;
    
    /**
     * Array to store ultrasonic data
     */
//    private float[] usDataLeft;
    
    /**
     * Array to store left light sensor data
     */
    private float[] leftLightData;
    
    /**
     * Array to store right light sensor data
     */
    private float[] rightLightData;
    
    /**
     * Mode indicating current state of Sensor Poller
     */
    private Mode mode;
    
    /**
     * States of sensor poller
     * 
     * @author Michael Li, Lora Zhang, Cecilia Jiang
     *
     */
    public enum Mode 
    { 
        US_LOCALIZATION, ULTRASONIC_LEFT, LIGHT, BOTH, IDLE; 
    } 
      
    public SensorPoller() {
        usDataFront = new float[usSensorFront.sampleSize()]; 
//        usDataLeft= new float[usSensorLeft.sampleSize()]; 
        leftLightData = new float[leftLightSensor.sampleSize()];                                    
        rightLightData = new float[rightLightSensor.sampleSize()];                                    
        this.mode = Mode.US_LOCALIZATION; // the mode is ultrasonic by default
    }

    /*
     * Sensors now return floats using a uniform protocol. Need to convert US result
     * to an integer [0,255] (non-Javadoc).
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
    	
        while (true) {
            if (mode==Mode.US_LOCALIZATION) {
            usSensorFront.getDistanceMode().fetchSample(usDataFront, 0);  // acquire distance data in meters and store it in
                                                                // usData (an array of float)
            ultrasonicLocalizer.processUSData((int) (usDataFront[0] * 100.0)); // extract from buffer (region of a physical
                                                                          // memory storage used to
                                                                          // temporarily store data while it is being moved from one place to
                                                                          // another), convert to cm, cast to int
            System.out.println(usDataFront[0] * 100.0);
            } else if (mode==Mode.LIGHT) {
              leftLightSensor.getRedMode().fetchSample(leftLightData, 0);
              rightLightSensor.getRedMode().fetchSample(rightLightData, 0);
              lightCorrector.processLightData(leftLightData[0] * 100.0, rightLightData[0] * 100.0);
            } else if (mode==Mode.BOTH) {
            	// light correction
            	leftLightSensor.getRedMode().fetchSample(leftLightData, 0);
                rightLightSensor.getRedMode().fetchSample(rightLightData, 0);
                lightCorrector.processLightData(leftLightData[0] * 100.0, rightLightData[0] * 100.0);
                
                // ultrasonic detection
                usSensorFront.getDistanceMode().fetchSample(usDataFront, 0);
//                usSensorLeft.getDistanceMode().fetchSample(usDataLeft, 0);
//                pController.processTwoUSData((int) (usDataFront[0] * 100.0), (int) (usDataLeft[0] * 100.0));
                pController.processUSData((int) (usDataFront[0] * 100.0));
            }
            if (mode==Mode.LIGHT) {
              Main.sleepFor(LIGHT_SLEEPINT);
            }else {
              Main.sleepFor(SLEEPINT);
            }
        }      
    }
    
    /**
     * Set current state of sensor poller. It can either be pooling data for ultrasonic sensor, light sensor, both sensors or not pooling data (IDLE).
     */
    public void setMode(Mode selectedMode) {
      mode = selectedMode;
    }
}