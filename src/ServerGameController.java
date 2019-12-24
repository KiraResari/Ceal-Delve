import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	
	//The functions that get carried out at the beginning of the game
	public void game_init() throws IOException{
		//Sends welcome message
		welcome_message(client);
		
		//Wait for the over message
		await_over();
		
		//Asks the character name
		ask_character_name();
		
		//Echo
		while(true) {
			try {
				Communication reply = await_client_reply();
				if(reply == null) {
					break;
				}
				String message = reply.message;
				send_message_to_client(message + "... " + message + "...... " + message + "......... ", false);
			
			}
			catch (Exception e){
				System.out.println("Runtime Error: " + e);
				e.printStackTrace();
			}	
		}
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
	
	//Asks for the character name
	public void ask_character_name() {
		// Sends the question
		Character player_character = new Character();
		send_message_to_client("What is your name?", false);
		
		// Waits for a reply
		Communication reply = await_client_reply();
		
		//Records the character name
		player_character.name = reply.message;
		
		//Greets the player character
		send_message_to_client("Hello " + player_character.name + ", welcome to the wonderful world of Ceal!", true);
		send_message_to_client("It is a fantastic world full of marvels, but also dangers.", false);
		
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
		
		Communication comm = new Communication(type, message);
		
		try {
			object_output_to_client.writeObject(comm);
			object_output_to_client.flush();
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent message to client: " + message);
		
		/*
		output_to_client.println(message);
		output_to_client.flush();
		System.out.println("Sent message:" + message);
		*/
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
