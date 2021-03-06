package server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import exceptions.ClientDisconnectedException;


public class DelveServer {
	
	String text_buffer;
	Socket client;
	String version = "1.4";
	int port = 1337;
	Server_Object_Stream server_object_stream;

	public static void main(String[] args) {
		DelveServer s = new DelveServer();
		s.start_server();
	}
	
	public void start_server() {
		try {
			// Start Server
			ServerSocket server = new ServerSocket(port);
			
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
			server_object_stream = new Server_Object_Stream(client);
			
			try {
				//Record client Address
				String client_address = client.getInetAddress().getHostAddress() + client.getInetAddress().getHostName();
				System.out.println("Incoming client from: " + client_address);
				
				//Initiate Game for that client
				ServerGameController game_controller = new ServerGameController(server_object_stream, version);
				game_controller.game_init();
			}
			catch (ClientDisconnectedException e){
				System.out.println("Client Disconnected");
			}
		}
	}
}
