package elevator;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;

public class ServerThread extends Thread{
    private final int port;
    private Socket client;
    private ElevatorControl control;

    public ServerThread(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Waiting for the client request");
            client = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("got connection");
        //noinspection InfiniteLoopStatement
        while(true){
            ObjectInputStream ois;
            String message = null;
            try {
                ois = new ObjectInputStream(client.getInputStream());
                //convert ObjectInputStream object to String
                message = (String) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                receiveMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data, boolean value) throws IOException, JSONException {
        try {
			if (!(data.equals("isOpen") || data.equals("isClosed") || data.equals("status"))){
			    throw new InvalidParameterException();
			}
			JSONObject json = new JSONObject();
			json.put("data", data);
			json.put("value", value);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			oos.writeObject(json.toString());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		} 
    }

    private void receiveMessage(String message) throws JSONException {
        try {
			JSONObject json = new JSONObject(message);
			String data = json.getString("data");
			boolean value = json.getBoolean("value");
			switch (data) {
			    case "reset":
			    	control.reset();
			        System.out.println("reset: " + value);
			        break;
			    case "open":
			    	control.openDoor();
			    	System.out.println("open door: " + value);
			    	while(!control.getDoorIsOpen())
			    	{
			    		control.readSensor();
			    	}
			    	send("isOpen", true);
			        break;
			    case "close":
			    	control.closeDoor();
			    	System.out.println("close door: " + value);
			    	while(!control.getDoorIsClosed())
			    	{
			    		control.readSensor();
			    	}
			    	send("isClosed", true);
			        break;
			    case "status":
			    	control.readSensor();
			    	send("status", control.getDoorIsClosed());
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		} 
    }
    
    public void initElevatorControl(ElevatorControl control)
    {
    	this.control = control;
    }
}
