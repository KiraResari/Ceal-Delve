import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonRoom {
	int coordinate_north;
	int coordinate_east;
	int depth;
	String room_description_adjective;
	String room_description_type;
	boolean visited_before = false;
	boolean enemy_present;
	boolean treasure_present;
	double treasure_chance = 0.2;
	double enemy_chance = 0.5;
	double treasure_chest_is_trapped_chance = 0.1;
	int treasure_chest_explosion_damage = 10;
	double treasure_chest_�_multiplier_lower = 1;
	double treasure_chest_�_multiplier_upper = 2;
	Random random_generator = new Random();
	ServerBattleController server_battle_controller;
	ServerMessagingSystem server_messaging_system;
	Character character;
	
	public DungeonRoom(int coordinate_north, int coordinate_east, ServerMessagingSystem server_messaging_system, ServerBattleController server_battle_controller, Character character) {
		this.coordinate_north = coordinate_north;
		this.coordinate_east = coordinate_east;
		this.depth = coordinate_north + coordinate_east;
		this.server_messaging_system = server_messaging_system;
		this.server_battle_controller = server_battle_controller;
		this.character = character;
		generate_random_room_description();
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
		if(treasure_present){
			trigger_looting();
		}
	}

	private void print_nothing_here_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("There's nothing interesting to be found in this particular cave.", false);
	}

	private void trigger_looting() throws ClientDisconnectedException {
		Communication reply = server_messaging_system.send_question_to_client(Question.construct_yes_no_question("You find a treasure chest. Do you want to open it?"));
		if(reply.message.toUpperCase().equals("Y")) {
			open_treasure_chest();
		}else{
			server_messaging_system.send_message_to_client("You leave the treasure chest where it is.", false);
		}
		server_messaging_system.send_message_to_client("", true);
	}

	private void open_treasure_chest() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You open the treasure chest.", false);
		if(treasure_chest_is_trapped()) {
			treasure_chest_explodes();
		}
		else {
			get_treasure_from_chest();
		}
		treasure_present = false;
	}
	
	private void get_treasure_from_chest() throws ClientDisconnectedException {
		int �_found = determine_�_in_treasure_chest();
		character.� += �_found;
		server_messaging_system.send_message_to_client("You found " + �_found + "�! (You now have " + character.� + "�)", false);
	}
	
	private int determine_�_in_treasure_chest() {
		int lower_limit = (int)((depth + 1) * treasure_chest_�_multiplier_lower);
		int upper_limit = (int)((depth + 1) * treasure_chest_�_multiplier_upper);
		return random_generator.nextInt(upper_limit - lower_limit) + lower_limit;
	}

	private void treasure_chest_explodes() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(" \\ | | | / ", true);
		server_messaging_system.send_message_to_client("--BOOM!!!--", true);
		server_messaging_system.send_message_to_client(" / | | | \\ ", true);
		server_messaging_system.send_message_to_client("The treasure chest explodes in your face!", false);
		int damage = treasure_chest_explosion_damage - character.defense;
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
		Enemy enemy = generate_random_enemy();
		server_battle_controller.battle(character, enemy);
		enemy_present = false;
		server_messaging_system.send_message_to_client("", true);
	}

	private Enemy generate_random_enemy() {
		List<Enemy> available_enemies = generate_available_enemies_list();
		Enemy random_enemy = available_enemies.get(random_generator.nextInt(available_enemies.size()));
		return random_enemy;
	}
	
	private List<Enemy> generate_available_enemies_list(){
		List<Enemy> available_enemies = new ArrayList<Enemy>();
		available_enemies.add(new EnemyZevi());
		return available_enemies;
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
		if(random_generator.nextDouble() < treasure_chance) {
			treasure_present = true;
		}
		else {
			treasure_present = false;
		}
	}

	private void check_if_enemy_spawns_and_spawn_it() {
		if(random_generator.nextDouble() < enemy_chance) {
			enemy_present = true;
		}
		else {
			enemy_present = false;
		}
	}

	public void print_visited_before_message() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("You realize you've been in this cave once before.", false);
		server_messaging_system.send_message_to_client("", true);
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