package elevator;

import org.json.JSONException;

import java.io.IOException;

public class Main {
	
	    public static void main(String[] args) throws IOException, InterruptedException, JSONException 
	    {
	    	ElevatorControl control = new ElevatorControl();
	    	ServerThread server = new ServerThread(8080);
	    	server.initElevatorControl(control);
	    	server.start();
	    }
}
