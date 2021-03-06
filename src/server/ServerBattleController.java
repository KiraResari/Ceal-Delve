package server;
import java.util.ArrayList;
import java.util.List;

import combatants.Character;
import combatants.CharacterLvUp;
import combatants.Combatant;
import enemies.Enemy;
import exceptions.ClientDisconnectedException;
import messages.Messages;
import messaging_system.Communication;
import messaging_system.Question;
import messaging_system.QuestionOption;

public class ServerBattleController {
	public Character character;
	Combatant victor;
	Enemy enemy;
	public ServerMessagingSystem server_messaging_system;
	int damage;
	
	int iserialogy_cost = 5;
	int iserialogy_power_factor = 2;
	int healing_kykli_power = 20;
	int energy_water_power = 10;
	
	public ServerBattleController(ServerMessagingSystem server_messaging_system) {
		this.server_messaging_system = server_messaging_system;
	}
	
	public void battle(Character character, Enemy enemy) throws ClientDisconnectedException{
		this.character = character;
		this.enemy = enemy;
		victor = null;
		Messages.print_battle_init_message(server_messaging_system, enemy);
		battle_flow();
		battle_end();
	}
	
	public void battle_flow() throws ClientDisconnectedException {
		while(victor == null) {
			player_turn();
			check_victor();
			if(victor != null) {
				break;
			}
			enemy_turn();
			check_victor();
		}
	}
	
	public void battle_end() throws ClientDisconnectedException {
		if(victor == character){
			victory();
		}
		else if(victor == enemy) {
			defeat();
		}
	}
	
