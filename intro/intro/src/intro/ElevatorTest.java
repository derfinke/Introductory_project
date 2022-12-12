package intro;

//codeexample
import java.io.IOException;
import java.net.UnknownHostException;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class ElevatorTest {

	private boolean[] readRegisters = new boolean[10];
	private ModbusClient client;

	public ElevatorTest() throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de",505);
		client.Connect();
	}

	public void run() {
		reset();
	}

	public static void main(String[] args) {
		ElevatorTest runner;
		try {
			runner = new ElevatorTest();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		runner.run();
	}
	
	public void reset() {
		try {
			if(!client.ReadCoils(0, 1)[0])
			{
				client.WriteSingleCoil(0, true);
				Thread.sleep(400);
				client.WriteSingleCoil(0, false);
			}
			else
			{
				client.WriteSingleCoil(0, false);
			}
		} catch (ModbusException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}