package elevator;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class Main {
	
	private static ElevatorLogic logic;
	
	public static void mockEvent(String topic, JSONObject payload) throws Exception {
		logic.eventHandler.messageArrived(topic, new MqttMessage(payload.toString().getBytes()));
	}

    public static void main(String[] args) throws Exception {
    	logic = new ElevatorLogic();
	    /*MQTT_Client client = new MQTT_Client("elevator control", "tcp://192.168.0.172:1883", logic.eventHandler);
		client.connect();
		client.subscribe("#");
		ElevatorLogic logic = new ElevatorLogic();
		
	    JSONObject json = new JSONObject();
		json.put("floorSelection", 4);
		json.put("timestamp", "kurz vor knapp");
	    mockEvent("topic", json);*/
    	new Testbench(logic);
    }
}
