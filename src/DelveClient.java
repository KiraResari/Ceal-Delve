import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DelveClient {
	
	String text_buffer;
	Socket server;
	String server_address = "127.0.0.1";
	int server_port = 1337;
	String version = "0.4";
	
	BufferedReader server_input;
	BufferedReader user_input;
	ObjectInputStream object_input_from_server;
	ObjectOutputStream object_output_to_server;
	
	public static void main(String[] args) {
		DelveClient s = new DelveClient();
		s.start_client();
		
	}

	public void start_client() {
		server = null;
		
		try {
			//Connect to the server
			server = new Socket(server_address, server_port);
			
			ClientGameController game_controller = new ClientGameController(server);
			
			System.out.println("Client Version " + version);
			
			game_controller.game_init();
			
		}
		
		catch (java.net.ConnectException e){
			System.out.println("ERROR: Delve Server does not appear to be running; Attempted to connect to Server: " + server_address + " Port: " + server_port);
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}

	}
}
