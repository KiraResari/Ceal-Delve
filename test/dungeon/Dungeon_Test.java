package dungeon;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import exceptions.ClientDisconnectedException;

import static org.hamcrest.CoreMatchers.instanceOf;

import server.ServerBattleController;
import server.ServerGameController;
import server.ServerMessagingSystem;
import test_case_suite.Server_Messaging_System_Test_Utils;
import town.Town;
import combatants.Character;

class Dungeon_Test {

	private ServerMessagingSystem server_messaging_system;
	private ServerGameController server_game_controller;
	private ServerBattleController server_battle_controller;
	private Dungeon dungeon;
	private Server_Messaging_System_Test_Utils server_messaging_system_test_utils;

	@Test
	void create_new_room_at_coordinates_is_added_to_room_list_test() {
		setup_test_dungeon();
		int initial_dungeon_rooms_size = dungeon.dungeon_rooms.size();
		int expected_dungeon_rooms_size = initial_dungeon_rooms_size + 1;
		
		dungeon.create_new_room_at_coordinates(0, 0);
		
		int actual_dungeon_rooms_size = dungeon.dungeon_rooms.size();
		assertEquals(expected_dungeon_rooms_size, actual_dungeon_rooms_size);
	}


	@Test
	void return_room_at_coordinates_returns_dungeon_room_object_test() {
		setup_test_dungeon();
		dungeon.create_new_room_at_coordinates(0, 0);
		
		Object dungeon_room = dungeon.return_room_at_coordinates(0, 0);
		
		assertThat(dungeon_room, instanceOf(DungeonRoom.class));
	}

	@Test
	void return_room_at_coordinates_returns_different_rooms_test() {
		setup_test_dungeon();
		dungeon.create_new_room_at_coordinates(0, 0);
		dungeon.create_new_room_at_coordinates(0, 1);
		
		DungeonRoom dungeon_room_1 = dungeon.return_room_at_coordinates(0, 0);
		DungeonRoom dungeon_room_2 = dungeon.return_room_at_coordinates(0, 1);
		
		assertNotEquals(dungeon_room_1, dungeon_room_2);
	}

	@Test
	void return_room_at_coordinates_returns_correct_room_test() {
		setup_test_dungeon();
		dungeon.create_new_room_at_coordinates(0, 0);
		dungeon.create_new_room_at_coordinates(0, 1);
		dungeon.create_new_room_at_coordinates(1, 0);
		dungeon.create_new_room_at_coordinates(1, 1);
		
		DungeonRoom dungeon_room = dungeon.return_room_at_coordinates(1, 1);
		
		assertEquals(1, dungeon_room.coordinate_north);
		assertEquals(1, dungeon_room.coordinate_east);
	}

	@Test
	void room_exists_at_coordinates_returns_true_for_existing_room_test() {
		setup_test_dungeon();
		dungeon.create_new_room_at_coordinates(0, 0);
		
		Boolean room_exists = dungeon.room_exists_at_coordinates(0, 0);
		
		assertTrue(room_exists);
	}

	@Test
	void room_exists_at_coordinates_returns_false_for_nonexisting_room_when_no_rooms_are_present_test() {
		setup_test_dungeon();
		
		Boolean room_exists = dungeon.room_exists_at_coordinates(0, 0);
		
		assertFalse(room_exists);
	}

	@Test
	void room_exists_at_coordinates_returns_false_for_nonexisting_room_test() {
		setup_test_dungeon();
		dungeon.create_new_room_at_coordinates(0, 0);
		dungeon.create_new_room_at_coordinates(0, 1);
		dungeon.create_new_room_at_coordinates(1, 0);
		
		Boolean room_exists = dungeon.room_exists_at_coordinates(1, 1);
		
		assertFalse(room_exists);
	}
	
