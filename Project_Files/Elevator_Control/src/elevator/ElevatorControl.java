package elevator;
import java.io.IOException;
import java.net.UnknownHostException;
import org.json.JSONObject;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import mqtt.MQTT_Client;
import java.util.concurrent.locks.*;

public class ElevatorControl extends Thread {

	private ModbusClient client;
	private ElevatorLogic logic;
	private MQTT_Client publisher;
	private ReentrantLock lock = new ReentrantLock(true);
	
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
	private boolean arrived_floor_flag;
	private boolean elevator_is_moving;
	private boolean request_door_state;
	private int previous_floor;
	private int current_floor;
	private int Direction = 0;
	private int wishedFloor = -1;
	JSONObject jsonObject = new JSONObject();

	public ElevatorControl(ElevatorLogic logic) throws UnknownHostException, IOException {
		client = new ModbusClient("ea-pc111.ei.htwg-konstanz.de", 506);
		client.Connect();
		this.logic = logic;
	}

	public void passMqtt(MQTT_Client publisher) {
		this.publisher = publisher;
	}

	//main thread checks for elevator status and sends door status to HMI
	@Override
	public void run() {
		request_door_state = true;
		initCurrentFloor();
		elevator_is_moving = false;

		
		logic.setCurrentFloor(current_floor);
		while (true) {
			wishedFloor = logic.getTargetFloor();
			Direction = logic.getCurrentDirection();
			readSensor();
			if (wishedFloor != -1) {
				previous_floor = current_floor;
				setCurrentFloor(Direction);
				try {
					ApproachStop(wishedFloor, Direction);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (current_floor > previous_floor || current_floor < previous_floor) {
						logic.setCurrentFloor(current_floor);
						publisher.publish_Int("currentFloor", current_floor, jsonObject);
					}
				if (wishedFloor > current_floor && !elevator_is_moving) {
					motorV2Up();
					elevator_is_moving = true;
				} else if (wishedFloor < current_floor && !elevator_is_moving) {
					motorV2Down();
					elevator_is_moving = true;
				}
			}
			if (request_door_state) {
				readSensor();
				lock.lock();
				if (s_dopened && !s_dclosed) {
					publisher.publish_String("doorStatus", "open", jsonObject);
				} else if (s_dclosed && !s_dopened) {
					publisher.publish_String("doorStatus", "closed", jsonObject);
				}
				request_door_state = false;
				lock.unlock();
			}
		}
	}

	//resets elevator error state
	public void reset() {
		lock.lock();
		try {
			client.WriteSingleRegister(0, 0);
			publisher.publish_String("errorState", "OK", jsonObject);
		} catch (ModbusException | IOException  e) {
			lock.unlock();
			e.printStackTrace();
		}
		lock.unlock();
		elevator_is_moving = false;
	}

	//resets elevator to floor one
	public void hard_reset() {
		try {
			readSensor();
			lock.lock();
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
			lock.unlock();

		} catch (ModbusException | IOException e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	public void openDoor() {
		try {
			readSensor();
			if (!m_on) {
				lock.lock();
				client.WriteSingleCoil(12, false); // set register to close door to false
				client.WriteSingleCoil(13, true); // set register to open door to true
				publisher.publish_String("doorStatus", "moving", jsonObject);
				request_door_state = true;
				lock.unlock();
			} else {
				lock.unlock();
				System.out.println("Door cant be opened at the moment");
			}
		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}

	public void closeDoor() {
		try {
			readSensor();
			if (!m_ready && !m_on) {
				lock.lock();
				client.WriteSingleCoil(13, false); // set register to open door to false
				client.WriteSingleCoil(12, true); // set register to close door to true
				publisher.publish_String("doorStatus", "moving", jsonObject);
				request_door_state = true;
				lock.unlock();
			} else {
				lock.unlock();
				System.out.println("Door cant be closed at the moment");
			}

		} catch (ModbusException | IOException e) {
			e.printStackTrace();
		}
	}

	public void stopDoor() {
		try {
			lock.lock();
			boolean[] doorStatus = client.ReadCoils(12, 2);
			lock.unlock();
			if (doorStatus[0] == true) {
				lock.lock();
				client.WriteSingleCoil(12, false); // set register to close door to true
				lock.unlock();
			} else if (doorStatus[1] == true) {
				lock.lock();
				client.WriteSingleCoil(13, false);
				lock.unlock();
			} else {
				System.out.println("Door isn't moving at the moment");
			}
		} catch (ModbusException | IOException e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	//stops elevator at any point
	public void emergencyStop(boolean state) {

		try {
			if (state) {
				lock.lock();
				// set velocity to zero
				client.WriteSingleRegister(1, 0);
				client.WriteSingleCoil(8, false);
				client.WriteSingleCoil(9, false);
				client.WriteSingleCoil(10, false);
				client.WriteSingleCoil(11, false);

				// stop door opening / closing
				client.WriteSingleCoil(12, false);
				client.WriteSingleCoil(13, false);
				publisher.publish_String("errorState", "error", jsonObject);
				lock.unlock();
			} else {
				reset();
			}
		} catch (ModbusException | IOException e) {
			lock.unlock();
			e.printStackTrace();
		}

	}
	
	//read out all elevator sensors
	public void readSensor() {
		boolean[] sensorValuesFloor = new boolean[27];
		boolean[] sensorValuesDoorAndMotor = new boolean[5];
		try {
			lock.lock();
			sensorValuesFloor = client.ReadDiscreteInputs(1, 27); // read values from register 10.0 - 10.4
			sensorValuesDoorAndMotor = client.ReadDiscreteInputs(80, 5);
			lock.unlock();

		} catch (ModbusException | IOException e) {
			lock.unlock();
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

	public void motorV2Up() {
		try {
			lock.lock();
			client.WriteSingleCoil(11, true);
			lock.unlock();
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	public void motorV2Down() {
		try {
			lock.lock();
			client.WriteSingleCoil(8, true);
			lock.unlock();
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	public void motorV2UpStop() {
		try {
			client.WriteSingleCoil(11, false);
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}
	}

	public void motorV2DownStop() {
		try {
			client.WriteSingleCoil(8, false);
		} catch (Exception e) {
			lock.unlock();
			e.printStackTrace();
		}

	}
	
	//sets current floor and sends it to HMI
	private void initCurrentFloor() {
		readSensor();

		if (s_l1r) {
			current_floor = 0;
		} else if (s_l2r) {
			current_floor = 1;
		} else if (s_l3r) {
			current_floor = 2;
		} else if (s_l4r) {
			current_floor = 3;
		}
		publisher.publish_Int("currentFloor", current_floor, jsonObject);
	}
	
	//sets current floor based on HMI
	public void setCurrentFloor(int Direction) {
		lock.lock();
		if (Direction == 1) {
			if (s_l2al) {
				current_floor = 1;
			} else if (s_l3al) {
				current_floor = 2;
			} else if (s_l4al) {
				current_floor = 3;
			}
		} else if (Direction == -1) {
			if (s_l3au) {
				current_floor = 2;
			} else if (s_l2au) {
				current_floor = 1;
			} else if (s_l1au) {
				current_floor = 0;
			}
		}
		lock.unlock();
	}

	//called after elevator has a target floor slows down elevator after passing uppermost or lowermost sensor
	public void ApproachStop(int stop, int Direction) throws Exception {
		if (Direction == 1) {
			switch (stop) {
				case 1:
					lock.lock();
					if (current_floor == 1) {
						if (s_l2al) {
							arrived_floor_flag = false;
							motorV2UpStop();
							client.WriteSingleRegister(1, 4);
						} else if (s_l2sl) {
							client.WriteSingleRegister(1, 1);
						} else if (s_l2r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				case 2:
					lock.lock();
					if (current_floor == 2) {
						if (s_l3al) {
							arrived_floor_flag = false;
							motorV2UpStop();
							client.WriteSingleRegister(1, 4);
						} else if (s_l3sl) {
							client.WriteSingleRegister(1, 1);
						} else if (s_l3r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				case 3:
					lock.lock();
					if (current_floor == 3) {
						if (s_l4al) {
							arrived_floor_flag = false;
							motorV2UpStop();
							client.WriteSingleRegister(1, 4);
						} else if (s_l4sl) {
							client.WriteSingleRegister(1, 1);
						} else if (s_l4r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				}
			} 
		else {
			switch (stop) {
				case 0:
					lock.lock();
					if (current_floor == 0) {
						if (s_l1au) {
							arrived_floor_flag = false;
							motorV2DownStop();
							client.WriteSingleRegister(1, -4);
						} else if (s_l1su) {
							client.WriteSingleRegister(1, -1);
						} else if (s_l1r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				case 1:
					lock.lock();
					if (current_floor == 1) {
						if (s_l2au) {
							arrived_floor_flag = false;
							motorV2DownStop();
							client.WriteSingleRegister(1, -4);
						} else if (s_l2su) {
							client.WriteSingleRegister(1, -1);
						} else if (s_l2r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				case 2:
					lock.lock();
					if (current_floor == 2) {
						if (s_l3au) {
							arrived_floor_flag = false;
							motorV2DownStop();
							client.WriteSingleRegister(1, -4);
						} else if (s_l3su) {
							client.WriteSingleRegister(1, -1);
						} else if (s_l3r && !arrived_floor_flag) {
							client.WriteSingleRegister(1, 0);
							logic.FloorEventHandler("floorArrived", 0);
							arrived_floor_flag = true;
							elevator_is_moving = false;
							request_door_state = true;
						}
					}
					lock.unlock();
					break;
				}
			}
		}
	}
