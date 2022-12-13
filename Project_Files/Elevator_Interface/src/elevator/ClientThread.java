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
    //todo: private GUI gui;

    public ClientThread(int port/*todo: , GUI gui*/){
        this.port = port;
        //todo: this.gui = gui
    }

    public void run() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            server = new Socket(host.getHostName(), port);
            System.out.println("connected");
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
                parseMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data, boolean value) throws IOException, JSONException {
        if (!(data.equals("reset") || data.equals("open") || data.equals("close"))){
            throw new InvalidParameterException();
        }
        JSONObject json = new JSONObject();
        json.put("data", data);
        json.put("value", value);
        ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
        oos.writeObject(json.toString());
    }

    private void parseMessage(String message) throws JSONException {
        JSONObject json = new JSONObject(message);
        String data = json.getString("data");
        boolean value = json.getBoolean("value");
        switch (data) {
            case "isOpen":
                //todo: gui.setDoorStateIsOpen(value)
                System.out.println("is open: " + value);
                break;
            case "isClosed":
                //todo: gui.setDoorStateIsClosed(value)
                break;
        }
    }
}