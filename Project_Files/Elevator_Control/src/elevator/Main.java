package elevator;
import org.apache.commons.lang3.time.*;
import java.util.concurrent.TimeUnit;

public class Main {
	
	private static ElevatorControl control;
	private static int Direction = 0;
	private static int wishedFloor = 0;
	private static long time_ms = 0;
	private static StopWatch myStopWatch;
	private static ElevatorLogic logic;
	
    public static void main(String[] args) throws Exception {
    	logic = new ElevatorLogic();
    	control = new ElevatorControl(logic);
    	control.reset();
    	logic.initControl(control);
    	new Testbench(logic);
    	while(true)
    	{
    		wishedFloor = logic.getTargetFloor();
    		Direction = logic.getCurrentDirection();
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
//    		System.out.println(control.getCurrentFloor());
    	}
    }
}
