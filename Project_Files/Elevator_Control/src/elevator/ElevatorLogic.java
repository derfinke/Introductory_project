package elevator;
import org.json.JSONObject;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ElevatorLogic {
    static final int down = -1, up = 1, none=0;
	int current_direction = none;
	int current_floor;
	int next_target_floor;
	boolean init = false;
	int init_direction;
	int init_floor;
	boolean init_request;
	ElevatorControl control;
	
	List<Integer> up_requests = new ArrayList<>();
	List<Integer> down_requests = new ArrayList<>();
	List<Integer> up_wait = new ArrayList<>();
	List<Integer> down_wait = new ArrayList<>();
	
	public IMqttMessageListener eventHandler = (topic, msg) -> {
		String payload = new String(msg.getPayload());
		JSONObject json = new JSONObject(payload);
		String[] keys = JSONObject.getNames(json);
		//String timeStamp = json.getString("timestamp");
		
		for (int i=0; i<keys.length; i++) {
			switch(keys[i]) {
				case "stopButtonDown":
					floor_request(down, json.getInt("stopButtonDown"));
					break;
				case "stopButtonUp":
					floor_request(up, json.getInt("stopButtonUp"));
					System.out.println("in StopButtonUp Event");
					break;
				case "floorSelection":
					floor_request(current_direction, json.getInt("floorSelection"));
					break;
				case "floorArrived":
					floor_arrived();
					System.out.println("in floorArrived Event");
					break;
			}
		}
	};
	
	public void mockEvent(String topic, JSONObject payload) throws Exception {
		eventHandler.messageArrived(topic, new MqttMessage(payload.toString().getBytes()));
	}
	
	
	private void add_request(List<Integer> list, int floor) {
		if (!list.contains(floor)) list.add(floor);
	}
	
	public void floor_request(int direction, int target_floor) {
		current_floor = control.getCurrentFloor();
		if (current_direction == none) { // init_request
			if (direction != none) { // from outside
				init_direction = direction; // direction after init_floor is reached
				init_floor = control.getCurrentFloor(); // save target_floor
				init = true; //flag to block new requests from changing the next_target_floor until the init_floor is reached
				init_request = true; //current request is init_request
			}
			if (target_floor - current_floor > 0) {
				direction = up;
			}
			else if (target_floor - current_floor < 0) {
				direction = down;
			}
			current_direction = direction;
		}
		if (direction != current_direction) { // requested direction is unequal to current_direction
			if (current_direction == up) {
				add_request(down_requests, target_floor);
			}
			else if (current_direction == down){
				add_request(up_requests, target_floor);
			}
		}
		else if (direction * (target_floor - current_floor) > 0 && !init || init_request){ //
			if (current_direction == up) {
				add_request(up_requests, target_floor);
			}
			else if (current_direction == down){
				add_request(down_requests, target_floor);
			}
		}
		else {
			if (current_direction == up) {
				add_request(up_wait, target_floor);
			}
			else if (current_direction == down){
				add_request(down_wait, target_floor);
			}
		}
		update_next_target_floor();
		init_request = false;
	}
	
	private void delete_complied_requests() {
		if (current_direction == up) {
			up_requests.removeIf(floor -> floor.equals(current_floor));
		}
		else if (current_direction == down) {
			down_requests.removeIf(floor -> floor.equals(current_floor));
		}
	}
	
	private boolean update_current_direction() {
		int last_direction = current_direction;
		if (init) {
			current_direction = init_direction;
			init = false;
			return last_direction != current_direction;
		}
		if (down_requests.isEmpty() && up_requests.isEmpty()) {
			current_direction = none;  // return to initial state
		}
		if (current_direction == down) {
			if (current_floor == 1) { // if elevator reached end of direction
				current_direction = up;
			}
			if (down_requests.isEmpty() && !up_requests.isEmpty()) {
				if (Collections.min(up_requests) > current_floor) { // if no more down_requests and no reachable up_request
					current_direction = up;
				}
			}
		}
		else if (current_direction == up) {
			if (current_floor == 4) { // if elevator reached end of direction
				current_direction = down;
			}
			if (up_requests.isEmpty() && !down_requests.isEmpty()) {
				if (Collections.max(down_requests) < current_floor) { // if no more up_requests and no reachable down_request
					current_direction = down;
				}
			}
		}
		return last_direction != current_direction;
	}
	
	private void forward_wait_lists() {
		if (current_direction == up) {
			down_requests.addAll(down_wait);
			down_wait.clear();
		}
		else if(current_direction == down) {
			up_requests.addAll(up_wait);
			up_wait.clear();
		}
	}
	
	private void update_next_target_floor() {
		if(!init) { //!init
			next_target_floor = init_floor;
			return;
		}
		if (current_direction == up) {
			if(!up_requests.isEmpty()) {
				next_target_floor = Collections.min(up_requests); //choose lowest request, that is still above current floor
				control.motorV2Up();
			}
			else if(!down_requests.isEmpty()){ //if no more up_requests check for highest down_request target, that is still above current floor
				int max_target = 1;
				for (int i=0; i< down_requests.size(); i++) {
					int dr = down_requests.get(i);
					if (dr > current_floor && dr > max_target) {
						max_target = dr;
					}
				}
				next_target_floor = max_target;
				control.motorV2Up();
			}
		}
		else if(current_direction == down) {
			if(!down_requests.isEmpty()) {
				next_target_floor = Collections.max(down_requests); //choose highest request, that is still below current floor
				control.motorV2Down();
			}
			else if(!up_requests.isEmpty()){ //if no more down_requests check for lowest up_request target, that is still below current floor
				int min_target = 4;
				for (int i=0; i< up_requests.size(); i++) {
					int ur = up_requests.get(i);
					if (ur < current_floor && ur < min_target) {
						min_target = ur;
					}
				}
				next_target_floor = min_target;
				control.motorV2Down();
			}
		}
	}
	
	public void floor_arrived() {
		//open / close door
		delete_complied_requests();
		
		if(update_current_direction()) {
			forward_wait_lists();
		};
		
		update_next_target_floor();
	}
	
	public int getTargetFloor()
	{
		return this.next_target_floor;
	}
	
	public int getCurrentDirection()
	{
		return this.current_direction;
	}
	
	public void initControl(ElevatorControl control)
	{
		this.control = control;
	}
}
