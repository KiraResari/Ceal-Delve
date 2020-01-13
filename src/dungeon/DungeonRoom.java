package dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import combatants.Character;
import enemies.AvailableEnemiesLists;
import enemies.Enemy;
import enemies.EnemyHamster;
import enemies.EnrageEnemy;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;
import server.ServerBattleController;
import server.ServerGameController;
import server.ServerMessagingSystem;

public class DungeonRoom {
	int coordinate_north;
	int coordinate_east;
	int depth;
	String room_description_adjective;
	String room_description_type;
	boolean visited_before = false;
	boolean enemy_present;
	boolean treasure_present;
	boolean town_access_present;
	double treasure_chance = 0.2;
	double enemy_chance = 0.7;
	double treasure_chest_is_trapped_chance = 0.1;
	int treasure_chest_explosion_base_damage = 10;
	double treasure_chest_µ_multiplier_lower = 1;
	double treasure_chest_µ_multiplier_upper = 2;
	int hamster_coordinates_seed = 10;
	Random random_generator = new Random();
	ServerBattleController server_battle_controller;
	ServerMessagingSystem server_messaging_system;
	ServerGameController server_game_controller;
	Character character;
	
	public DungeonRoom(int coordinate_north, int coordinate_east, ServerGameController server_game_controller, ServerMessagingSystem server_messaging_system, ServerBattleController server_battle_controller) {
		this.coordinate_north = coordinate_north;
		this.coordinate_east = coordinate_east;
		this.depth = coordinate_north + coordinate_east;
		this.server_messaging_system = server_messaging_system;
		this.server_battle_controller = server_battle_controller;
		this.server_game_controller = server_game_controller;
		this.character = server_game_controller.character;
		determine_town_access();
		generate_random_room_description();
	}
	
	void determine_town_access() {
		town_access_present = false;
		if(is_starting_room() || is_portal_room()) {
			town_access_present = true;
		}
	}

	private boolean is_portal_room() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean is_starting_room() {
		if(this.coordinate_east == server_game_controller.starting_coordinate_east && this.coordinate_north == server_game_controller.starting_coordinate_north) {
			return true;
		}
		return false;
	}

	public void enter_room() throws ClientDisconnectedException {
		print_enter_room_message();
		if(visited_before) {
			print_visited_before_message();
		} else {
			visited_before = true;
			determine_room_contents();

			if(!enemy_present && !treasure_present) {
				print_nothing_here_message();
			}
		}
		if(enemy_present){
			trigger_battle();
		}
		if(character.current_life <= 0) {
			return;
		}
		if(treasure_present){
			trigger_looting();
		}
		if(town_access_present) {
			print_town_access_message();
		}
	}

