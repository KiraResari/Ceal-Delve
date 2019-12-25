import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerGameController {
	
	String text_buffer;
	Socket client;
	String version;
	ObjectInputStream object_input_from_client;
	ObjectOutputStream object_output_to_client;
	

	public ServerGameController(Socket client, String version) {
		this.client = client;
		this.version = version;
		try {
			object_output_to_client = new ObjectOutputStream(client.getOutputStream());
			object_input_from_client = new ObjectInputStream(client.getInputStream());
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	
	//The functions that get carried out at the beginning of the game
	public void game_init() throws IOException{
		//Sends welcome message
		welcome_message(client);
		
		//Wait for the over message
		await_over();
		
		character_creation();
		
		//Echo phase at the end of the game
		echo_phase();
	}

	public void echo_phase() {
		send_message_to_client("You have reached the end of the delve.", true);
		send_message_to_client("A great, empty cave unfolds before your eyes.", true);
		send_message_to_client("Surely there's a great echo here.", true);
		send_message_to_client("What do you want to call out?", false);
		
		while(true) {
			Communication reply = await_client_reply();
			if(reply == null) {
				break;
			}
			String incoming_message = reply.message;
			send_message_to_client(incoming_message + "... " + incoming_message + "...... " + incoming_message + "......... ", true);
			send_message_to_client("", true);
			send_message_to_client("That was nice.", true);
			send_message_to_client("What do you want to call out?", false);
	
		}
	}
	
	public void welcome_message(Socket client) throws IOException{
		send_message_to_client("        //------------------------//", true);
		send_message_to_client("       // Welcome to...          //", true);
		send_message_to_client("      //                        //", true);
		send_message_to_client("     // THE CHRONICLES OF CEAL //", true);
		send_message_to_client("    //          ~             //", true);
		send_message_to_client("   //     DEEPER DELVING     //", true);
		send_message_to_client("  //                        //", true);
		send_message_to_client(" //         by Kira Resari //", true);
		send_message_to_client("//------------------------//", true);
		send_message_to_client("Server Version " + version, true);
		send_message_to_client("", false);
		System.out.println("Sent welcome message");
	}
	
	//Waits for the client to send a String Terminator before proceeding
	public void await_over() throws IOException{
		System.out.println("Waiting for over-message...");
		while(true) {
			Communication reply = await_client_reply();
			if(reply.type.equals(CommunicationTypes.over)){
				break;
			}
		}
		System.out.println("Received Over-message. Continuing.");
	}
	
	public void character_creation() {
		Character player_character = new Character();
		
		//Asks for a character name
		ask_character_name(player_character);
		
		//Asks for the character's favorite element
		ask_character_element(player_character);
	}
	
	//Asks for the character name
	public void ask_character_name(Character player_character) {
		// Sends the question
		send_message_to_client("What is your name?", false);
		
		// Waits for a reply
		Communication reply = await_client_reply();
		
		//Records the character name
		player_character.name = reply.message;
		
		//Greets the player character
		send_message_to_client("Hello " + player_character.name + ", welcome to the wonderful world of Ceal!", true);
		send_message_to_client("It is a fantastic world full of marvels, but also dangers.", true);
		send_message_to_client("", false);
		
	}
	
	public void ask_character_element(Character player_character) {
		
		Communication reply;
		int element_id;
		
		//Sends the question
		while(true) {
			String question_message = "What is your favourite element?";
			List<QuestionOption> question_options = new ArrayList<QuestionOption>();
			List<Element> available_elements = Elements.get_available_elements();
			available_elements.forEach((element) ->
				{
					question_options.add(new QuestionOption(element.name, Integer.toString(element.id)));
				}
			);
			Question question = new Question(question_message, question_options);
			
			reply = send_question_to_client(question);
			element_id = Integer.parseInt(reply.message);
			
			question_message = "So your favourite element is " + Elements.get_element_by_id(element_id).name + ". Is that right?";
	
			question = new Question(question_message);
			reply = send_question_to_client(question);
			
			if(reply.message.equals("Y")) {
				send_message_to_client("", true);
				break;
			}
			else {
				send_message_to_client("Okay, then let me ask again:", true);
			}
		}
		
		//Assigns the element to the character
		player_character.element = Elements.get_element_by_id(element_id);
		
	}
	
	//Sends a message to the client
	public void send_message_to_client(String message, Boolean autoscroll) {
		String type;
		if(autoscroll) {
			type = CommunicationTypes.message_autoscroll;
		}
		else {
			type = CommunicationTypes.message;
		}
		
		Communication outgoing_communication = new Communication(type, message);
		
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent message to client: " + message);
	}
	
	public Communication send_question_to_client(Question question) {
		Communication outgoing_communication = new Communication(CommunicationTypes.question, question);
		//Communication outgoing_communication = new Communication(CommunicationTypes.message, "Test question");
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent question to client: " + question.question_message);
		
		// Waits for a reply
		Communication reply = await_client_reply();
		
		while(true) {
			
			//Checks for a valid reply
			if (question.validateReply(reply.message)){
				break;
			}
			else {
				send_message_to_client("Please pick from the provided options:", true);
				send_message_to_client(question.question_option_hotkeys.toString(), false);
			}
			reply = await_client_reply();
		}
		return reply;
	}
	
	//Awaits a reply from the client
	public Communication await_client_reply() {
		try {
			Communication comm = (Communication) object_input_from_client.readObject();
			String message = comm.message;
			
			System.out.println("Received message from client: " + message);
			return comm;
		}
		catch (java.net.SocketException e){
			System.out.println("Client disconnected");
			return null;
		}
		catch (Exception e){
			System.out.println("Runtime Error: " + e);
			e.printStackTrace();
			return null;
		}
	}
}
