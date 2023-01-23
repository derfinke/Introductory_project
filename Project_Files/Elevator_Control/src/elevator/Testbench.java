package elevator;

public class Testbench{
	public Testbench(ElevatorLogic logic) throws Exception {
    	logic.current_floor = 3;
    	logic.FloorEventHandler("stopButtonDown", 3);
    	logic.FloorEventHandler("stopButtonUp", 3);
 
    	/*
    	
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
    	*/
    	
    	
    
	}
}


