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
	private int current_floor;
	private int next_target_floor;
	private boolean first_request = false;
	private int init_direction;
	private int first_floor_request;

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
					floor_request(getCurrentDirection(), json.getInt("floorSelection"));
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
		System.out.println("wanted direction:" + direction);
		setCurrentFloor(control.getCurrentFloor());
		if (getCurrentDirection() == none) { // init_request
			if (direction != none) { // from outside
				init_direction = direction; // direction after init_floor is reached
				first_floor_request = target_floor; // save target_floor
				first_request = true; //flag to block new requests from changing the next_target_floor until the init_floor is reached
			}
			if (target_floor - current_floor > 0) {
				direction = up;
			}
			else if (target_floor - current_floor < 0) {
				direction = down;
			}
			setCurrentDirection(direction);
		}
		if (direction != getCurrentDirection()) { // requested direction is unequal to current_direction
			if (getCurrentDirection() == up) {
				add_request(down_requests, target_floor);
			}
			else if (getCurrentDirection() == down){
				add_request(up_requests, target_floor);
			}
		}
		else if (direction * (target_floor - current_floor) > 0){ //
			if (getCurrentDirection() == up) {
				add_request(up_requests, target_floor);
			}
			else if (getCurrentDirection() == down){
				add_request(down_requests, target_floor);
			}
		}
		else {
			if (getCurrentDirection() == up) {
				add_request(up_wait, target_floor);
			}
			else if (getCurrentDirection() == down){
				add_request(down_wait, target_floor);
			}
		}
		update_next_target_floor();
		printElevatorInfo(current_floor);
	}


	
	private void delete_complied_requests() {
		if (getCurrentDirection() == up) {
			up_requests.removeIf(floor -> floor.equals(current_floor));
		}
		else if (getCurrentDirection() == down) {
			down_requests.removeIf(floor -> floor.equals(current_floor));
		}
	}
	
	private boolean update_current_direction() {
		int last_direction = getCurrentDirection();
		if (first_request) {
			setCurrentDirection(init_direction);
			first_request = false;
			return last_direction != getCurrentDirection();
		}
		if (down_requests.isEmpty() && up_requests.isEmpty()) {
			setCurrentDirection(none);
		}
		if (getCurrentDirection() == down) {
			if (current_floor == 1) { // if elevator reached end of direction
				setCurrentDirection(up);
			}
			if (down_requests.isEmpty() && !up_requests.isEmpty()) {
				if (Collections.min(up_requests) > current_floor) { // if no more down_requests and no reachable up_request
					setCurrentDirection(up);
				}
			}
		}
		else if (getCurrentDirection() == up) {
			if (current_floor == 4) { // if elevator reached end of direction
				setCurrentDirection(down);
			}
			if (up_requests.isEmpty() && !down_requests.isEmpty()) {
				if (Collections.max(down_requests) < current_floor) { // if no more up_requests and no reachable down_request
					setCurrentDirection(down);
				}
			}
		}
		return last_direction != getCurrentDirection();
	}
	
	private void forward_wait_lists() {
		if (getCurrentDirection() == up) {
			System.out.println("Forward wait list");
			down_requests.addAll(down_wait);
			down_wait.clear();
		}
		else if(getCurrentDirection() == down) {
			up_requests.addAll(up_wait);
			up_wait.clear();
		}
	}
	
	private void update_next_target_floor() {
		if(first_request) { //!init
			//TODO: implement pick up
			System.out.println("first_request == true");
			next_target_floor = first_floor_request;
			if(current_direction == 1)
			{
				control.motorV2Up();
			}
			else if(current_direction == -1)
			{
				control.motorV2Down();
			}
			return;
		}
		if (getCurrentDirection() == up) {
			if(!up_requests.isEmpty()) {
				next_target_floor = Collections.min(up_requests); //choose lowest request, that is still above current floor
				System.out.println("In next Target Floor Up");
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
				System.out.println("In next Target Floor Up aber erst down");
				control.motorV2Up();
			}
		}
		else if(getCurrentDirection() == down) {
			if(!down_requests.isEmpty()) {
				next_target_floor = Collections.max(down_requests); //choose highest request, that is still below current floor
				System.out.println("In next Target Floor Down");
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
				System.out.println("In next Target Floor Down aber up");
				control.motorV2Down();
			}
		}
	}
	
	public void floor_arrived() {
		//open / close door
		if (first_request) {
			System.out.println("floor_arrived: first_request = true");
			first_request = false;
		}
		setCurrentFloor(control.getCurrentFloor());
		delete_complied_requests();
		if(update_current_direction()) {
			forward_wait_lists();
		};
		System.out.println("floor_arrived: before update");
		update_next_target_floor();
		printElevatorInfo(current_floor);
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
