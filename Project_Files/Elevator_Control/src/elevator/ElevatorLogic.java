package elevator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang3.time.*;


public class ElevatorLogic extends Thread{
    private static final int down = -1, up = 1, none=0;
    private static final int request = 0, arrived = 1;
	private int current_direction = none;
	public int current_floor;
	private int next_target_floor = -1;
	private boolean wait_first_floor_arrived = false;
	private boolean first_request = false;
	private int init_direction;
	private int first_floor_request;
	private long time_in_ms;
	
	private boolean isStopwatchRunning = false;

	private ElevatorControl control;
	
	List<Integer> up_requests = new ArrayList<>();
	List<Integer> down_requests = new ArrayList<>();
	List<Integer> up_wait = new ArrayList<>();
	List<Integer> down_wait = new ArrayList<>();
	
	public void FloorEventHandler(String key, int floor){
		switch(key) {
			case "stopButtonDown":
				floor_request(down, floor, key);
				break;
			case "stopButtonUp":
				floor_request(up, floor, key);
				System.out.println("in StopButtonUp Event");
				break;
			case "floorSelection":
				floor_request(none, floor, key);
				break;
			case "floorArrived":
				floor_arrived();
				break;
		}
	}
	
	public void DoorEventHandler(String doorCommand){
		switch(doorCommand)
		{
			case "open":
				control.openDoor();
				break;
			case "close":
				control.closeDoor();
				break;
			case "stop":
				control.stopDoor();
				break;
		}
	}
	
	private void add_request(List<Integer> list, int floor) {
		if (!list.contains(floor)) list.add(floor);
	}
	
