package Control;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws MqttException, IOException {
    	ElevatorControl control = new ElevatorControl();
	    MQTT_Client client = new MQTT_Client("elevator control", "tcp://192.168.0.172:1883", control.eventHandler);
		client.connect();
		client.subscribe("test");
	    control.referenceClient(client);
    }
}
