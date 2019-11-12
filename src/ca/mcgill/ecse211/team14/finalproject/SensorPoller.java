package ca.mcgill.ecse211.team14.finalproject;

import static ca.mcgill.ecse211.team14.finalproject.Resources.*;

/**
 * Class that handles which sensor to use (US Sensor or Light Sensor) and fetch data from. 
 */
public class SensorPoller implements Runnable {
	
	/**
	 * Array to store ultrasonic data
	 */
    private float[] usData;
    
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
        ULTRASONIC, LIGHT, BOTH, IDLE; 
    } 
      
    public SensorPoller() {
        usData = new float[usSensor.sampleSize()]; // create an array of float of size corresponding to the number of
                                                    // elements in a sample. The number of elements does not change.
        leftLightData = new float[leftLightSensor.sampleSize()];                                    
        rightLightData = new float[rightLightSensor.sampleSize()];                                    
        this.mode = Mode.ULTRASONIC; // the mode is ultrasonic by default
    }

    /*
     * Sensors now return floats using a uniform protocol. Need to convert US result
     * to an integer [0,255] (non-Javadoc).
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
    	
        while (true) {
            if (mode==Mode.ULTRASONIC) {
            usSensor.getDistanceMode().fetchSample(usData, 0);  // acquire distance data in meters and store it in
                                                                // usData (an array of float)
            ultrasonicLocalizer.processUSData((int) (usData[0] * 100.0)); // extract from buffer (region of a physical
                                                                          // memory storage used to
                                                                          // temporarily store data while it is being moved from one place to
                                                                          // another), convert to cm, cast to int
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
                usSensor.getDistanceMode().fetchSample(usData, 0);
                ultrasonicLocalizer.processUSData((int) (usData[0] * 100.0));
            }
            Main.sleepFor(SLEEPINT);
        }      
    }
    
    /**
     * Set current state of sensor poller. It can either be pooling data for ultrasonic sensor, light sensor, both sensors or not pooling data (IDLE).
     */
    public void setMode(Mode selectedMode) {
      mode = selectedMode;
    }
}