package elevator;

import java.io.IOException;
import java.net.UnknownHostException;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class ElevatorControl {
	
	private ModbusClient client;
	private boolean DoorIsOpen;
	private boolean DoorIsClosed;
	private boolean MotorIsReady;
	private boolean MotorIsOn;
    private boolean ErrorState;
	
	private boolean[] readValues = new boolean[5]; 

	
	public boolean getDoorIsOpen() {
		return DoorIsOpen;
	}

	public void setDoorIsOpen(boolean doorIsOpen) {
		DoorIsOpen = doorIsOpen;
	}

	public boolean getDoorIsClosed() {
		return DoorIsClosed;
	}

	public void setDoorIsClosed(boolean doorIsClosed) {
		DoorIsClosed = doorIsClosed;
	}

	public boolean getErrorState() {
		return ErrorState;
	}

	public void setErrorState(boolean errorState) {
		ErrorState = errorState;
	}

	public ElevatorControl() throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de",505);
		client.Connect();
		reset();
		
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
			readSensor();
			
			if(DoorIsClosed && MotorIsReady && !MotorIsOn)
			{
				client.WriteSingleCoil(12, false); //set register to close door to false
				client.WriteSingleCoil(13, true);  //set register to open door to true
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
			readSensor();
			
			if(DoorIsOpen && !MotorIsReady && !MotorIsOn)
			{
				client.WriteSingleCoil(13, false); //set register to open door to false
				client.WriteSingleCoil(12, true);  //set register to close door to true
			}
			else
			{
				System.out.println("Door cant be closed at the moment");
			}

		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSensor()
	{
		try 
		{
			readValues = client.ReadDiscreteInputs(80, 5); //read values from register 10.0 - 10.4
			
			DoorIsOpen = readValues[0];
			DoorIsClosed = readValues[1];
			
			MotorIsReady = readValues[2];
			MotorIsOn = readValues[3];
			
			ErrorState = readValues[4];
			
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
		
	}
}
