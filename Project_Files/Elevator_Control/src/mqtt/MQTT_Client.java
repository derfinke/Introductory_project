package mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import elevator.ElevatorControl;
import elevator.ElevatorLogic;


public class MQTT_Client {
    private final MqttClient client;
    private final MqttConnectOptions options;
    private final int qos;
    private final boolean retained;
    private static final String PASSWORD = "DF7";
    private ElevatorLogic logic;
    private ElevatorControl control;

    public MQTT_Client(String name, String brokerAddress, ElevatorLogic logic, ElevatorControl control) throws MqttException {
        client = new MqttClient(brokerAddress, name);
        options = new MqttConnectOptions();
        options.setUserName("C2");
        options.setPassword(PASSWORD.toCharArray());
        options.setCleanSession(false);
        options.setConnectionTimeout(60);
        qos = 1;
        retained = false;
        this.logic = logic;
        this.control = control;
    }

    public void connect() throws MqttException {
        client.connect(options);
    }

    public void publish(String topic, String payload) throws MqttException {
        client.publish(topic, payload.getBytes(), qos, retained);
    } 

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic, MqttEventHandler);
    }
    
    public void disconnect() throws MqttException {
    	client.disconnect();
    }
    
    public void close() throws MqttException {
    	client.close();
    }
    
	public IMqttMessageListener MqttEventHandler = (topic, msg) -> {
		String payload = new String(msg.getPayload());
		JSONObject json = new JSONObject(payload);
		String[] keys = JSONObject.getNames(json);
		int floor;
		String doorState;
		boolean resetElevator;
		boolean emergencyStop;
		
		for (int i=0; i<keys.length; i++) {
		
			switch(keys[i]) {
				case "stopButtonDown":
					floor = json.getInt(keys[i]);
					//call function in ElevatorLogic pass key
					System.out.println("stopButtoDown Event:");
					System.out.println(floor);
					logic.FloorEventHandler(keys[i], floor);
					break;
				case "stopButtonUp":
					floor = json.getInt(keys[i]);
					//floor_request(up, json.getInt("stopButtonUp"));
					System.out.println("in StopButtonUp Event");
					System.out.println(floor);
					logic.FloorEventHandler(keys[i], floor);
					break;
				case "floorSelection":
					floor = json.getInt(keys[i]);
					System.out.println("floorSelection Event");
					//floor_request(getCurrentDirection(), json.getInt("floorSelection"));
					System.out.println(floor);
					logic.FloorEventHandler(keys[i], floor);
					break;
				case "doorButton":
					doorState = json.getString(keys[i]);
					System.out.println("doorButton Event");
					System.out.println(doorState);
					logic.DoorEventHandler(doorState);
					break;
				case "reset":
					resetElevator = json.getBoolean(keys[i]);
					System.out.println("reset Event");
					System.out.println(resetElevator);
					control.hard_reset();
					break;
				case "manualDoor":
					doorState = json.getString(keys[i]);
					System.out.println("manualDoor Event");
					System.out.println(doorState);
					logic.DoorEventHandler(doorState);
					break;
				case "emergencyStop":
					emergencyStop = json.getBoolean(keys[i]);
					System.out.println("emergencyStop Event");
					System.out.println(emergencyStop);
					control.emergencyStop(emergencyStop);
					break;
			}
		}
	};

}
