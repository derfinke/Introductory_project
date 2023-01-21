package elevator;
import org.apache.commons.lang3.time.*;

import mqtt.MQTT_Client;

import java.util.concurrent.TimeUnit;


public class Main {
	
//	private static ElevatorControl control;
//	private static StopWatch myStopWatch;
//	private static ElevatorLogic logic;
	
    public static void main(String[] args) throws Exception {
    	ElevatorLogic logic = new ElevatorLogic();
    	ElevatorControl control  = new ElevatorControl(logic);
    	control.reset();
    	logic.initControl(control);
    	MQTT_Client subscriber = new MQTT_Client("C2", "tcp://ea-pc165.ei.htwg-konstanz.de:1883", logic, control);
    	subscriber.connect();
    	subscriber.subscribe("/22WS-SysArch/C2/");
    	control.start();
    }
}