	private void print_town_access_message() throws ClientDisconnectedException {
		if(is_starting_room()) {
			server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.town_access_starting_room, false);
		} else if(is_portal_room()) {
			server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.town_access_portal, false);
		}
	}

	private void print_nothing_here_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.empty_cave, false);
	}

	private void trigger_looting() throws ClientDisconnectedException {
		Communication reply = server_messaging_system.send_question_to_client(Question.construct_yes_no_question(strings.Dungeon_Exploration_Strings.treasure_chest_find));
		if(reply.message.toUpperCase().equals(strings.Hotkeys.yes)) {
			open_treasure_chest();
		}else{
			server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.treasure_chest_leave, false);
		}
		server_messaging_system.send_message_to_client("", true);
	}

	private void open_treasure_chest() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.treasure_chest_open, false);
		if(treasure_chest_is_trapped()) {
			treasure_chest_explodes();
		}
		else {
			get_treasure_from_chest();
		}
		treasure_present = false;
	}
	
	private void get_treasure_from_chest() throws ClientDisconnectedException {
		int µ_found = determine_µ_in_treasure_chest();
		character.µ += µ_found;
		server_messaging_system.send_message_to_client("You found " + µ_found + "µ! (You now have " + character.µ + "µ)", false);
	}
	
	private int determine_µ_in_treasure_chest() {
		int lower_limit = (int)((depth + 1) * treasure_chest_µ_multiplier_lower);
		int upper_limit = (int)((depth + 1) * treasure_chest_µ_multiplier_upper);
		return random_generator.nextInt(upper_limit - lower_limit) + lower_limit;
	}

	private void treasure_chest_explodes() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(" \\ | | | / ", true);
		server_messaging_system.send_message_to_client("--BOOM!!!--", true);
		server_messaging_system.send_message_to_client(" / | | | \\ ", true);
		server_messaging_system.send_message_to_client("The treasure chest explodes in your face!", false);
		int damage = treasure_chest_explosion_base_damage + depth - character.defense;
		server_battle_controller.character_take_damage(damage);
		if(character.current_life <= 0) {
			character.current_life = 1;
			server_messaging_system.send_message_to_client("You have barely managed to survive the explosion.", false);
		}
		server_messaging_system.send_message_to_client("Current life: " + character.current_life + "/" + character.max_life, false);
	}

	private boolean treasure_chest_is_trapped() {
		if(random_generator.nextDouble() < treasure_chest_is_trapped_chance) {
			return true;
		}
		else {
			return false;
		}
	}

	private void trigger_battle() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("An enemy awaits you here!", false);
		Enemy enemy;
		if(is_hamster_room()) {
			enemy = generate_hamster();
		}
		else {
			enemy = generate_random_enemy();
		}
		server_battle_controller.battle(character, enemy);
		enemy_present = false;
		server_messaging_system.send_message_to_client("", true);
	}

	private Enemy generate_hamster() {
		Enemy enemy;
		enemy = new EnemyHamster();
		int rage_level = ((depth - 10) / hamster_coordinates_seed);
		EnrageEnemy.enrage(enemy, rage_level);
		return enemy;
	}

	private Enemy generate_random_enemy() {
		List<Enemy> available_enemies = generate_available_enemies_list();
		Enemy random_enemy = available_enemies.get(random_generator.nextInt(available_enemies.size()));
		return random_enemy;
	}
	
	private List<Enemy> generate_available_enemies_list(){
		return AvailableEnemiesLists.get_available_enemies_by_depth(depth);
	}

	public void print_enter_room_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You arrive at a cave.", false);
		server_messaging_system.send_message_to_client("", true);
		print_room_header();
		server_messaging_system.send_message_to_client("It's a " + room_description_adjective + " " + room_description_type + ".", false);
	}

	public void print_room_header() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("//==================================================", true);
		server_messaging_system.send_message_to_client("|| Room North " + Integer.toString(coordinate_north) + " | East " + Integer.toString(coordinate_east), true);
		server_messaging_system.send_message_to_client("// Delve Depth " + depth, true);
		server_messaging_system.send_message_to_client("/", true);
	}
	
	public void determine_room_contents() {
		check_if_enemy_spawns_and_spawn_it();
		check_if_treasure_spawns_and_spawn_it();
	}
	
	private void check_if_treasure_spawns_and_spawn_it() {
		if(random_generator.nextDouble() < treasure_chance || is_hamster_room()) {
			treasure_present = true;
		}
		else {
			treasure_present = false;
		}
	}

	private void check_if_enemy_spawns_and_spawn_it() {
		if(random_generator.nextDouble() < enemy_chance || is_hamster_room()) {
			enemy_present = true;
		}
		else {
			enemy_present = false;
		}
	}
	

	private boolean is_hamster_room() {
		if(coordinate_east % hamster_coordinates_seed == 0 && coordinate_north % hamster_coordinates_seed == 0 && coordinate_east == coordinate_north && coordinate_east > 0) {
			return true;
		}
		return false;
	}

	public void print_visited_before_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.visited_before, false);
	}
	
	private void generate_random_room_description() {
		generate_random_room_description_adjective();
		generate_random_room_description_type();
	}

	private void generate_random_room_description_adjective() {
		List<String> random_room_description_adjectives = new ArrayList<String>();
		random_room_description_adjectives.add("narrow");
		random_room_description_adjectives.add("spacious");
		random_room_description_adjectives.add("hot");
		random_room_description_adjectives.add("cold");
		random_room_description_adjectives.add("cool");
		random_room_description_adjectives.add("warm");
		random_room_description_adjectives.add("windy");
		random_room_description_adjectives.add("great");
		random_room_description_adjectives.add("damp");
		random_room_description_adjectives.add("dry");
        room_description_adjective = random_room_description_adjectives.get(random_generator.nextInt(random_room_description_adjectives.size())); 
	}

	private void generate_random_room_description_type() {
		List<String> random_room_description_types = new ArrayList<String>();
		random_room_description_types.add("cave");
		random_room_description_types.add("cave with a lake");
		random_room_description_types.add("cavern");
		random_room_description_types.add("stalagmite cave");
		random_room_description_types.add("cave with old ruins");
		random_room_description_types.add("cave with a lava stream");
		random_room_description_types.add("mushroom cave");
		random_room_description_types.add("grotto");
		random_room_description_types.add("hollow");
		random_room_description_types.add("dome-roofed cavern");
        room_description_type = random_room_description_types.get(random_generator.nextInt(random_room_description_types.size())); 
	}
}
