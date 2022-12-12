package intro;

//codeexample
import java.io.IOException;
import java.net.UnknownHostException;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class ElevatorTest {

	private int[] readRegisters = new int[10];
	private ModbusClient client;

	public ElevatorTest() throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de",509);
		client.Connect();
	}

	public void run() {
		while (true) {
			try {
				readRegisters = client.ReadInputRegisters(0, 10);
			} catch (ModbusException | IOException e) {
				e.printStackTrace();
				break;
			}
			System.out.println(readRegisters[2]);
		}
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

}