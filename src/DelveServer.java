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
			game_init();
			
			
			
		}
	}
	
	public void welcome_message(Socket client) throws IOException{
		output_to_client.println("        //------------------------//");
		output_to_client.println("       // Welcome to...          //");
		output_to_client.println("      //                        //");
		output_to_client.println("     // THE CHRONICLES OF CEAL //");
		output_to_client.println("    //          ~             //");
		output_to_client.println("   //     DEEPER DELVING     //");
		output_to_client.println("  //                        //");
		output_to_client.println(" //         by Kira Resari //");
		output_to_client.println("//------------------------//");
		output_to_client.println();
		output_to_client.flush();
		System.out.println("Sent welcome message");
	}
	
	//The functions that get carried out at the beginning of the game
	public void game_init() throws IOException{
		//Sends welcome message
		welcome_message(client);
		
		//Wait for the over message
		await_over();
		
		//Asks the character name
		ask_character_name();
		
		//Echo
		try {
			while((text_buffer = input_from_client.readLine()) != null) {
				text_buffer = "Re: " + text_buffer;
				output_to_client.println(text_buffer);
				output_to_client.flush();
				System.out.println("Sent message: " + text_buffer);
			}
		}
		catch (java.net.SocketException e){
			System.out.println("Client disconnected");
		}
		catch (Exception e){
			System.out.println("Runtime Error: " + e);
			e.printStackTrace();
		}
	}
	
	//Waits for the client to send an over-message before proceeding
	public void await_over() throws IOException{
		System.out.println("Waiting for over-message...");
		while(true) {
			await_client_reply();
			if(text_buffer.contentEquals("")){
				break;
			}
		}
		System.out.println("Received Over-message. Continuing.");
	}
	
	//Asks for the character name
	public void ask_character_name() {
		// Sends the question
		Character player_character = new Character();
		send_message_to_client("What is your name?");
		
		// Waits for a reply
		await_client_reply();
		
		//Records the character name
		player_character.name = text_buffer;
		
		//Greets the player character
		send_message_to_client("Hello " + player_character.name + ", welcome to the wonderful world of Ceal!");
		
	}
	
	//Sends a message to the client
	public void send_message_to_client(String message) {
		output_to_client.println(message);
		output_to_client.flush();
		System.out.println("Sent message:" + message);
	}
	
	
	//Awaits a reply from the client
	public void await_client_reply() {
		try {
			while(!input_from_client.ready()) {
				try {
					Thread.sleep(100);
				} catch (Exception e){
					System.out.println("Runtime Error: " + e);
					e.printStackTrace();
				}
			}
			while(input_from_client.ready()) {
				text_buffer = input_from_client.readLine();
				System.out.println("Received message from client: " + text_buffer);
			}
		}
		catch (Exception e){
			System.out.println("Runtime Error: " + e);
			e.printStackTrace();
		}
	}

}
