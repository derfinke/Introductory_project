package elevator;

public class Main {
	
	private static ElevatorLogic logic;
	
	

    public static void main(String[] args) throws Exception {
    	logic = new ElevatorLogic();
	    /*MQTT_Client client = new MQTT_Client("elevator control", "tcp://192.168.0.172:1883", logic.eventHandler);
		client.connect();
		client.subscribe("#");*/
    	new Testbench(logic);
    }
}
