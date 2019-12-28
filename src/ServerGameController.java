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
	Dungeon dungeon;
	Character character;
	int starting_coordinate_north = 0;
	int starting_coordinate_east = 0;
	

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
		
		//Character creation
		character_creation();
		
		//First battle
		first_battle();
		if(character.current_life <= 0) {
			return;
		}
		
		initialize_dungeon_and_enter_first_room();
		
		while(true) {
			dungeon.ask_which_way_to_go_and_move_to_next_room();
		}
		
		/*
		//Further battles
		for(int i = 0; i < 4; i++) {
			battle(new EnemyZevi());
		}
		
		//Echo phase at the end of the game
		echo_phase();
		*/
	}
	
	public void initialize_dungeon_and_enter_first_room() throws ClientDisconnectedException {
		dungeon = new Dungeon(server_messaging_system, server_battle_controller, character);
		server_messaging_system.send_message_to_client("After overcoming this first obstacle, you make your way deeper into the delve.", false);
		dungeon.enter_room_at_coordinates(starting_coordinate_north, starting_coordinate_east);
	}
	
	public void first_battle() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("Tales of riches have let you to a cave.", false);
		server_messaging_system.send_message_to_client("Some of the adventurers who entered it have returned loaded with treasure.", false);
		server_messaging_system.send_message_to_client("Others have not returned at all.", false);
		server_messaging_system.send_message_to_client("What fate will await you?", false);
		server_messaging_system.send_message_to_client("Carefully, you make your way into the cave.", false);
		server_messaging_system.send_message_to_client("Suddenly, you come across the first challenge of your delve.", false);
		battle(new EnemyZevi());
	}
	
	public void battle(Enemy enemy) throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("An enemy appears before you!", false);
		server_battle_controller.battle(character, enemy);
	}

	public void echo_phase() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You have reached the end of the delve.", false);
		server_messaging_system.send_message_to_client("A great, empty cave unfolds before your eyes.", false);
		server_messaging_system.send_message_to_client("Surely there's a great echo here.", false);
		
		while(true) {
			Communication reply = server_messaging_system.send_free_text_entry_request_to_client("What do you want to call out?");
			String incoming_message = reply.message;
			server_messaging_system.send_message_to_client(incoming_message + "... " + incoming_message + "...... " + incoming_message + "......... ", false);
			server_messaging_system.send_message_to_client("That was nice.", false);
		}
	}
	
	public void welcome_message(Socket client) throws IOException, ClientDisconnectedException{
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
		server_messaging_system.send_message_to_client("", true);
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
		character = new Character();
		
		//Asks for a character name
		ask_character_name(character);
		
		//Asks for the character's favorite element
		ask_character_element(character);
	}
	
	//Asks for the character name
	public void ask_character_name(Character player_character) throws ClientDisconnectedException {
		Communication reply = server_messaging_system.send_free_text_entry_request_to_client("What is your name?");
		player_character.name = reply.message;
		
		server_messaging_system.send_message_to_client("Hello " + player_character.name + ", welcome to the wonderful world of Ceal! (press Enter/Return to continue)", false);
		server_messaging_system.send_message_to_client("It is a fantastic world full of marvels, but also dangers.", false);
		server_messaging_system.send_message_to_client("", true);
		
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
			Question question = Question.construct_question_with_custom_options(question_message, question_options);
			
			reply = server_messaging_system.send_question_to_client(question);
			element_id = Integer.parseInt(reply.message);
			
			question_message = "So your favourite element is " + Elements.get_element_by_id(element_id).name + ". Is that right?";
	
			question = Question.construct_yes_no_question(question_message);
			reply = server_messaging_system.send_question_to_client(question);
			
			if(reply.message.toUpperCase().equals("Y")) {
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
