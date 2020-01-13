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
	static int healing_kykli_cost = 25;
	static int energy_water_cost = 15;
	
	public Town(ServerGameController server_game_controller, ServerMessagingSystem server_messaging_system, Character character) {
		this.server_messaging_system = server_messaging_system;
		this.server_game_controller = server_game_controller; 
		this.character = character;
	}
	
	public void enter() throws ClientDisconnectedException {
		messages.Messages.print_enter_town_message(server_messaging_system);
		ask_and_perform_activity();
	}

	private void ask_and_perform_activity() throws ClientDisconnectedException {
		Communication reply = ask_what_to_do();
		perform_activity(reply);
	}

	Communication ask_what_to_do() throws ClientDisconnectedException {
		Question question = build_activity_question();
		Communication reply = server_messaging_system.send_question_to_client(question);
		return reply;
	}

	Question build_activity_question() {
		String question_message = strings.Town_Strings.activity_prompt1 + character.µ + strings.Town_Strings.activity_prompt2;
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		if(character_is_not_fully_restored()) {
			question_options.add(new QuestionOption(strings.Town_Strings.activity_option_rest, strings.Hotkeys.rest));
		}
		question_options.add(new QuestionOption(strings.Town_Strings.activity_option_buy_healing_kykli, strings.Hotkeys.healing_kykli));
		question_options.add(new QuestionOption(strings.Town_Strings.activity_option_buy_energy_water, strings.Hotkeys.energy_water));
		question_options.add(new QuestionOption(strings.Town_Strings.activity_option_return_to_cave, strings.Hotkeys.cave));
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		return question;
	}

	private boolean character_is_not_fully_restored() {
		if(CharacterOperations.return_missing_life(character) > 0 || CharacterOperations.return_missing_energy(character) > 0) {
			return true;
		}
		return false;
	}

	void perform_activity(Communication reply) throws ClientDisconnectedException {
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

	void rest() throws ClientDisconnectedException {
		int resting_cost = determine_resting_cost();
		if(character_cant_afford(resting_cost)) {
			send_cant_afford_message(strings.Town_Strings.cant_afford_rest, resting_cost);
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
		server_messaging_system.send_message_to_client(strings.Town_Strings.rest1, false);
		server_messaging_system.send_message_to_client(strings.Town_Strings.rest2, false);
	}

	private boolean character_cant_afford(int cost) {
		if(character.µ < cost) {
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
		question_message = strings.Town_Strings.rest_ask1 + Integer.toString(resting_cost) + strings.Town_Strings.rest_ask2;
		question = Question.construct_yes_no_question(question_message);
		reply = server_messaging_system.send_question_to_client(question);	
		if(reply.message.toUpperCase().equals("Y")) {
			return true;
		}
		return false;
	}

	void buy_healing_kykli() throws ClientDisconnectedException {
		if(character_cant_afford(healing_kykli_cost)) {
			send_cant_afford_message(strings.Town_Strings.cant_afford_healing_kykli, healing_kykli_cost);
		} else if (ask_whether_to_buy_a_healing_kykli()) {
			perform_healing_kykli_purchase_transaction();
			send_healing_kykli_purchased_message();
		}
	}

	private void send_cant_afford_message(String cant_afford_message, int price) throws ClientDisconnectedException {
		String message = cant_afford_message + Integer.toString(price) + strings.Town_Strings.cant_afford_end_stub;
		server_messaging_system.send_message_to_client(message, false);
	}

	private void perform_healing_kykli_purchase_transaction() {
		character.µ -= healing_kykli_cost;
		character.healing_kykli_count += 1;
	}
	
	private boolean ask_whether_to_buy_a_healing_kykli() throws ClientDisconnectedException {
		String question_message = strings.Town_Strings.buy_healing_kykli_ask1 + Integer.toString(healing_kykli_cost) + strings.Town_Strings.buy_healing_kykli_ask2 + character.healing_kykli_count + strings.Town_Strings.buy_healing_kykli_ask3;
		Question question = Question.construct_yes_no_question(question_message);
		Communication reply = server_messaging_system.send_question_to_client(question);	
		if(reply.message.toUpperCase().equals("Y")) {
			return true;
		}
		return false;
	}
	
	private void send_healing_kykli_purchased_message() throws ClientDisconnectedException {
		String message = strings.Town_Strings.buy_healing_kykli1 + character.healing_kykli_count + strings.Town_Strings.buy_healing_kykli2;
		server_messaging_system.send_message_to_client(message, false);
	}

	private void buy_energy_water() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town_Strings.out_of_stock, false);
	}

	private void enter_cave() throws ClientDisconnectedException {
		send_enter_cave_message();
		server_game_controller.dungeon.enter_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
	}

	private void send_enter_cave_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town_Strings.return_to_cave, false);
		server_messaging_system.send_message_to_client("", true);
	}
}