	@Test
	void enter_room_at_coordinates_creates_new_room_if_nonexistant_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.no);
		
		assertFalse(dungeon.room_exists_at_coordinates(0, 0));
		
		dungeon.enter_room_at_coordinates(0, 0);
		
		assertTrue(dungeon.room_exists_at_coordinates(0, 0));
	}
	
	@Test
	void enter_room_at_coordinates_does_not_create_new_room_for_existant_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.no);
		DungeonRoom dungeon_room_before = create_and_return_room_at_coordinates(0, 0);
		
		dungeon.enter_room_at_coordinates(0, 0);
		DungeonRoom dungeon_room_after = dungeon.return_room_at_coordinates(0, 0);
		
		assertEquals(dungeon_room_before, dungeon_room_after);
	}
	
	@Test
	void ask_which_way_to_go_and_move_to_next_room_north_moves_to_correct_coordinate_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		int starting_coordinate_north = 1;
		int starting_coordinate_east = 1;
		start_at_room_with_coordinates(starting_coordinate_north, starting_coordinate_east);
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.north);
		DungeonRoom expected_dungeon_room = create_and_return_room_at_coordinates(starting_coordinate_north + 1, starting_coordinate_east);
		
		dungeon.ask_which_way_to_go_and_move_to_next_room();
		DungeonRoom actual_dungeon_room = dungeon.current_room;
		
		assertEquals(expected_dungeon_room, actual_dungeon_room);
	}
	
	@Test
	void ask_which_way_to_go_and_move_to_next_room_east_moves_to_correct_coordinate_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		int starting_coordinate_north = 1;
		int starting_coordinate_east = 1;
		start_at_room_with_coordinates(starting_coordinate_north, starting_coordinate_east);
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.east);
		DungeonRoom expected_dungeon_room = create_and_return_room_at_coordinates(starting_coordinate_north, starting_coordinate_east + 1);
		
		dungeon.ask_which_way_to_go_and_move_to_next_room();
		DungeonRoom actual_dungeon_room = dungeon.current_room;
		
		assertEquals(expected_dungeon_room, actual_dungeon_room);
	}
	
	@Test
	void ask_which_way_to_go_and_move_to_next_room_south_moves_to_correct_coordinate_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		int starting_coordinate_north = 1;
		int starting_coordinate_east = 1;
		start_at_room_with_coordinates(starting_coordinate_north, starting_coordinate_east);
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.south);
		DungeonRoom expected_dungeon_room = create_and_return_room_at_coordinates(starting_coordinate_north - 1, starting_coordinate_east);
		
		dungeon.ask_which_way_to_go_and_move_to_next_room();
		DungeonRoom actual_dungeon_room = dungeon.current_room;
		
		assertEquals(expected_dungeon_room, actual_dungeon_room);
	}
	
	@Test
	void ask_which_way_to_go_and_move_to_next_room_west_moves_to_correct_coordinate_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		int starting_coordinate_north = 1;
		int starting_coordinate_east = 1;
		start_at_room_with_coordinates(starting_coordinate_north, starting_coordinate_east);
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.west);
		DungeonRoom expected_dungeon_room = create_and_return_room_at_coordinates(starting_coordinate_north, starting_coordinate_east - 1);
		
		dungeon.ask_which_way_to_go_and_move_to_next_room();
		DungeonRoom actual_dungeon_room = dungeon.current_room;
		
		assertEquals(expected_dungeon_room, actual_dungeon_room);
	}
	
	@Test
	void ask_which_way_to_go_and_move_to_next_room_town_enters_town_test() throws ClientDisconnectedException {
		setup_test_dungeon();
		Town town = mock(Town.class);
		server_game_controller.town = town;
		int starting_coordinate_north = 0;
		int starting_coordinate_east = 0;
		start_at_room_with_coordinates(starting_coordinate_north, starting_coordinate_east);
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.town);
		
		dungeon.ask_which_way_to_go_and_move_to_next_room();
		
		verify(town, times(1)).enter();
	}

	private DungeonRoom create_and_return_room_at_coordinates(int coordinate_north, int coordinate_east) {
		dungeon.create_new_room_at_coordinates(coordinate_north, coordinate_east);
		DungeonRoom dungeon_room_before = dungeon.return_room_at_coordinates(coordinate_north, coordinate_east);
		return dungeon_room_before;
	}


	private void start_at_room_with_coordinates(int coordinate_north, int coordinate_east) {
		dungeon.create_new_room_at_coordinates(coordinate_north, coordinate_east);
		dungeon.current_room = dungeon.return_room_at_coordinates(coordinate_north, coordinate_east);
	}

	private void setup_test_dungeon() {
		server_messaging_system = mock(ServerMessagingSystem.class);
		server_messaging_system_test_utils = new Server_Messaging_System_Test_Utils(server_messaging_system);
		server_game_controller = mock(ServerGameController.class);
		server_game_controller.character = new Character();
		server_battle_controller = mock(ServerBattleController.class);
		dungeon = new Dungeon(server_game_controller, server_messaging_system, server_battle_controller);
	}

}
