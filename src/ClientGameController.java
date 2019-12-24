import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientGameController {
	
	ObjectInputStream object_input_from_server;
	ObjectOutputStream object_output_to_server;
	Socket server;
	
	String text_buffer;
	BufferedReader user_input;
	
	public ClientGameController(Socket server) {
		this.server = server;
		try {
			object_output_to_server = new ObjectOutputStream(server.getOutputStream());
			object_input_from_server = new ObjectInputStream(server.getInputStream());
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		
		//The user input
		user_input = new BufferedReader(new InputStreamReader(System.in));
	}
	
	//The functions that get carried out at the beginning of the game
	public void game_init() {
		//Await welcome message
		await_server_communication();
		send_string_terminator();
		
		//Await Name entry
		await_server_communication();
		get_user_input();
		await_server_communication();
		
		//Echo Phase at end of game
		echo_phase();
	}
	
	public void echo_phase() {
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
