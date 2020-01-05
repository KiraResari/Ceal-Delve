package town;

import java.util.ArrayList;
import java.util.List;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;
import messaging_system.QuestionOption;
import server.ServerGameController;
import server.ServerMessagingSystem;
import combatants.Character;
import combatants.CharacterOperations;

public class Town {
	private ServerMessagingSystem server_messaging_system;
	private ServerGameController server_game_controller;
	private Character character;
	
	public Town(ServerGameController server_game_controller, ServerMessagingSystem server_messaging_system, Character character) {
		this.server_messaging_system = server_messaging_system;
		this.server_game_controller = server_game_controller; 
		this.character = character;
	}
	
	public void enter() throws ClientDisconnectedException {
		send_enter_town_message();
		ask_and_perform_activity();
	}

	private void send_enter_town_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.title_bars, true);
		server_messaging_system.send_message_to_client(strings.Town.title, true);
		server_messaging_system.send_message_to_client(strings.Town.title_bars, true);
		server_messaging_system.send_message_to_client("", true);
	}

	private void ask_and_perform_activity() throws ClientDisconnectedException {
		Communication reply = ask_what_to_do();
		perform_activity(reply);
	}

	private Communication ask_what_to_do() throws ClientDisconnectedException {
		Question question = build_activity_question();
		Communication reply = server_messaging_system.send_question_to_client(question);
		return reply;
	}

	private Question build_activity_question() {
		String question_message = strings.Town.activity_prompt1 + character.µ + strings.Town.activity_prompt2;
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		if(character_is_not_fully_restored()) {
			question_options.add(new QuestionOption(strings.Town.activity_option_rest, strings.Hotkeys.rest));
		}
		question_options.add(new QuestionOption(strings.Town.activity_option_buy_healing_kykli, strings.Hotkeys.healing_kykli));
		question_options.add(new QuestionOption(strings.Town.activity_option_buy_energy_water, strings.Hotkeys.energy_water));
		question_options.add(new QuestionOption(strings.Town.activity_option_return_to_cave, strings.Hotkeys.cave));
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		return question;
	}

	private boolean character_is_not_fully_restored() {
		if(CharacterOperations.return_missing_life(character) > 0 || CharacterOperations.return_missing_energy(character) > 0) {
			return true;
		}
		return false;
	}

	private void perform_activity(Communication reply) throws ClientDisconnectedException {
		if(reply.message.toUpperCase().equals(strings.Hotkeys.rest)) {
			rest();
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.healing_kykli)) {
			buy_healing_kykli();
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.energy_water)) {
			buy_energy_water();
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.cave)) {
			enter_cave();
			return;
		}
		server_messaging_system.send_message_to_client("", true);
		ask_and_perform_activity();
	}

	private void rest() throws ClientDisconnectedException {
		int resting_cost;
		resting_cost = determine_resting_cost();
		if(character_cant_afford_rest(resting_cost)) {
			send_cant_afford_rest_message(resting_cost);
			return;
		}
		if(ask_whether_to_rest_for_price(resting_cost)) {
			send_resting_message();
			CharacterOperations.subtract_µ(character, resting_cost);
			CharacterOperations.fully_heal_life(character);
			CharacterOperations.fully_heal_energy(character);
		}
	}

	private void send_resting_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.rest1, false);
		server_messaging_system.send_message_to_client(strings.Town.rest2, false);
	}

	private void send_cant_afford_rest_message(int resting_cost) throws ClientDisconnectedException {
		String message;
		message = strings.Town.cant_afford_rest1 + Integer.toString(resting_cost) + strings.Town.cant_afford_rest2;
		server_messaging_system.send_message_to_client(message, false);
	}

	private boolean character_cant_afford_rest(int resting_cost) {
		if(character.µ < resting_cost) {
			return true;
		}
		return false;
	}

	private int determine_resting_cost() {
		int resting_cost = 0;
		resting_cost += CharacterOperations.return_missing_life(character);
		resting_cost += CharacterOperations.return_missing_energy(character);
		return resting_cost;
	}

	private boolean ask_whether_to_rest_for_price(int resting_cost) throws ClientDisconnectedException {
		String question_message;
		Question question;
		Communication reply;
		question_message = strings.Town.rest_ask1 + Integer.toString(resting_cost) + strings.Town.rest_ask2;
		question = Question.construct_yes_no_question(question_message);
		reply = server_messaging_system.send_question_to_client(question);	
		if(reply.message.toUpperCase().equals("Y")) {
			return true;
		}
		return false;
	}

	private void buy_healing_kykli() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.out_of_stock, false);
	}

	private void buy_energy_water() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.out_of_stock, false);
	}

	private void enter_cave() throws ClientDisconnectedException {
		send_enter_cave_message();
		server_game_controller.dungeon.enter_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
	}

	private void send_enter_cave_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.return_to_cave, false);
		server_messaging_system.send_message_to_client("", true);
	}
}
