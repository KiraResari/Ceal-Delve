package client;
import java.io.IOException;

import exceptions.ServerDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;

public class ClientGameController {
	
	Client_Object_Stream client_object_stream;
	Console console;
	
	public ClientGameController( Console console, Client_Object_Stream client_object_stream) throws IOException {
		this.console = console;
		this.client_object_stream = client_object_stream;
	}
	
	//The functions that get carried out at the beginning of the game
	public void game_init() throws ServerDisconnectedException, ClassNotFoundException, IOException {
		while(true) {
			await_server_communication();
		}
	}

	//Handles incoming communications from the server
	public void await_server_communication() throws ServerDisconnectedException, IOException, ClassNotFoundException {
		try {
			Communication incoming_communication = client_object_stream.get_communication_from_server();
			
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
				console.println("ERROR: Communication Type '" + incoming_communication.type + "' is not implemented in the client.");
			}
		} 
		
		catch (java.net.SocketException e){
			throw new ServerDisconnectedException("SocketException in await_server_communication");
		}
	}
	
	public void handle_server_message(Communication incoming_communication) {
		console.print(incoming_communication.message);
		console.print(" »");
	}
	
	
	public void handle_server_message_autoscroll(Communication incoming_communication) throws ServerDisconnectedException, ClassNotFoundException, IOException {
		console.println(incoming_communication.message);
		await_server_communication();
	}
	
	public void handle_server_question(Communication incoming_communication) {
		incoming_communication.question.print_question();
	}
	
	public void handle_server_free_text_entry_request(Communication incoming_communication) {
		console.println(incoming_communication.message);
	}
	
	public void get_user_input() throws IOException {
		String user_input = console.get_user_input_with_prompt();
		client_object_stream.send_message_to_server(this, user_input);
	}
	
	public void wait_for_enter() throws IOException {
		console.get_user_input_without_prompt();
		client_object_stream.send_message_to_server(this, "");
	}
}
