package dungeon;
import java.util.ArrayList;
import java.util.List;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;
import messaging_system.QuestionOption;
import server.ServerBattleController;
import server.ServerGameController;
import server.ServerMessagingSystem;

public class Dungeon {
	List<DungeonRoom> dungeon_rooms = new ArrayList<DungeonRoom>();
	ServerMessagingSystem server_messaging_system;
	ServerBattleController server_battle_controller;
	ServerGameController server_game_controller;
	DungeonRoom current_room;
	
	public Dungeon(ServerGameController server_game_controller, ServerMessagingSystem server_messaging_system, ServerBattleController server_battle_controller) {
		this.server_messaging_system = server_messaging_system;
		this.server_battle_controller = server_battle_controller;
		this.server_game_controller = server_game_controller;
	}
	
	public void create_new_room_at_coordinates(int coordinate_north, int coordinate_east){
		DungeonRoom room = new DungeonRoom(coordinate_north, coordinate_east, server_game_controller, server_messaging_system, server_battle_controller);
		dungeon_rooms.add(room);
	}
	
	public DungeonRoom return_room_at_coordinates(int coordinate_north, int coordinate_east) {
		for(DungeonRoom room : dungeon_rooms) {
			if(room.coordinate_north == coordinate_north && room.coordinate_east == coordinate_east) {
				return room;
			}
		}
		return null;
	}
	
	public boolean room_exists_at_coordinates(int coordinate_north, int coordinate_east) {
		for(DungeonRoom room : dungeon_rooms) {
			if(room.coordinate_north == coordinate_north && room.coordinate_east == coordinate_east) {
				return true;
			}
		}
		return false;
	}
	
	public void enter_room_at_coordinates(int coordinate_north, int coordinate_east) throws ClientDisconnectedException {
		if(!room_exists_at_coordinates(coordinate_north, coordinate_east)) {
			create_new_room_at_coordinates(coordinate_north, coordinate_east);
		}
		current_room = return_room_at_coordinates(coordinate_north, coordinate_east);
		current_room.enter_room();
	}
	
	public void ask_which_way_to_go_and_move_to_next_room() throws ClientDisconnectedException {
		Communication reply = ask_which_way_to_go();
		if(reply.message.toUpperCase().equals(strings.Hotkeys.town)) {
			go_to_town();
		}else {
			move_into_direction(reply);
		}
		server_messaging_system.send_message_to_client("", true);
	}

	private void move_into_direction(Communication reply) throws ClientDisconnectedException {
		int target_coordinate_north = current_room.coordinate_north;
		int target_coordinate_east = current_room.coordinate_east;
		if(reply.message.toUpperCase().equals(strings.Hotkeys.north)) {
			target_coordinate_north = move_north(target_coordinate_north);
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.east)) {
			target_coordinate_east = move_east(target_coordinate_east);
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.south)) {
			target_coordinate_north = move_south(target_coordinate_north);
		}else if(reply.message.toUpperCase().equals(strings.Hotkeys.west)) {
			target_coordinate_east = move_west(target_coordinate_east);
		}
		enter_room_at_coordinates(target_coordinate_north, target_coordinate_east);
	}

	private void go_to_town() throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.go_to_town, false);
		server_messaging_system.send_message_to_client("", true);
		server_game_controller.town.enter();
	}

	private int move_west(int target_coordinate_east) throws ClientDisconnectedException {
		target_coordinate_east --;
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.explore_towards_west, false);
		return target_coordinate_east;
	}

	private int move_south(int target_coordinate_north) throws ClientDisconnectedException {
		target_coordinate_north --;
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.explore_towards_south, false);
		return target_coordinate_north;
	}

	private int move_east(int target_coordinate_east) throws ClientDisconnectedException {
		target_coordinate_east ++;
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.explore_towards_east, false);
		return target_coordinate_east;
	}

	private int move_north(int target_coordinate_north) throws ClientDisconnectedException {
		target_coordinate_north ++;
		server_messaging_system.send_message_to_client(strings.Dungeon_Exploration_Strings.explore_towards_north, false);
		return target_coordinate_north;
	}

	public Communication ask_which_way_to_go() throws ClientDisconnectedException {
		String question_message = construct_which_way_to_go_question_message();
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption(strings.Dungeon_Exploration_Strings.north_capitalized, strings.Hotkeys.north));
		question_options.add(new QuestionOption(strings.Dungeon_Exploration_Strings.east_capitalized, strings.Hotkeys.east));
		if(current_room.coordinate_north > 0) {
			question_options.add(new QuestionOption(strings.Dungeon_Exploration_Strings.south_capitalized, strings.Hotkeys.south));
		}
		if(current_room.coordinate_east > 0) {
			question_options.add(new QuestionOption(strings.Dungeon_Exploration_Strings.west_capitalized, strings.Hotkeys.west));
		}
		if(current_room.town_access_present) {
			question_options.add(new QuestionOption(strings.Dungeon_Exploration_Strings.return_to_town, strings.Hotkeys.town));
		}
		Communication reply = server_messaging_system.send_question_to_client(Question.construct_question_with_custom_options(question_message, question_options));
		return reply;
	}
	
	private String construct_which_way_to_go_question_message() {
		return strings.Dungeon_Exploration_Strings.which_way_to_go_question_message_part1 + current_room.coordinate_north + strings.Dungeon_Exploration_Strings.which_way_to_go_question_message_part2 + current_room.coordinate_east + ")";
	}
}
