package elevator;

import org.json.JSONException;

import java.io.IOException;

public class Main {
	
	    public static void main(String[] args) throws IOException, InterruptedException, JSONException 
	    {
	    	GUI gui = new GUI();
	    	ClientThread thread = new ClientThread(8080, gui);
	    	gui.initClientThread(thread);
	    	thread.start();
	    }
}
