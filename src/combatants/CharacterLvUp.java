package combatants;

import exceptions.ClientDisconnectedException;
import server.ServerBattleController;

public class CharacterLvUp {
	private static final double energy_gain_factor = 1.1;
	private static final double life_gain_factor = 1.1;
	private static final int level_gain_summand = 1;
	private static final double attack_gain_factor = 1.1;
	private static final double defense_gain_factor = 1.1;
	private static final double iserialogy_gain_factor = 1.1;

	public static void level_up(Character character, ServerBattleController serverBattleController) throws ClientDisconnectedException {
		send_lv_up_message_title(serverBattleController);
		increase_level(character);
		increase_attack(character);
		increase_defense(character);
		increase_iserialogy(character);
		increase_max_life(character);
		increase_max_energy(character);
		decrease_current_experience(character);
		increase_required_experience(character);
		CharacterOperations.fully_heal_life(character);
		CharacterOperations.fully_heal_energy(character);
		send_lv_up_message_body(character, serverBattleController);
	}

	private static void increase_required_experience(Character character) {
		character.experience_to_next_level = (int) Math.ceil(character.experience_to_next_level * 1.1);
	}

	private static void decrease_current_experience(Character character) {
		character.experience_current -= character.experience_to_next_level;
	}

	private static void increase_iserialogy(Character character) {
		character.iserialogy = (int) Math.ceil(character.iserialogy * iserialogy_gain_factor);
	}

	private static void increase_defense(Character character) {
		character.defense = (int) Math.ceil(character.defense * defense_gain_factor);
	}

	private static void increase_attack(Character character) {
		character.attack = (int) Math.ceil(character.attack * attack_gain_factor);
	}

	private static void increase_level(Character character) {
		character.level += level_gain_summand;
	}

	private static void increase_max_energy(Character character) {
		character.max_energy = (int) Math.ceil(character.max_energy * life_gain_factor);
	}
	
	private static void increase_max_life(Character character) {
		character.max_life = (int) Math.ceil(character.max_life * energy_gain_factor);
	}

	private static void send_lv_up_message_title(ServerBattleController serverBattleController)
			throws ClientDisconnectedException {
		serverBattleController.server_messaging_system.send_message_to_client("", true);
		serverBattleController.server_messaging_system.send_message_to_client("|=============|", true);
		serverBattleController.server_messaging_system.send_message_to_client("|| LEVEL UP! ||", true);
		serverBattleController.server_messaging_system.send_message_to_client("|=============|", true);
		serverBattleController.server_messaging_system.send_message_to_client("", true);
	}

	private static void send_lv_up_message_body(Character character, ServerBattleController serverBattleController)
			throws ClientDisconnectedException {
		serverBattleController.server_messaging_system.send_message_to_client("You are now a Lv" + character.level + " " + character.character_class + "!", false);
		serverBattleController.server_messaging_system.send_message_to_client("Your Life and Energy have been fully restored.", false);
		serverBattleController.server_messaging_system.send_message_to_client("", true);
	}
}
