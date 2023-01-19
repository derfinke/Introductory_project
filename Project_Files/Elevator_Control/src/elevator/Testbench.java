package elevator;

import java.util.List;

import org.json.JSONObject;

public class Testbench {
	public Testbench(ElevatorLogic logic) throws Exception {
		JSONObject json = new JSONObject();
		json.put("floorSelection", 1);
		json.put("timestamp", "kurz vor knapp");
		//Main.mockEvent("", json);
	   
		logic.current_direction = ElevatorLogic.up;
    	logic.current_floor = 3;
    	logic.requested_floor = 2;
	    logic.floor_request(ElevatorLogic.up);
	    
	    List<Integer> down_requests = logic.down_requests;
	    System.out.printf("down_requests:\n");
	    for(int i = 0; i < down_requests.size(); i++) {
	    	System.out.printf("down_request: %d\n", down_requests.get(i));
	    }
	    List<Integer> up_requests = logic.up_requests;
	    System.out.printf("\nup_requests:\n");
	    for(int i = 0; i < up_requests.size(); i++) {
	    	System.out.printf("up_request: %d\n", up_requests.get(i));
	    }
	    List<Integer> down_wait = logic.down_wait;
	    System.out.printf("\ndown_wait:\n");
	    for(int i = 0; i < down_wait.size(); i++) {
	    	System.out.printf("down_wait: %d\n", down_wait.get(i));
	    }
	    List<Integer> up_wait = logic.up_wait;
	    System.out.printf("\nup_wait:\n");
	    for(int i = 0; i < up_wait.size(); i++) {
	    	System.out.printf("up_wait: %d\n", up_wait.get(i));
	    }
	}
}
