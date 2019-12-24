import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class DelveClient {
	
	String text_buffer;
	Socket server;
	String server_address = "127.0.0.1";
	int server_port = 1337;
	String version = "0.4";
	
	InputStream server_input_stream;
	BufferedReader server_input;
	PrintWriter client_output;
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
			
			//The server input stream
			server_input_stream = server.getInputStream();
			
			//The text that is being received from the server
			server_input = new BufferedReader(new InputStreamReader(server_input_stream));
			
			//The text that is sent to the server
			client_output = new PrintWriter(server.getOutputStream());
			
			object_output_to_server = new ObjectOutputStream(server.getOutputStream());
			object_input_from_server = new ObjectInputStream(server.getInputStream());
			
			
			//The user input
			user_input = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Client Version " + version);
			
			//Await welcome message
			await_server_message_single();
			send_string_terminator();
			
			//Await Name entry
			await_server_communication();
			get_user_input();
			await_server_communication();
			
			
			//Echo Phase
			System.out.println("You have reached the end of the delve.");
			System.out.println("A great, empty cave unfolds before your eyes.");
			System.out.println("Surely there's a great echo here.");
			System.out.println("What do you want to call out?");
			
			while(true) {
				get_user_input();
				await_server_communication();
				
				System.out.println("That was nice.");
				System.out.println("What do you want to call out?");
			}
			
		}
		
		catch (java.net.ConnectException e){
			System.out.println("ERROR: Delve Server does not appear to be running; Attempted to connect to Server: " + server_address + " Port: " + server_port);
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}

	}
	
	//This function sends a String Terminator to the server, signaling that the client is ready for the next request
	public void send_string_terminator() {
		try {
		Communication comm = new Communication(CommunicationTypes.over, "");
			object_output_to_server.writeObject(comm);
			object_output_to_server.flush();
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	
	//This function awaits a single message from the server and prints it
	public void await_server_message_single() {
		try {
			while(!server_input.ready()) {
				Thread.sleep(100);
			}
			while(server_input.ready()) {
				text_buffer = server_input.readLine();
				System.out.println(text_buffer);
			}
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	

	
	//This function awaits multiple messages from the server and prints them until a String Terminator is sent
	public void await_server_message_multi() {
		try {
			while(true) {
				while(!server_input.ready()) {
					Thread.sleep(100);
				}
				while(server_input.ready()) {
					text_buffer = server_input.readLine();
					if(text_buffer.contentEquals("")){
						return;
					}
					System.out.println(text_buffer);
				}
			}
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	
	//Handles incoming communications from the server
	public void await_server_communication() {
		try {
			Communication comm = (Communication) object_input_from_server.readObject();
			
			if (comm.type.equals(CommunicationTypes.message)) {
				handle_server_message(comm);
			}
			else if (comm.type.equals(CommunicationTypes.message_autoscroll)) {
				handle_server_message_autoscroll(comm);
			}
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}	
	}
	
	public void handle_server_message(Communication comm) {
		System.out.println(comm.message);
	}
	
	
	public void handle_server_message_autoscroll(Communication comm) {
		System.out.println(comm.message);
		await_server_communication();
	}
	
	//Requests user input and sends it to the server
	public void get_user_input() {
		try {
			//Get user input
			System.out.print("> ");
			text_buffer = user_input.readLine();
			
			Communication comm = new Communication(CommunicationTypes.message, text_buffer);
			object_output_to_server.writeObject(comm);
			object_output_to_server.flush();
			
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}

}
