package elevator;
import org.apache.commons.lang3.time.*;
import java.util.concurrent.TimeUnit;


public class Main {
	
//	private static ElevatorControl control;
//	private static StopWatch myStopWatch;
//	private static ElevatorLogic logic;
	
    public static void main(String[] args) throws Exception {
    	ElevatorLogic logic = new ElevatorLogic();
    	ElevatorControl control  = new ElevatorControl(logic);
    	logic.initControl(control);
    	new Testbench(logic);  
    	control.start();
    }
}