	@Override
	public void run()
	{
		StopWatch watch = new StopWatch();
		while(true)
		{
			if(isStopwatchRunning) 
			{
				watch.start();
				time_in_ms = watch.getTime();
				
				while(time_in_ms <= 6000)
				{
					time_in_ms = watch.getTime();
				}
				watch.reset();
				control.closeDoor();
				isStopwatchRunning = false;
			}
			else {
				try {
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void floor_request(int requested_direction, int target_floor, String key) {
		
		// first request when all lists have been empty -> current_direction = not set (none)
		if (current_direction == none){ 
			if (requested_direction != none) { 			// from outside
				init_direction = requested_direction; 	// direction after first request has been reached
				first_floor_request = target_floor; 	// save target_floor
				wait_first_floor_arrived = true; 		// flag to block new requests from changing the next_target_floor until the init_floor is reached
				first_request = true;
			}
			// request from inside the elevator cabin
			else {
				first_request = true;
			}
		} 
		// set direction if current_direction is not set yet
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
		// requested direction is unequal to current_direction
		if (requested_direction != current_direction) { 
			if (current_direction == up) {
				add_request(down_requests, target_floor);
			}
			else if (current_direction == down){
				add_request(up_requests, target_floor);
			}
		}
		// if in correct direction and on the way
		else if (requested_direction * (target_floor - current_floor) > 0){ 
			if (current_direction == up) {
				add_request(up_requests, target_floor);
			}
			else if (current_direction == down){
				add_request(down_requests, target_floor);
			}
		}
		// if right direction but not on the current way
		else if(!first_request) { 
			if (current_direction == up) {
				add_request(up_wait, target_floor); 
			}
			else if (current_direction == down){ 		
				add_request(down_wait, target_floor); 
			}
		}
		
		update_next_target_floor(request);
		
		if (first_request) {
			first_request = false;
		}
		
		printElevatorInfo(key);
	}


	// remove requests for the current floor if current direction corresponds to the requested direction 
	private boolean delete_complied_requests(boolean up_for_down_request, boolean down_for_up_request) {
		boolean request_deleted = false;
		if (current_direction == up || down_for_up_request) {
			request_deleted = up_requests.removeIf(floor -> floor.equals(current_floor));
		}
		if (current_direction == down || up_for_down_request) {
			request_deleted = down_requests.removeIf(floor -> floor.equals(current_floor));
		}
		return request_deleted;
	}
	
	private boolean update_current_direction() {
		int last_direction = current_direction;
		if (wait_first_floor_arrived && current_floor == first_floor_request) {
			current_direction = init_direction;
			//wait_first_floor_arrived = false;
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
		if (current_direction == down) {
			if (current_floor == 0) { // if elevator reached end of direction
				current_direction = up;
			}
			if (down_requests.isEmpty() && !up_requests.isEmpty()) {
				if (Collections.min(up_requests) > current_floor) { // if no more down_requests and no reachable up_request
					current_direction = up;
				}
				else {
					if(delete_complied_requests(false, true)) { // down for up_request
						update_current_direction(); 
					}
				}
			}
		}
		else if (current_direction == up) {
			if (current_floor == 3) { // if elevator reached end of direction
				current_direction = down;
			}
			if (up_requests.isEmpty() && !down_requests.isEmpty()) {
				if (Collections.max(down_requests) < current_floor) { // if no more up_requests and no reachable down_request
					current_direction = down;
				}
				else {
					if (delete_complied_requests(true, false)) { // up for down_request
						update_current_direction();
					}
					
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
	
	private void update_next_target_floor(int origin) {		
		int targetBuffer = current_floor;
		if (current_direction == up) {
			if(!up_requests.isEmpty()) {
				targetBuffer = Collections.min(up_requests); //choose lowest request, that is still above current floor
				System.out.println("In next Target Floor Up");
			}
			else if(!down_requests.isEmpty()){ //if no more up_requests check for highest down_request target, that is still above current floor
				int max_down_request = Collections.max(down_requests);
				if(max_down_request > current_floor) {
					targetBuffer = max_down_request;
				}
			}
		}
		else if(current_direction == down) {
			if(!down_requests.isEmpty()) {
				targetBuffer = Collections.max(down_requests); //choose highest request, that is still below current floor
				System.out.println("In next Target Floor Down");
			}
			else if(!up_requests.isEmpty()){ //if no more down_requests check for lowest up_request target, that is still below current floor
				int min_up_request = Collections.min(up_requests);
				if(min_up_request < current_floor) {
					
					targetBuffer = min_up_request;
				}
			}
		}
		
		// Check if all Lists are empty, then set target floor to current floor -> no movement
		if (up_requests.isEmpty() && down_requests.isEmpty() && up_wait.isEmpty() && down_wait.isEmpty())
			targetBuffer = current_floor;
		
		if(!wait_first_floor_arrived || origin == request) {
			next_target_floor = targetBuffer;
		}
		else{
			if (origin == arrived && first_floor_request == current_floor) {
				if (init_direction * (targetBuffer - current_floor) > 0) {
					next_target_floor = targetBuffer;
				}
				else {
					next_target_floor = current_floor;
				}
				wait_first_floor_arrived = false;
			}
			else {
				next_target_floor = targetBuffer;
			}
		}
	}
	
	public void floor_arrived() {
		control.openDoor();	
		isStopwatchRunning = true;
		delete_complied_requests(false, false);
		if(update_current_direction()) {
			forward_wait_lists(current_direction);
		};
		System.out.println("floor_arrived: before update");
		update_next_target_floor(arrived);
		System.out.println("in floorArrived Event");
		
		printElevatorInfo("floorArrived"); 
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
	
	public void initControl(ElevatorControl control)
	{
		this.control = control;
	}
	
	public void printElevatorInfo(String event) {
        System.out.println();
        printBar();
        System.out.println();
        System.out.printf("TESTOUTPUT: (Event: " + event + ")\n");
        printBar();
        System.out.println();
        System.out.printf("down_requests:\t");
        printArray(down_requests);
        System.out.printf("up_requests:\t");
        printArray(up_requests);
        System.out.printf("down_wait:\t");
        printArray(down_wait);
        System.out.printf("up_wait:\t");
        printArray(up_wait);
        System.out.printf("\nNext target: %d\t", next_target_floor);
        System.out.printf("\nCurrent floor: %d\t", current_floor);
        System.out.printf("\nCurrent direction: %d\t\n", current_direction);
    }

    private void printBar() {
        for(int i=0; i<17; i++) {
            System.out.printf("__");
        }
    }

    private void printArray(List<Integer> list) {
        for(int i = 0; i < list.size(); i++) {
            System.out.printf("%d\t", list.get(i));
        }
        System.out.println();
    }
}