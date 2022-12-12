package intro;

import java.io.IOException;
import java.net.UnknownHostException;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class ElevatorControl {
	
	private ModbusClient client;

	public ElevatorControl() throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de",505);
		client.Connect();
	}

	public void run() {

		openDoor();
	}

	public static void main(String[] args) {
		ElevatorControl runner;
		try {
			runner = new ElevatorControl();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		runner.run();
	}
	
	public void reset() 
	{
		try {
			if(!client.ReadCoils(0, 1)[0])
			{
				client.WriteSingleCoil(0, true);
				Thread.sleep(500);
				client.WriteSingleCoil(0, false);
				Thread.sleep(200);
			}
			else
			{
				client.WriteSingleCoil(0, false);
			}
		} catch (ModbusException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void openDoor() 
	{
		try {
			
			client.WriteSingleCoil(12, false);
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}
}
