package dungeon;
import java.util.ArrayList;
import java.util.List;

import combatants.Character;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;
import messaging_system.QuestionOption;
import server.ServerBattleController;
import server.ServerMessagingSystem;

public class Dungeon {
	List<DungeonRoom> dungeon_rooms = new ArrayList<DungeonRoom>();
	ServerMessagingSystem server_messaging_system;
	ServerBattleController server_battle_controller;
	Character character;
	DungeonRoom current_room;
	
	public Dungeon(ServerMessagingSystem server_messaging_system, ServerBattleController server_battle_controller, Character character) {
		this.server_messaging_system = server_messaging_system;
		this.server_battle_controller = server_battle_controller;
		this.character = character;
	}
	
	public void create_new_room_at_coordinates(int coordinate_north, int coordinate_east){
		DungeonRoom room = new DungeonRoom(coordinate_north, coordinate_east, server_messaging_system, server_battle_controller, character);
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
		int target_coordinate_north = current_room.coordinate_north;
		int target_coordinate_east = current_room.coordinate_east;
		if(reply.message.toUpperCase().equals("N")) {
			target_coordinate_north ++;
			server_messaging_system.send_message_to_client("You keep on exploring towards the north.", false);
		}else if(reply.message.toUpperCase().equals("E")) {
			target_coordinate_east ++;
			server_messaging_system.send_message_to_client("You keep on exploring towards the east.", false);
		}else if(reply.message.toUpperCase().equals("S")) {
			target_coordinate_north --;
			server_messaging_system.send_message_to_client("You keep on exploring towards the south.", false);
		}else if(reply.message.toUpperCase().equals("W")) {
			target_coordinate_east --;
			server_messaging_system.send_message_to_client("You keep on exploring towards the west.", false);
		}
		server_messaging_system.send_message_to_client("", true);
		enter_room_at_coordinates(target_coordinate_north, target_coordinate_east);
	}

	public Communication ask_which_way_to_go() throws ClientDisconnectedException {
		String question_message = "Which way do you want to go from here? (You are currently at North " + current_room.coordinate_north + " | East " + current_room.coordinate_east + ")";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("North", "N"));
		question_options.add(new QuestionOption("East", "E"));
		if(current_room.coordinate_north > 0) {
			question_options.add(new QuestionOption("South", "S"));
		}
		if(current_room.coordinate_east > 0) {
			question_options.add(new QuestionOption("West", "W"));
		}
		Communication reply = server_messaging_system.send_question_to_client(Question.construct_question_with_custom_options(question_message, question_options));
		return reply;
	}
}
