package elevator;

import java.util.List;

import org.json.JSONObject;

public class Testbench extends Thread{
	private ElevatorLogic logic;
	
	public void mockFloorEvent(String event, int data) throws Exception {
		JSONObject payload = new JSONObject();
		payload.put(event, data);
		logic.mockEvent(event, payload);
		
	}
	
	
	public void printElevatorInfo(int printNumber) {
		for(int i=0;i<15;i++) {
			System.out.printf("__");
		}
		System.out.printf("\nTESTOUTPUT: " + printNumber + "\n");
		for(int i=0;i<15;i++) {
			System.out.printf("__");
		}
		System.out.printf("\n");
		
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
	    
	    System.out.printf("next target: %d\n", logic.getTargetFloor());
	    System.out.printf("direction: %d\n", logic.getCurrentDirection());
	}
	
	
	
	
	public Testbench(ElevatorLogic logic) throws Exception {
		this.logic = logic;
	}
	
	@Override 
	public void run() {
//		JSONObject json = new JSONObject();
//		json.put("floorArrived", "");
//		json.put("timestamp", "kurz vor knapp");
		//logic.mockEvent("", json);
	   
		//1 to 4 upwards with halt in 3(up) request in 2(down)
//    	logic.setCurrentFloor(1);
//    	logic.current_direction = logic.up;
    	
    	try {
    		
    		
			
	    	
//		    logic.floor_request(ElevatorLogic.up, 2);
//		    logic.floor_request(ElevatorLogic.up, 3);
//		    logic.current_floor = 3;
//		    logic.floor_request(ElevatorLogic.up, 4);
    		mockFloorEvent("stopButtonUp", 2);
    		mockFloorEvent("stopButtonUp", 3);
    		mockFloorEvent("stopButtonUp", 4);
    		printElevatorInfo(1);
		    /*while(true) {
    			if (current_floor != logic.getCurrentFloor()) {
    				current_floor = logic.getCurrentFloor();
    				printElevatorInfo(current_floor);
    				switch(current_floor) {
	    			case 1:
	    				
	    				break;
	    			
				    case 2:
						mockFloorEvent("floorSelection", 3);
						break;
						
				    case 4:
				    	mockFloorEvent("floorSelection", 1);
				    	break;
    			}
    			}
    			
    		}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
}
