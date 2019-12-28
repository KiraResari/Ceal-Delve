import java.io.BufferedReader;
import java.io.IOException;
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
	public void game_init() throws ServerDisconnectedException {
		while(true) {
			await_server_communication();
		}
	}
	
	public void echo_phase() throws IOException, ServerDisconnectedException {
		//Echo Phase
		System.out.println();
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

	//Handles incoming communications from the server
	public void await_server_communication() throws ServerDisconnectedException {
		try {
			Communication incoming_communication = (Communication) object_input_from_server.readObject();
			
			if (incoming_communication.type.equals(CommunicationTypes.message)) {
				handle_server_message(incoming_communication);
				wait_for_enter();
			}
			else if (incoming_communication.type.equals(CommunicationTypes.message_autoscroll)) {
				handle_server_message_autoscroll(incoming_communication);
			}
			else if (incoming_communication.type.equals(CommunicationTypes.question)) {
				handle_server_question(incoming_communication);
				get_user_input();
			}
			else if (incoming_communication.type.equals(CommunicationTypes.free_text_entry)) {
				handle_server_free_text_entry_request(incoming_communication);
				get_user_input();
			}
			else {
				System.out.println("ERROR: Communication Type '" + incoming_communication.type + "' is not implemented in the client.");
			}
		} 
		
		catch (java.net.SocketException e){
			throw new ServerDisconnectedException("SocketException in await_server_communication");
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}	
	}
	
	public void handle_server_message(Communication incoming_communication) {
		System.out.print(incoming_communication.message);
		System.out.print(" »");
	}
	
	
	public void handle_server_message_autoscroll(Communication incoming_communication) throws ServerDisconnectedException {
		System.out.println(incoming_communication.message);
		await_server_communication();
	}
	
	public void handle_server_question(Communication incoming_communication) {
		incoming_communication.question.print_question();
	}
	
	public void handle_server_free_text_entry_request(Communication incoming_communication) {
		System.out.println(incoming_communication.message);
	}
	
	public void get_user_input() throws IOException {
		//Get user input
		System.out.print("> ");
		text_buffer = user_input.readLine();
		
		Communication comm = new Communication(CommunicationTypes.message, text_buffer);
		object_output_to_server.writeObject(comm);
		object_output_to_server.flush();
	}
	
	public void wait_for_enter() throws IOException {
		user_input.readLine();
		Communication comm = new Communication(CommunicationTypes.message, "");
		object_output_to_server.writeObject(comm);
		object_output_to_server.flush();
	}
}
