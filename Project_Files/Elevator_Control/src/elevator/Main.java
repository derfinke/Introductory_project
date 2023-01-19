package elevator;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Main {
	
	private static ElevatorControl control;
	
	private static void mockEvent(String topic, String payload) throws Exception {
		control.eventHandler.messageArrived(topic, new MqttMessage(payload.getBytes()));
	}

    public static void main(String[] args) throws Exception {
    	control = new ElevatorControl();
	    MQTT_Client client = new MQTT_Client("elevator control", "tcp://192.168.0.172:1883", control.eventHandler);
		client.connect();
		client.subscribe("#");
	    control.referenceClient(client);
	    mockEvent("topic", "payload");
    }
}
