package server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import combatants.Character;
import dungeon.Dungeon;
import elements.Element;
import elements.Elements;
import enemies.Enemy;
import enemies.EnemyZevi;
import exceptions.ClientDisconnectedException;
import messages.Messages;
import messaging_system.Communication;
import messaging_system.Question;
import messaging_system.QuestionOption;
import town.Town;

public class ServerGameController {
	
	String text_buffer;
	String version;
	ObjectInputStream object_input_from_client;
	ServerMessagingSystem server_messaging_system;
	ServerBattleController server_battle_controller;
	Server_Object_Stream server_object_stream;
	public Dungeon dungeon;
	public Town town;
	public Character character;
	public int starting_coordinate_north = 0;
	public int starting_coordinate_east = 0;
	

	public ServerGameController(Server_Object_Stream server_object_stream, String version) {
		this.server_object_stream = server_object_stream;
		this.version = version;
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		server_battle_controller = new ServerBattleController(server_messaging_system);
	}
	
	public void game_init() throws IOException, ClientDisconnectedException{
		Messages.print_welcome_message(server_messaging_system, version);
		
		character_creation();
		
		first_battle();
		if(character.current_life <= 0) {
			return;
		}
		
		initialize_town();
		initialize_dungeon_and_enter_first_room();
		
		while(character.current_life > 0) {
			dungeon.ask_which_way_to_go_and_move_to_next_room();
		}
	}
	
	void initialize_town() {
		town = new Town(this, server_messaging_system, character);
	}

	public void initialize_dungeon_and_enter_first_room() throws ClientDisconnectedException {
		dungeon = new Dungeon(this, server_messaging_system, server_battle_controller);
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
	
	public void character_creation() throws ClientDisconnectedException {
		character = new Character();
		ask_character_name(character);
		ask_character_element(character);
	}
	
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
		player_character.element = Elements.get_element_by_id(element_id);
	}
}
