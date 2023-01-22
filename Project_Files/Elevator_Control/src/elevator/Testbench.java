package elevator;

import java.util.List;

import org.json.JSONObject;

public class Testbench{
	private ElevatorLogic logic;
	
//	public void mockFloorEvent(String event, int data) throws Exception {
//		JSONObject payload = new JSONObject();
//		payload.put(event, data);
//		logic.mockEvent(event, payload);
//		
//	}
	
	
	public Testbench(ElevatorLogic logic) throws Exception {
		this.logic = logic;
		//JSONObject json = new JSONObject();
//		json.put("floorArrived", "");
//		json.put("timestamp", "kurz vor knapp");
		//logic.mockEvent("", json);
	   
	
    	logic.current_floor = 1;
    	mockFloorEvent("stopButtonDown", 4);
    	mockFloorEvent("stopButtonDown", 2);
    	logic.printElevatorInfo(logic.current_floor);
 
    	
    	
    	logic.current_floor = 2;
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 3;
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 4;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	
    	
    	
    	mockFloorEvent("floorSelection", 3);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	
    	logic.current_floor = 3;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	
    	mockFloorEvent("stopButtonDown", 4);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	
    	logic.current_floor = 2;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 3;
    	
    	logic.current_floor = 4;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	mockFloorEvent("floorSelection", 2);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 2;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	mockFloorEvent("stopButtonUp", 1);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 1;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	mockFloorEvent("floorSelection", 2);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	logic.current_floor = 2;
    	mockFloorEvent("floorArrived", 0);
    	logic.printElevatorInfo(logic.current_floor);
    	
    	
    
	}
}


