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
	ServerMessagingSystem server_messaging_system;
	ServerBattleController server_battle_controller;
	Character player_character;

	public ServerGameController(Socket client, String version) {
		this.client = client;
		this.version = version;
		server_messaging_system = new ServerMessagingSystem(client);
		server_battle_controller = new ServerBattleController(server_messaging_system);
	}
	
	//The functions that get carried out at the beginning of the game
	public void game_init() throws IOException, ClientDisconnectedException{
		//Sends welcome message
		welcome_message(client);
		
		//Wait for the over message
		await_over();
		
		//Character creation
		character_creation();
		
		//First battle
		first_battle();
		if(player_character.current_life <= 0) {
			return;
		}
		
		//Further battles
		for(int i = 0; i < 4; i++) {
			battle(new EnemyZevi());
		}
		
		//Echo phase at the end of the game
		echo_phase();
	}
	
	public void first_battle() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("Tales of riches have let you to a cave.", true);
		server_messaging_system.send_message_to_client("Some of the adventurers woh entered it have returned loaded with treasure.", true);
		server_messaging_system.send_message_to_client("Others have not returned at all.", true);
		server_messaging_system.send_message_to_client("What fate will await you?", true);
		server_messaging_system.send_message_to_client("Carefully, you make your way into the cave.", true);
		server_messaging_system.send_message_to_client("Suddenly, the first challenge of your delve appears before you.", true);
		battle(new EnemyZevi());
	}
	
	public void battle(Enemy enemy) throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("An enemy appears before you!", true);
		server_battle_controller.battle(player_character, enemy);
	}

	public void echo_phase() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You have reached the end of the delve.", true);
		server_messaging_system.send_message_to_client("A great, empty cave unfolds before your eyes.", true);
		server_messaging_system.send_message_to_client("Surely there's a great echo here.", true);
		server_messaging_system.send_message_to_client("What do you want to call out?", false);
		
		while(true) {
			Communication reply = server_messaging_system.await_client_reply();
			if(reply.equals(null)) {
				break;
			}
			String incoming_message = reply.message;
			server_messaging_system.send_message_to_client(incoming_message + "... " + incoming_message + "...... " + incoming_message + "......... ", true);
			server_messaging_system.send_message_to_client("", true);
			server_messaging_system.send_message_to_client("That was nice.", true);
			server_messaging_system.send_message_to_client("What do you want to call out?", false);
	
		}
	}
	
	public void welcome_message(Socket client) throws IOException{
		server_messaging_system.send_message_to_client("        //------------------------//", true);
		server_messaging_system.send_message_to_client("       // Welcome to...          //", true);
		server_messaging_system.send_message_to_client("      //                        //", true);
		server_messaging_system.send_message_to_client("     // THE CHRONICLES OF CEAL //", true);
		server_messaging_system.send_message_to_client("    //          ~             //", true);
		server_messaging_system.send_message_to_client("   //     DEEPER DELVING     //", true);
		server_messaging_system.send_message_to_client("  //                        //", true);
		server_messaging_system.send_message_to_client(" //         by Kira Resari //", true);
		server_messaging_system.send_message_to_client("//------------------------//", true);
		server_messaging_system.send_message_to_client("Server Version " + version, true);
		server_messaging_system.send_message_to_client("", false);
		System.out.println("Sent welcome message");
	}
	
	//Waits for the client to send a String Terminator before proceeding
	public void await_over() throws IOException, ClientDisconnectedException{
		System.out.println("Waiting for over-message...");
		while(true) {
			Communication reply = server_messaging_system.await_client_reply();
			if(reply.type.equals(CommunicationTypes.over)){
				break;
			}
		}
		System.out.println("Received Over-message. Continuing.");
	}
	
	public void character_creation() throws ClientDisconnectedException {
		player_character = new Character();
		
		//Asks for a character name
		ask_character_name(player_character);
		
		//Asks for the character's favorite element
		ask_character_element(player_character);
	}
	
	//Asks for the character name
	public void ask_character_name(Character player_character) throws ClientDisconnectedException {
		// Sends the question
		server_messaging_system.send_message_to_client("What is your name?", false);
		
		// Waits for a reply
		Communication reply = server_messaging_system.await_client_reply();
		
		//Records the character name
		player_character.name = reply.message;
		
		//Greets the player character
		server_messaging_system.send_message_to_client("Hello " + player_character.name + ", welcome to the wonderful world of Ceal!", true);
		server_messaging_system.send_message_to_client("It is a fantastic world full of marvels, but also dangers.", true);
		server_messaging_system.send_message_to_client("", false);
		
	}
	
	public void ask_character_element(Character player_character) throws ClientDisconnectedException {
		
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
			
			reply = server_messaging_system.send_question_to_client(question);
			element_id = Integer.parseInt(reply.message);
			
			question_message = "So your favourite element is " + Elements.get_element_by_id(element_id).name + ". Is that right?";
	
			question = new Question(question_message);
			reply = server_messaging_system.send_question_to_client(question);
			
			if(reply.message.equals("Y")) {
				server_messaging_system.send_message_to_client("", true);
				break;
			}
			else {
				server_messaging_system.send_message_to_client("Okay, then let me ask again:", true);
			}
		}
		
		//Assigns the element to the character
		player_character.element = Elements.get_element_by_id(element_id);
		
	}
}
