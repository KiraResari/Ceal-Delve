import java.util.ArrayList;
import java.util.List;

public class ServerBattleController {
	Character character;
	Combatant victor;
	Enemy enemy;
	ServerMessagingSystem server_messaging_system;
	int damage;
	
	int iserialogy_cost = 5;
	int iserialogy_power = 10;
	int healing_kykli_power = 20;
	int energy_water_power = 10;
	
	public ServerBattleController(ServerMessagingSystem server_messaging_system) {
		this.server_messaging_system = server_messaging_system;
	}
	
	public void battle(Character character, Enemy enemy){
		this.character = character;
		this.enemy = enemy;
		victor = null;
		battle_init();
		battle_flow();
		battle_end();
	}
	
	public void battle_init(){
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client("|=================|", true);
		server_messaging_system.send_message_to_client("|| BATTLE START! ||", true);
		server_messaging_system.send_message_to_client("|=================|", true);
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client(enemy.entry_narrative, true);
		server_messaging_system.send_message_to_client(" ", true);
	}
	
	public void battle_flow() {
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
	
	public void battle_end() {
		if(victor == character){
			victory();
		}
		else if(victor == enemy) {
			defeat();
		}
	}
	
	public void player_turn() {
		server_messaging_system.send_message_to_client("It's your turn!", true);
		print_player_turn_gui();
		String question_message = "What do you want to do?";
		List<QuestionOption> question_options = get_player_turn_options();
		Communication reply_from_client = server_messaging_system.send_question_to_client(new Question(question_message, question_options));
		String player_choice = reply_from_client.message;
		if(player_choice.equals("A")) {
			player_attack();
		}
		else if(player_choice.equals("I")) {
			player_iserialogy();
		}
		else if(player_choice.equals("D")) {
			player_defend();
		}
		else if(player_choice.equals("H")) {
			player_healing_kykli();
		}
		else if(player_choice.equals("E")) {
			player_energy_water();
		}
		server_messaging_system.send_message_to_client("", true);
	}
	
	public void enemy_turn() {
		server_messaging_system.send_message_to_client(enemy.attack_narrative, true);
		damage = enemy.attack - character.defense;
		if(damage > 0) {
			character.current_life -= damage;
			server_messaging_system.send_message_to_client("You take " + damage + " damage.", true);
		} else {
			server_messaging_system.send_message_to_client("...but you don't takes any damage.", true);
		}
		server_messaging_system.send_message_to_client("", true);
	}
	
	public void check_victor() {
		if(character.current_life <= 0) {
			victor = enemy;
		}
		else if(enemy.current_life <= 0) {
			victor = character;
		}
	}
	
	public void victory() {
		server_messaging_system.send_message_to_client(enemy.defeat_narrative, true);
		server_messaging_system.send_message_to_client("You gained " + enemy.experience + " Exp,", true);
		character.experience_current += enemy.experience;
		if(character.experience_current >= character.experience_to_next_level) {
			level_up();
		}
		server_messaging_system.send_message_to_client("You found " + enemy.µ + "µ.", true);
		character.µ += enemy.µ;
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client("|==============|", true);
		server_messaging_system.send_message_to_client("|| BATTLE END ||", true);
		server_messaging_system.send_message_to_client("|==============|", true);
		server_messaging_system.send_message_to_client(" ", true);
	}
	
	public void defeat() {
		server_messaging_system.send_message_to_client(enemy.player_kill_narrative, true);
		server_messaging_system.send_message_to_client("", true);
		server_messaging_system.send_message_to_client("//===============\\\\", true);
		server_messaging_system.send_message_to_client("||               ||", true);
		server_messaging_system.send_message_to_client("||   GAME OVER   ||", true);
		server_messaging_system.send_message_to_client("||               ||", true);
		server_messaging_system.send_message_to_client("\\\\===============//", true);
		
	}
	
	public void level_up() {
		server_messaging_system.send_message_to_client("", true);
		server_messaging_system.send_message_to_client("|=============|", true);
		server_messaging_system.send_message_to_client("|| LEVEL UP! ||", true);
		server_messaging_system.send_message_to_client("|=============|", true);
		server_messaging_system.send_message_to_client("", true);
		character.level += 1;
		character.attack += 1;
		character.defense += 1;
		character.max_life *= 1.1;
		character.max_energy *= 1.1;
		character.experience_current -= character.experience_to_next_level;
		character.experience_to_next_level *= 1.1;
		server_messaging_system.send_message_to_client(" You are now a Lv" + character.level + " " + character.character_class + "!", true);
		character.current_life = character.max_life;
		character.current_energy = character.max_energy;
		server_messaging_system.send_message_to_client(" Your Life and Energy have been fully restored.", true);
	}
	
	public void print_player_turn_gui() {
		server_messaging_system.send_message_to_client("//==================================================", true);
		server_messaging_system.send_message_to_client("|| " + character.name, true);
		server_messaging_system.send_message_to_client("|| Lv" + character.level + " " + character.character_class + "   Exp: " + character.experience_current + "/" + character.experience_to_next_level, true);
		server_messaging_system.send_message_to_client("|| Life: " + character.current_life + "/" + character.max_life + "   Energy: " + character.current_energy + "/" + character.max_energy + "   Attack: " + character.attack + "   Defense: " + character.defense, true);
		server_messaging_system.send_message_to_client("// Healing Kykli: " + character.healing_kykli_count + "   Energy Water: " + character.energy_water_count + "   " + character.µ + "µ", true);
		server_messaging_system.send_message_to_client("/", true);
		server_messaging_system.send_message_to_client("", true);
		server_messaging_system.send_message_to_client("\\ ENEMY", true);
		server_messaging_system.send_message_to_client("\\\\ Lv" + enemy.level + " " + enemy.name, true);
		server_messaging_system.send_message_to_client("|| Life: " + enemy.current_life + "/" + enemy.max_life, true);
		server_messaging_system.send_message_to_client("\\\\==================================================", true);
		
	}
	
	public List<QuestionOption> get_player_turn_options() {
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Attack", "A"));
		if(character.current_energy >= iserialogy_cost) {
			question_options.add(new QuestionOption("Iserialogy", "I"));
		}
		question_options.add(new QuestionOption("Defend", "D"));
		if(character.healing_kykli_count > 0) {
			question_options.add(new QuestionOption("Use Healing Kykli", "H"));
		}
		if(character.energy_water_count > 0) {
			question_options.add(new QuestionOption("Drink Energy Water", "E"));
		}
		
		return question_options;
	}
	
	public void player_attack() {
		server_messaging_system.send_message_to_client("You attack the " + enemy.name, true);
		damage = character.attack - enemy.defense;
		if(damage > 0) {
			enemy.current_life -= damage;
			server_messaging_system.send_message_to_client("The " + enemy.name + " takes " + damage + " damage.", true);
		} else {
			server_messaging_system.send_message_to_client("...but the " + enemy.name + " takes no damage.", true);
		}
	}
	
	public void player_iserialogy() {
		server_messaging_system.send_message_to_client("NOT IMPLEMENTED YET", true);
	}
	
	public void player_defend() {
		server_messaging_system.send_message_to_client("NOT IMPLEMENTED YET", true);
	}
	
	public void player_healing_kykli() {
		server_messaging_system.send_message_to_client("NOT IMPLEMENTED YET", true);
	}
	
	public void player_energy_water() {
		server_messaging_system.send_message_to_client("NOT IMPLEMENTED YET", true);
	}
	
}
