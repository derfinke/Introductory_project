package elevator;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.time.*;
public class ElevatorControl extends Thread{
	
	private ModbusClient client;
//	private boolean DoorIsOpen;
//	private boolean DoorIsClosed;
//	private boolean MotorIsReady;
//	private boolean MotorIsOn;
//    private boolean ErrorState;
    private ElevatorLogic logic;
    
    private boolean s_l1sl;
    private boolean s_l1r;
    private boolean s_l1su;
    private boolean s_l1au;
    private boolean s_l2al;
    private boolean s_l2sl;
    private boolean s_l2r;
    private boolean s_l2su;
    private boolean s_l2au;
    private boolean s_l3al;
    private boolean s_l3sl;
    private boolean s_l3r;
    private boolean s_l3su;
    private boolean s_l3au;
    private boolean s_l4al;
    private boolean s_l4sl;
    private boolean s_l4r;
    private boolean s_l4su;
    private boolean s_dopened;
    private boolean s_dclosed;
    private boolean m_ready;
    private boolean m_on;
    private boolean m_error;
    
    private int current_floor;
    private boolean arrived_floor_flag;
	private int Direction = 0;
	private int wishedFloor = 0;
	
	private ReentrantLock lock = new ReentrantLock(true);
	
	public ElevatorControl(ElevatorLogic logic) throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de",506);
		client.Connect();
		initCurrentFloor();
		this.logic = logic;
		if(current_floor == 0)
		{
			reset();
			current_floor = 1;
		}
		
	}
	
