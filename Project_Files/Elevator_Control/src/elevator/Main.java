package elevator;
import org.apache.commons.lang3.time.*;
import java.util.concurrent.TimeUnit;

public class Main {
	
	private static ElevatorControl control;
	private static int Direction = -1;
	private static int wishedFloor = 1;
	private static long time_ms = 0;
	private static StopWatch myStopWatch;
	
    public static void main(String[] args) throws Exception {
    	
    	control = new ElevatorControl();
    	//Create Logic Object
    	control.motorV2Down();
//    	control.motorV2Up();
    	myStopWatch = new StopWatch();
    	myStopWatch.start();
    	while(true)
    	{
    		time_ms =  myStopWatch.getTime(TimeUnit.MILLISECONDS);
    		control.readSensor();
    		//Direction = Logic_Object.getCurrentDirection();
    		if(wishedFloor != 0)
    		{
    			control.setCurrentFloor(Direction);
    			control.ApproachStop(wishedFloor, Direction);
    		}
    		else
    		{
    			
    		}
    		if(time_ms >= 200)
    		{
    			wishedFloor = 2;
    		}
    		System.out.println(control.getCurrentFloor());
    	}
    }
}
