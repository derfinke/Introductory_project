package elevator;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidParameterException;

public class ClientThread extends Thread{
    private Socket server;
    private final int port;
    private GUI gui;

    public ClientThread(int port, GUI gui){
        this.port = port;
        this.gui = gui;
    }

    public void run() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            server = new Socket(host.getHostName(), port);
            System.out.println("connected");
            send("status", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            //read from socket to ObjectInputStream object
            ObjectInputStream ois;
            String message = null;
            try {
                ois = new ObjectInputStream(server.getInputStream());
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

    public void send(String data, boolean value) {
    
        try {
			if (!(data.equals("reset") || data.equals("open") || data.equals("close") || (data.equals("status")))){
			    throw new InvalidParameterException();
			}
			JSONObject json = new JSONObject();
			json.put("data", data);
			json.put("value", value);
			ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
			oos.writeObject(json.toString());
			System.out.println(json.toString());
			
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
			    case "isOpen":
			    	gui.setSa_Door_is_CLOSED_FLAG(false);
			    	gui.setSa_Door_is_OPEN_FLAG(true);
			    	gui.repaint();
			        System.out.println("is open: " + value);
			        break;
			    case "isClosed":
			    	gui.setSa_Door_is_OPEN_FLAG(false);
			    	gui.setSa_Door_is_CLOSED_FLAG(true);
			    	gui.repaint();
			    	System.out.println("is closed: " + value);
			        break;
			    case "status":
			    	if(value)
			    	{
				    	gui.setSa_Door_is_OPEN_FLAG(false);
				    	gui.setSa_Door_is_CLOSED_FLAG(true);
				    	gui.repaint();
			    	}
			    	else
			    	{
			    		gui.setSa_Door_is_OPEN_FLAG(true);
				    	gui.setSa_Door_is_CLOSED_FLAG(false);
				    	gui.repaint();
			    	}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
}