//	public void mockFloorEvent(String event, int data) throws Exception {
//		JSONObject payload = new JSONObject();
//		payload.put(event, data);
//		logic.mockEvent(event, payload);
//		
//	}
	private boolean[] readValues = new boolean[5]; 

	
	public boolean getDoorIsOpen() {
		return s_dopened;
	}

	public void setDoorIsOpen(boolean doorIsOpen) {
		s_dopened = doorIsOpen;
	}

	public boolean getDoorIsClosed() {
		return s_dclosed;
	}

	public void setDoorIsClosed(boolean doorIsClosed) {
		s_dclosed = doorIsClosed;
	}

	public boolean getErrorState() {
		return m_error;
	}

	public void setErrorState(boolean errorState) {
	}

	@Override
	public void run()
	{
    	while(true)
    	{
    		wishedFloor = logic.getTargetFloor();
    		Direction = logic.getCurrentDirection();
    		readSensor();
    		//Direction = Logic_Object.getCurrentDirection();
    		if(wishedFloor != 0)
    		{
    			setCurrentFloor(Direction);
    			try {
					ApproachStop(wishedFloor, Direction);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    		else
    		{
    			
    		}
//    		System.out.println(control.getCurrentFloor());
    	}
	}


	public void reset() 
	{
		try 
		{
			readSensor();
            client.WriteSingleRegister(0, 1);

            // Set the velocity to 0
            client.WriteSingleRegister(1, 0);
            client.WriteSingleCoil(8, false);
            client.WriteSingleCoil(9, false);
            client.WriteSingleCoil(10, false);
            client.WriteSingleCoil(11, false);

            // Reset door opening / closing
            client.WriteSingleCoil(12, false);
            client.WriteSingleCoil(13, false);

            client.WriteSingleRegister(0, 0);
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openDoor() 
	{
		try 
		{	
			if(s_dclosed && m_ready && !m_on)
			{
				lock.lock();
				client.WriteSingleCoil(12, false); //set register to close door to false
				client.WriteSingleCoil(13, true);  //set register to open door to true
				lock.unlock();
			}
			else
			{
				System.out.println("Door cant be opened at the moment");
			}
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeDoor()
	{
		try 
		{	
			
			if(s_dopened && !m_ready && !m_on)
			{
				lock.lock();
				client.WriteSingleCoil(13, false); //set register to open door to false
				client.WriteSingleCoil(12, true);  //set register to close door to true
				lock.unlock();
			}
			else
			{
				System.out.println("Door cant be closed at the moment");
			}
			lock.unlock();

		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopDoor()
	{
		try 
		{
			boolean[] doorStatus = client.ReadCoils(12, 2);
			if(doorStatus[0] == true)
			{
				lock.lock();
				client.WriteSingleCoil(12, false);  //set register to close door to true
				lock.unlock();
			}
			else if(doorStatus[1] == true)
			{
				lock.lock();
				client.WriteSingleCoil(13, false);
				lock.unlock();
			}
			else
			{
				System.out.println("Door isn't moving at the moment");
			}
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void emergencyStop()
	{

        try {
        	lock.lock();
        	//set velocity to zero
            client.WriteSingleRegister(1, 0);
			client.WriteSingleCoil(8, false);
	        client.WriteSingleCoil(9, false);
	        client.WriteSingleCoil(10, false);
	        client.WriteSingleCoil(11, false);

	        // stop door opening / closing
	        client.WriteSingleCoil(12, false);
	        client.WriteSingleCoil(13, false);
	        lock.unlock();
		} catch (ModbusException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void readSensor()
	{
        boolean[] sensorValuesFloor = new boolean[27];
        boolean[] sensorValuesDoorAndMotor = new boolean[5];
		try 
		{
			lock.lock();
			sensorValuesFloor = client.ReadDiscreteInputs(1, 27); //read values from register 10.0 - 10.4
			sensorValuesDoorAndMotor = client.ReadDiscreteInputs(80, 5);
			lock.unlock();
			
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
		lock.lock();
        s_l1sl = sensorValuesFloor[0];
        s_l1r = sensorValuesFloor[1];
        s_l1su = sensorValuesFloor[2];
        s_l1au = sensorValuesFloor[3];
        s_l2al = sensorValuesFloor[7];
        s_l2sl = sensorValuesFloor[8];
        s_l2r = sensorValuesFloor[9];
        s_l2su = sensorValuesFloor[10];
        s_l2au = sensorValuesFloor[11];
        s_l3al = sensorValuesFloor[15];
        s_l3sl = sensorValuesFloor[16];
        s_l3r = sensorValuesFloor[17];
        s_l3su = sensorValuesFloor[18];
        s_l3au = sensorValuesFloor[19];
        s_l4al = sensorValuesFloor[23];
        s_l4sl = sensorValuesFloor[24];
        s_l4r = sensorValuesFloor[25];
        s_l4su = sensorValuesFloor[26];
        s_dopened = sensorValuesDoorAndMotor[0];
        s_dclosed = sensorValuesDoorAndMotor[1];
        m_ready = sensorValuesDoorAndMotor[2];
        m_on = sensorValuesDoorAndMotor[3];
        m_error = sensorValuesDoorAndMotor[4];
        lock.unlock();
	}
	
    public void printSensorValues(){
        System.out.println("s_l1sl = " + s_l1sl +
                " | s_l1r = " + s_l1r +
                " | s_l1su = " + s_l1su +
                " | s_l1au = " + s_l1au +
                " | s_l2al = " + s_l2al +
                " | s_l2sl = " + s_l2sl +
                " | s_l2r = " + s_l2r +
                " | s_l2su = " + s_l2su +
                " | s_l2au = " + s_l2au +
                " | s_l3al = " + s_l3al +
                " | s_l3sl = " + s_l3sl +
                " | s_l3r = " + s_l3r +
                " | s_l3su = " + s_l3su +
                " | s_l3au = " + s_l3au +
                " | s_l4al = " + s_l4al +
                " | s_l4sl = " + s_l4sl +
                " | s_l4r = " + s_l4r +
                " | s_l4su = " + s_l4su +
                " | s_dopened = " + s_dopened +
                " | s_dclosed = " + s_dclosed +
                " | m_ready = " + m_ready +
                " | m_on = " + m_on +
                " | m_error = " + m_error);
    }
	
    public void motorV2Up() {
    	try {
    		lock.lock();
			client.WriteSingleCoil(11, true);
			lock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void motorV2Down() {
    	try {
    		lock.lock();
    		client.WriteSingleCoil(8, true);
    		lock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void motorV1Up() {
    	try {
    		client.WriteSingleCoil(10, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void motorV1Down() {
    	try {
    		client.WriteSingleCoil(9, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void motorV1DownStop() {
    	try {
    		client.WriteSingleCoil(9, false);
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void motorV1UpStop() {
    	try {
    		client.WriteSingleCoil(10, false);
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void motorV2UpStop() {
    	try {
    		client.WriteSingleCoil(11, false);
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void motorV2DownStop() {
    	try {
    		client.WriteSingleCoil(8, false);
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}

    }
    
    synchronized public int getCurrentFloor()
    {	
    	return current_floor;
    }
    
    private void initCurrentFloor()
    {
    	JSONObject json = new JSONObject();
    	readSensor();

    	if(s_l1r) 
    	{
    		current_floor = 1;
    	}
    	else if(s_l2r)
    	{
    		current_floor = 2;
    	}
    	else if(s_l3r)
    	{
    		current_floor = 3;
    	}
    	else if(s_l4r)
    	{
    		current_floor = 4;
    	}
    	else 
    	{
    		current_floor = 0;
    	}
    }
    
    public void setCurrentFloor(int Direction)
    {
    	lock.lock();
    	if(Direction == 1)
    	{
    		if(s_l2al){
    			current_floor = 2;
    		}
    		else if(s_l3al)
    		{
    			current_floor = 3;
    		}
    		else if(s_l4al)
    		{
    			current_floor = 4;
    		}
    	}
    	else
    	{
    		if(s_l3au)
    		{
    			current_floor = 3;
    		}
    		else if(s_l2au)
    		{
    			current_floor = 2;
    		}
    		else if(s_l1au)
    		{
    			current_floor = 1;
    		}
    	}
    	lock.unlock();
    }
    
    public void ApproachStop(int stop, int Direction) throws Exception
    {
    	//long time_ms = 0;
    	//StopWatch myStopWatch = new StopWatch();
		if(Direction == 1)
		{
	    	switch(stop)
	    	{
	    	case 2:
	    		lock.lock();
	    		if(current_floor == 2)
	    		{
	    			if(s_l2al)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2UpStop();
	    				client.WriteSingleRegister(1, 4);
	    			}
	    			else if(s_l2sl)
    				{
    					client.WriteSingleRegister(1, 1);	
    				}
    				else if(s_l2r && !arrived_floor_flag)
    				{	
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}
	    		}
	    		lock.unlock();
	    		break;
	    	case 3:
	    		lock.lock();
	    		if(current_floor == 3)
	    		{
	    			if(s_l3al)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2UpStop();
	    				client.WriteSingleRegister(1, 4);
	    			}
	    			else if(s_l3sl)
    				{
    					client.WriteSingleRegister(1, 1);
    				}
    				else if(s_l3r && !arrived_floor_flag)
    				{
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}   				
	    		}
	    		lock.unlock();
	    		break;
	    	case 4:
	    		lock.lock();
	    		if(current_floor == 4)
	    		{
	    			if(s_l4al)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2UpStop();
	    				client.WriteSingleRegister(1, 4);
	    			}
	    			else if(s_l4sl)
    				{
    					client.WriteSingleRegister(1, 1);
    				}
    				else if(s_l4r && !arrived_floor_flag)
    				{
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}	
	    		}
	    		lock.unlock();
	    		break;
	    	}
		}
		else
		{
	    	switch(stop)
	    	{
	    	case 1:
	    		lock.lock();
	    		if(current_floor == 1)
	    		{
	    			if(s_l1au)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2DownStop();
	    				client.WriteSingleRegister(1, -4);
	    			}
	    			else if(s_l1su)
    				{
    					client.WriteSingleRegister(1, -1);
    				}
    				else if(s_l1r && !arrived_floor_flag)
    				{
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}
	    		}
	    		lock.unlock();
	    		break;
	    	case 2:
	    		lock.lock();
	    		if(current_floor == 2)
	    		{
	    			if(s_l2au)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2DownStop();
	    				client.WriteSingleRegister(1, -4);
	    			}
	    			else if(s_l2su)
    				{
    					client.WriteSingleRegister(1, -1);
    				}
    				else if(s_l2r && !arrived_floor_flag)
    				{
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}
	    		}
	    		lock.unlock();
	    		break;
	    	case 3:
	    		lock.lock();
	    		if(current_floor == 3)
	    		{
	    			if(s_l3au)
	    			{
	    				arrived_floor_flag = false;
	    				motorV2DownStop();
	    				client.WriteSingleRegister(1, -4);
	    			}
	    			else if(s_l3su)
    				{
    					client.WriteSingleRegister(1, -1);
    				}
    				else if(s_l3r && !arrived_floor_flag)
    				{
    					client.WriteSingleRegister(1, 0);
    					logic.FloorEventHandler("floorArrived", 0);
    					arrived_floor_flag = true;
    				}
	    		}
	    		lock.unlock();
	    		break;
	    	} 
		}
     }
}
