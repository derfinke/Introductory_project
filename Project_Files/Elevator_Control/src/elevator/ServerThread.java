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
    //todo: private Control control;

    public ServerThread(int port/*todo: , Control control*/) {
        this.port = port;
        //todo: this.control = control;
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
                parseMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data, boolean value) throws IOException, JSONException {
        if (!(data.equals("isOpen") || data.equals("isClosed"))){
            throw new InvalidParameterException();
        }
        JSONObject json = new JSONObject();
        json.put("data", data);
        json.put("value", value);
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        oos.writeObject(json.toString());
    }

    private void parseMessage(String message) throws JSONException {
        JSONObject json = new JSONObject(message);
        String data = json.getString("data");
        boolean value = json.getBoolean("value");
        switch (data) {
            case "reset":
                //todo: gui.reset(value)
                System.out.println("reset: " + value);
                break;
            case "open":
                //todo: gui.openDoor(value)
                break;
            case "close":
                //todo: gui.closeDoor(value)
                break;
        }

    }
}
