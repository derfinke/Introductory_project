package elevator;
import org.json.JSONObject;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ElevatorLogic {
    private static final int down = -1, up = 1, none=0;
	private int current_direction = none;
	public int current_floor;
	private int next_target_floor;
	private boolean wait_first_floor_arrived = false;
	private boolean first_request = false;
	private int init_direction;
	private int first_floor_request;
	private boolean down_for_up_request = false;
	private boolean up_for_down_request = false;

	private ElevatorControl control;
	
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
					floor_request(none, json.getInt("floorSelection"));
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
	
	public void floor_request(int requested_direction, int target_floor) {
		//System.out.println("wanted direction:" + requested_direction);
		//setCurrentFloor(control.getCurrentFloor());
		if (getCurrentDirection() == none) { // init_request
			if (requested_direction != none) { // from outside
				init_direction = requested_direction; // direction after init_floor is reached
				first_floor_request = target_floor; // save target_floor
				wait_first_floor_arrived = true; //flag to block new requests from changing the next_target_floor until the init_floor is reached
				first_request = true;
			}
			else {
				first_request = true;
			}
		}
		if(requested_direction == none || first_request) {
			if (target_floor - current_floor > 0) {
				requested_direction = up;
			}
			else if (target_floor - current_floor < 0) {
				requested_direction = down;
			}
			if(first_request) {
				current_direction = requested_direction;
			}
		}
		if (requested_direction != getCurrentDirection()) { // requested direction is unequal to current_direction
			if (getCurrentDirection() == up) {
				add_request(down_requests, target_floor);
			}
			else if (getCurrentDirection() == down){
				add_request(up_requests, target_floor);
			}
		}
		else if (requested_direction * (target_floor - current_floor) > 0){ // if in correct direction and on the way
			if (getCurrentDirection() == up) {
				add_request(up_requests, target_floor);
			}
			else if (getCurrentDirection() == down){
				add_request(down_requests, target_floor);
			}
		}
		else { //If right direction but not on the current way
			if (current_floor > target_floor && current_direction == up) {					
				add_request(up_wait, target_floor); //was up_wait
			}
			else if (current_floor < target_floor && current_direction == down){			
				add_request(down_wait, target_floor); //was down_wait
			}
		}
		update_next_target_floor();
		
		if (first_request) {
			first_request = false;
		}
		printElevatorInfo(current_floor); //comment for debugging in testbench
	}


	
	private void delete_complied_requests() {
		if (getCurrentDirection() == up || down_for_up_request) {
			up_requests.removeIf(floor -> floor.equals(current_floor));
			if(down_for_up_request) {
				down_for_up_request = false;
			}
		}
		if (getCurrentDirection() == down || up_for_down_request) {
			down_requests.removeIf(floor -> floor.equals(current_floor));
			if(up_for_down_request) {
				up_for_down_request = false;
			}
		}
	}
	
	private boolean update_current_direction() {
		int last_direction = getCurrentDirection();
		if (wait_first_floor_arrived && current_floor == first_floor_request) {
			current_direction = init_direction;
			wait_first_floor_arrived = false;
			return last_direction != current_direction;
		}
		if (down_requests.isEmpty() && up_requests.isEmpty()) { 
			if(forward_wait_lists(toggleDirection())) { //check wait_lists from other direction. Contains requests and forwards to request list
				current_direction = toggleDirection();
				return true;				// if yes, check direction again
			}
			else {
				current_direction = none;
			}
			
			
		}
		if (getCurrentDirection() == down) {
			if (current_floor == 1) { // if elevator reached end of direction
				current_direction = up;
			}
			if (down_requests.isEmpty() && !up_requests.isEmpty()) {
				if (Collections.min(up_requests) > current_floor) { // if no more down_requests and no reachable up_request
					current_direction = up;
				}
				else {
					down_for_up_request = true;
					delete_complied_requests();
					update_current_direction();
				}
			}
		}
		else if (getCurrentDirection() == up) {
			if (current_floor == 4) { // if elevator reached end of direction
				current_direction = down;
			}
			if (up_requests.isEmpty() && !down_requests.isEmpty()) {
				if (Collections.max(down_requests) < current_floor) { // if no more up_requests and no reachable down_request
					current_direction = down;
				}
				else {
					up_for_down_request = true;
					delete_complied_requests();
					update_current_direction();
				}
			}
		}
		return last_direction != current_direction;
	}
	
	private boolean forward_wait_lists(int direction) {
		
		System.out.println("Forward wait list");
		if (direction == up) {
			if(down_wait.isEmpty()) {
				return false;
			}
			
			down_requests.addAll(down_wait);
			down_wait.clear();
			
		}
		else if(direction == down) {
			if(up_wait.isEmpty()) {
				return false;
			}
			up_requests.addAll(up_wait);
			up_wait.clear();
		}
		
		return true;
	}
	
	private int toggleDirection() {
		if(current_direction == up) {
			return down;
		}
		else if(current_direction == down){
			return up;
		}
		else {
			return none;
		}
	}
	
	private void update_next_target_floor() {
		
		if (current_direction == up) {
			if(!up_requests.isEmpty()) {
				next_target_floor = Collections.min(up_requests); //choose lowest request, that is still above current floor
				System.out.println("In next Target Floor Up");
				
			}
			else if(!down_requests.isEmpty()){ //if no more up_requests check for highest down_request target, that is still above current floor
				int max_down_request = Collections.max(down_requests);
				if(max_down_request > current_floor) {
					next_target_floor = max_down_request;
					System.out.println("In next Target Floor Up für down request");
				}
			}
				
		}
		else if(current_direction == down) {
			if(!down_requests.isEmpty()) {
				next_target_floor = Collections.max(down_requests); //choose highest request, that is still below current floor
				System.out.println("In next Target Floor Down");
				
			}
			else if(!up_requests.isEmpty()){ //if no more down_requests check for lowest up_request target, that is still below current floor
				int min_up_request = Collections.min(up_requests);
				if(min_up_request < current_floor) {
					next_target_floor = min_up_request;
					System.out.println("In next Target Floor Down aber up");
				}
			}
		}
	}
	
	public void floor_arrived() {
		
		
		delete_complied_requests();
		if(update_current_direction()) {
			forward_wait_lists(current_direction);
		};
		System.out.println("floor_arrived: before update");
		update_next_target_floor();
		//printElevatorInfo(current_floor); //uncommment for debugging
	}
	
	public int getTargetFloor()
	{
		return this.next_target_floor;
	}
	
	
	public void setCurrentFloor(int floor) {
		this.current_floor = floor;
	}
	
	public int getCurrentDirection() {
		return this.current_direction;
	}
	
	public void setCurrentDirection(int direction) {
		this.current_direction = direction;
	}

	
	public void initControl(ElevatorControl control)
	{
		this.control = control;
	}
	
	
	
	
	public void printElevatorInfo(int floor) {
        for(int i=0;i<15;i++) {
            System.out.printf("");
        }
        System.out.printf("\nTESTOUTPUT: (floor " + floor + ")\n");
        for(int i=0;i<15;i++) {
            System.out.printf("");
        }
        System.out.printf("\n");

        System.out.printf("down_requests:\n");
        for(int i = 0; i < down_requests.size(); i++) {
            System.out.printf("%d\n", down_requests.get(i));
        }
        System.out.printf("\nup_requests:\n");
        for(int i = 0; i < up_requests.size(); i++) {
            System.out.printf("%d\n", up_requests.get(i));
        }
        System.out.printf("\ndown_wait:\n");
        for(int i = 0; i < down_wait.size(); i++) {
            System.out.printf("%d\n", down_wait.get(i));
        }
        System.out.printf("\nup_wait:\n");
        for(int i = 0; i < up_wait.size(); i++) {
            System.out.printf("%d\n", up_wait.get(i));
        }

        System.out.printf("\nnext target: %d\n", getTargetFloor());
        System.out.printf("Current direction: %d\n", getCurrentDirection());
    }
	
}