	public void victory() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(enemy.defeat_narrative, false);
		server_messaging_system.send_message_to_client("You gained " + enemy.experience + " Exp,", false);
		character.experience_current += enemy.experience;
		if(character.experience_current >= character.experience_to_next_level) {
			CharacterLvUp.level_up(character, this);
		}
		server_messaging_system.send_message_to_client("You found " + enemy.� + "�.", false);
		character.� += enemy.�;
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client("|==============|", true);
		server_messaging_system.send_message_to_client("|| BATTLE END ||", true);
		server_messaging_system.send_message_to_client("|==============|", true);
		server_messaging_system.send_message_to_client(" ", true);
	}
	
	private void defeat() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(enemy.player_kill_narrative, false);
		server_messaging_system.send_message_to_client("", true);
		Messages.print_game_over_message(server_messaging_system);
		
	}
	
	public void player_turn() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("It's your turn!", true);
		character.defending = false;
		print_player_turn_gui();
		String question_message = "What do you want to do?";
		List<QuestionOption> question_options = get_player_turn_options();
		Communication reply_from_client = server_messaging_system.send_question_to_client(Question.construct_question_with_custom_options(question_message, question_options));
		String player_choice = reply_from_client.message.toUpperCase();
		if(player_choice.equals(strings.Hotkeys.attack)) {
			player_attack();
		}
		else if(player_choice.equals(strings.Hotkeys.iserialogy)) {
			player_iserialogy();
		}
		else if(player_choice.equals(strings.Hotkeys.defend)) {
			player_defend();
		}
		else if(player_choice.equals(strings.Hotkeys.healing_kykli)) {
			player_healing_kykli();
		}
		else if(player_choice.equals(strings.Hotkeys.energy_water)) {
			player_energy_water();
		}
		server_messaging_system.send_message_to_client("", true);
	}
	
	public void enemy_turn() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(enemy.attack_narrative, false);
		damage = calculate_attack_damage_dealt_by_enemy();
		character_take_damage(damage);
		server_messaging_system.send_message_to_client("", true);
	}

	public int calculate_attack_damage_dealt_by_enemy() throws ClientDisconnectedException {
		int dealt_damage = enemy.attack - character.defense;
		if(character.defending) {
			int reduced_damage = calculate_damage_reduction_from_defending(dealt_damage);
			player_regain_energy(reduced_damage);
			dealt_damage -= reduced_damage;
			server_messaging_system.send_message_to_client("You take " + reduced_damage + " less damage because you defended and regain " + reduced_damage + " energy.", false);
		}
		return dealt_damage;
	}

	public int calculate_damage_reduction_from_defending(int incoming_damage) {
		int reduced_damage = (int) Math.ceil(incoming_damage / 2.0);
		return reduced_damage;
	}

	public void character_take_damage(int damage) throws ClientDisconnectedException {
		if(damage > 0) {
			character.current_life -= damage;
			server_messaging_system.send_message_to_client("You take " + damage + " damage.", false);
		} else {
			server_messaging_system.send_message_to_client("...but you don't takes any damage.", false);
		}
	}
	
	public void check_victor() {
		if(character.current_life <= 0) {
			victor = enemy;
		}
		else if(enemy.current_life <= 0) {
			victor = character;
		}
	}

	public void print_player_turn_gui() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("//==================================================", true);
		server_messaging_system.send_message_to_client("|| " + character.name, true);
		server_messaging_system.send_message_to_client("|| Lv" + character.level + " " + character.character_class + "   Exp: " + character.experience_current + "/" + character.experience_to_next_level, true);
		server_messaging_system.send_message_to_client("|| Life: " + character.current_life + "/" + character.max_life + "   Energy: " + character.current_energy + "/" + character.max_energy + "   Attack: " + character.attack + "   Defense: " + character.defense, true);
		server_messaging_system.send_message_to_client("// Healing Kykli: " + character.healing_kykli_count + "   Energy Water: " + character.energy_water_count + "   " + character.� + "�", true);
		server_messaging_system.send_message_to_client("/", true);
		server_messaging_system.send_message_to_client("", true);
		server_messaging_system.send_message_to_client("\\ ENEMY", true);
		server_messaging_system.send_message_to_client("\\\\ Lv" + enemy.level + " " + enemy.name, true);
		server_messaging_system.send_message_to_client("|| Life: " + enemy.current_life + "/" + enemy.max_life, true);
		server_messaging_system.send_message_to_client("\\\\==================================================", true);
		
	}
	
	public List<QuestionOption> get_player_turn_options() {
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Attack", strings.Hotkeys.attack));
		if(character.current_energy >= iserialogy_cost) {
			question_options.add(new QuestionOption("Iserialogy", strings.Hotkeys.iserialogy));
		}
		question_options.add(new QuestionOption("Defend", strings.Hotkeys.defend));
		if(character.healing_kykli_count > 0 && character.current_life < character.max_life) {
			question_options.add(new QuestionOption("Use Healing Kykli", strings.Hotkeys.healing_kykli));
		}
		if(character.energy_water_count > 0 && character.current_energy < character.max_energy) {
			question_options.add(new QuestionOption("Drink Energy Water", strings.Hotkeys.energy_water));
		}
		
		return question_options;
	}
	
	public void player_attack() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You attack the " + enemy.name + "!", false);
		damage = calculate_attack_damage_dealt_by_player();
		if(damage > 0) {
			enemy.current_life -= damage;
			server_messaging_system.send_message_to_client("The " + enemy.name + " takes " + damage + " damage.", false);
		} else {
			server_messaging_system.send_message_to_client("...but the " + enemy.name + " takes no damage.", false);
		}
	}

	public int calculate_attack_damage_dealt_by_player() {
		return (character.attack - enemy.defense);
	}
	
	public void player_iserialogy() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You invoke the Iserialogy " + character.element.name + " Blast!", false);
		character.current_energy -= iserialogy_cost;
		damage = calculate_iserialogy_damage_dealt_by_player();
		if(damage > 0) {
			enemy.current_life -= damage;
			server_messaging_system.send_message_to_client("The " + enemy.name + " takes " + damage + " " + character.element.name + " damage.", false);
		} else {
			server_messaging_system.send_message_to_client("...but the " + enemy.name + " takes no damage.", false);
		}
	}

	public int calculate_iserialogy_damage_dealt_by_player() throws ClientDisconnectedException {
		int calculated_damage;
		calculated_damage = calculate_iserialogy_raw_strength();
		if(enemy.weakness == character.element) {
			server_messaging_system.send_message_to_client("You hit the enemy's weak point!", false);
			calculated_damage *= 2;
		}
		calculated_damage -= enemy.defense;
		return calculated_damage;
	}

	public int calculate_iserialogy_raw_strength() {
		return character.iserialogy * iserialogy_power_factor;
	}
	
	public void player_defend() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You defend yourself.", false);
		character.defending = true;
	}
	
	public void player_healing_kykli() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You use a Healing Kykli to heal yourself.", false);
		character.healing_kykli_count -= 1;
		int life_before = character.current_life;
		player_regain_life(healing_kykli_power);
		int life_regenerated = character.current_life - life_before;
		if(character.current_life == character.max_life) {
			server_messaging_system.send_message_to_client("You are fully healed.", false);
		}else {
			server_messaging_system.send_message_to_client("Your health is restored by " + life_regenerated + " points.", false);
		}
	}
	
	public void player_energy_water() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You drink some Energy Water to restore your Energy.", false);
		character.energy_water_count -= 1;
		int energy_before = character.current_energy;
		player_regain_energy(energy_water_power);
		int energy_regenerated = character.current_energy - energy_before;
		if(character.current_energy == character.max_energy) {
			server_messaging_system.send_message_to_client("Your energy is fully restored.", false);
		}else {
			server_messaging_system.send_message_to_client("Your energy is restored by " + energy_regenerated + " points.", false);
		}
	}
	
	public void player_regain_energy(int energy_regeneration) {
		character.current_energy += energy_regeneration;
		if(character.current_energy > character.max_energy) {
			character.current_energy = character.max_energy;
		}
	}
	
	public void player_regain_life(int life_regeneration) {
		character.current_life += life_regeneration;
		if(character.current_life > character.max_life) {
			character.current_life = character.max_life;
		}
	}
	
}
