import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class DelveServer {
	
	String text_buffer;
	Socket client;
	BufferedReader input_from_client;
	PrintWriter output_to_client;
	String version = "0.3";

	public static void main(String[] args) {
		DelveServer s = new DelveServer();
		s.start_server();
	}
	
	public void start_server() {
		try {
			// Start Server
			ServerSocket server = new ServerSocket(1337);
			
			//Print IP Address
			System.out.println("Server started");
			InetAddress server_address = InetAddress.getLocalHost();
			String host_address = server_address.getHostAddress();
			System.out.println("Server is available under: " + host_address);
			System.out.println("Server Version " + version);
			
			//Run Server
			run_server(server);
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		
	}
	
	public void run_server(ServerSocket server) throws IOException{
		while(true) {
			//Accept incoming connections
			client = server.accept();
			
			//Record client Address
			String client_address = client.getInetAddress().getHostAddress() + client.getInetAddress().getHostName();
			System.out.println("Incoming client from: " + client_address);
			
			//Initiate I/O channels
			input_from_client = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output_to_client = new PrintWriter(client.getOutputStream());
			
			//Initiate Game for that client
			GameController game_controller = new GameController(client, version);
			game_controller.game_init();
			
			
			
		}
	}
	


}
