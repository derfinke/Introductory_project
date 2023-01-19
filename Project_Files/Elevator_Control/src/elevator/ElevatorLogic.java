package elevator;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ElevatorLogic {
    static final int down = -1, up = 1;
	int requested_direction;
	int current_direction;
	int requested_floor;
	int current_floor;
	
	List<Integer> up_requests = new ArrayList<>();
	List<Integer> down_requests = new ArrayList<>();
	List<Integer> up_wait = new ArrayList<>();
	List<Integer> down_wait = new ArrayList<>();
	
	public IMqttMessageListener eventHandler = (topic, msg) -> {
		String payload = new String(msg.getPayload());
		JSONObject json = new JSONObject(payload);
		String[] keys = JSONObject.getNames(json);
		String timeStamp = json.getString("timestamp");
		
		for (int i=0; i<keys.length; i++) {
			switch(keys[i]) {
				case "stopButtonDown":
					requested_floor = json.getInt("stopButtonDown");
					requested_direction = down;
					floor_request(down);
					break;
				case "stopButtonUp":
					requested_floor = json.getInt("stopButtonUp");
					requested_direction = up;
					floor_request(up);
					break;
				case "floorSelection":
					requested_floor = json.getInt("floorSelection"); 
					floor_request(current_direction);
					break;
				case "floorArrived":
					//open / close door
				
			}
		}
	};
	
	public void mockEvent(String topic, JSONObject payload) throws Exception {
		eventHandler.messageArrived(topic, new MqttMessage(payload.toString().getBytes()));
	}
	
	
	private void add_request(List<Integer> list, int floor) {
		if (!list.contains(floor)) list.add(floor);
	}
	
	public void floor_request(int direction) {
		//current_floor = control.getCurrentFloor();
		if (direction != current_direction) { //stimmt angeforderte mit Fahrtrichtung überein
			if (current_direction == up) {
				add_request(down_requests, requested_floor);
			}
			else if (current_direction == down){
				add_request(up_requests, requested_floor);
			}
		}
		else if (direction * (requested_floor - current_floor) > 0){ //liegt requested_floor auf dem Weg?
			if (current_direction == up) {
				add_request(up_requests, requested_floor);
			}
			else if (current_direction == down){
				add_request(down_requests, requested_floor);
			}
		}
		else {
			if (current_direction == up) {
				add_request(up_wait, requested_floor);
			}
			else if (current_direction == down){
				add_request(down_wait, requested_floor);
			}
		}
		
	}
}
