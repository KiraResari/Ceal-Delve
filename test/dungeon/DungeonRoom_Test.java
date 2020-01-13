package dungeon;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import combatants.Character;
import enemies.EnemyHamster;
import exceptions.ClientDisconnectedException;
import server.ServerBattleController;
import server.ServerGameController;
import server.ServerMessagingSystem;
import test_case_suite.Server_Messaging_System_Test_Utils;

class DungeonRoom_Test {
	
	private ServerBattleController server_battle_controller;
	private ServerMessagingSystem server_messaging_system;
	private ServerGameController server_game_controller;
	private DungeonRoom dungeon_room;
	private Server_Messaging_System_Test_Utils server_messaging_system_test_utils;

	@Test
	void determine_town_access_returns_true_if_starting_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		
		dungeon_room.determine_town_access();
		
		assertTrue(dungeon_room.town_access_present);
	}

	@Test
	void determine_town_access_returns_false_if_one_north_of_starting_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north + 1, server_game_controller.starting_coordinate_east);
		
		dungeon_room.determine_town_access();
		
		assertFalse(dungeon_room.town_access_present);
	}

	@Test
	void determine_town_access_returns_false_if_one_east_of_starting_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east + 1);
		
		dungeon_room.determine_town_access();
		
		assertFalse(dungeon_room.town_access_present);
	}

	@Test
	void determine_town_access_returns_false_if_one_northeast_of_starting_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north + 1, server_game_controller.starting_coordinate_east + 1);
		
		dungeon_room.determine_town_access();
		
		assertFalse(dungeon_room.town_access_present);
	}

	@Test
	void enter_room_prints_visited_before_message_if_visited_before_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.visited_before = true;
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(1)).send_message_to_client(strings.Dungeon_Exploration_Strings.visited_before, false);
	}

	@Test
	void enter_room_prints_nothing_here_message_if_empty_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(1)).send_message_to_client(strings.Dungeon_Exploration_Strings.empty_cave, false);
	}

	@Test
	void enter_room_triggers_battle_if_enemy_present_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 1;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(1)).battle(any(), any());
	}

	@Test
	void enter_room_triggers_looting_if_treasure_present_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.yes);
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(1)).send_message_to_client(strings.Dungeon_Exploration_Strings.treasure_chest_open, false);
	}

	@Test
	void enter_room_does_not_triggers_looting_if_treasure_present_but_character_life_zero_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		dungeon_room.character.current_life = 0;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.yes);
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(0)).send_message_to_client(strings.Dungeon_Exploration_Strings.treasure_chest_open, false);
	}

	@Test
	void enter_room_prints_town_access_message_if_starting_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(1)).send_message_to_client(strings.Dungeon_Exploration_Strings.town_access_starting_room, false);
	}

	@Test
	void enter_room_does_not_print_town_access_message_if_not_starting_or_portal_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north + 1, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_messaging_system, times(0)).send_message_to_client(strings.Dungeon_Exploration_Strings.town_access_starting_room, false);
	}

	@Test
	void enter_room_tresure_looting_declining_leaves_chest_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.no);
		
		dungeon_room.enter_room();
		
		assertTrue(dungeon_room.treasure_present);
	}

	@Test
	void enter_room_get_non_trap_treasure_increases_µ_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		dungeon_room.treasure_chest_is_trapped_chance = 0;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.yes);
		int µ_before = dungeon_room.character.µ;
		
		dungeon_room.enter_room();
		int µ_after = dungeon_room.character.µ;
		
		assertTrue(µ_after > µ_before);
	}

	@Test
	void enter_room_get_trap_treasure_causes_expected_damage_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		dungeon_room.treasure_chest_is_trapped_chance = 1;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.yes);
		int expected_damage = dungeon_room.treasure_chest_explosion_base_damage + dungeon_room.depth - dungeon_room.character.defense;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(1)).character_take_damage(expected_damage);
	}

	@Test
	void enter_room_get_trap_treasure_character_survivies_with_one_life_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 1;
		dungeon_room.treasure_chest_is_trapped_chance = 1;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.yes);
		dungeon_room.character.current_life = 2;
		
		dungeon_room.enter_room();
		
		assertEquals(1, dungeon_room.character.current_life);
	}
	
	@Test
	void dungeon_room_character_equals_game_controller_character_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
	
		assertEquals(dungeon_room.character, server_game_controller.character);
	}

	@Test
	void enter_room_triggers_hamster_battle_if_hamster_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = dungeon_room.hamster_coordinates_seed;
		dungeon_room.coordinate_east = dungeon_room.hamster_coordinates_seed;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.no);
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(1)).battle(any(Character.class), any(EnemyHamster.class));
	}

	@Test
	void enter_room_hamster_room_always_has_treasure_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = dungeon_room.hamster_coordinates_seed;
		dungeon_room.coordinate_east = dungeon_room.hamster_coordinates_seed;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		server_messaging_system_test_utils.prepare_single_answer_from_client(strings.Hotkeys.no);
		
		dungeon_room.enter_room();
		
		assertTrue(dungeon_room.treasure_present);
	}

	@Test
	void enter_room_ten_zero_is_not_hamster_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = dungeon_room.hamster_coordinates_seed;
		dungeon_room.coordinate_east = 0;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(0)).battle(any(Character.class), any(EnemyHamster.class));
	}

	@Test
	void enter_room_zero_ten_is_not_hamster_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = 0;
		dungeon_room.coordinate_east = dungeon_room.hamster_coordinates_seed;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(0)).battle(any(Character.class), any(EnemyHamster.class));
	}

	@Test
	void enter_room_twenty_ten_is_not_hamster_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = dungeon_room.hamster_coordinates_seed * 2;
		dungeon_room.coordinate_east = dungeon_room.hamster_coordinates_seed;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(0)).battle(any(Character.class), any(EnemyHamster.class));
	}

	@Test
	void enter_room_zero_tero_is_not_hamster_room_test() throws ClientDisconnectedException {
		setup_dungeon_room_test_environment();
		dungeon_room = create_and_return_test_dungeon_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
		dungeon_room.coordinate_north = 0;
		dungeon_room.coordinate_east = 0;
		dungeon_room.enemy_chance = 0;
		dungeon_room.treasure_chance = 0;
		
		dungeon_room.enter_room();
		
		verify(server_battle_controller, times(0)).battle(any(Character.class), any(EnemyHamster.class));
	}
	
	void setup_dungeon_room_test_environment() throws ClientDisconnectedException {
		server_messaging_system = mock(ServerMessagingSystem.class);
		server_messaging_system_test_utils = new Server_Messaging_System_Test_Utils(server_messaging_system);
		server_game_controller = mock(ServerGameController.class);
		server_game_controller.character = new Character();
		server_game_controller.starting_coordinate_east = 0;
		server_game_controller.starting_coordinate_north = 0;
		server_battle_controller = mock(ServerBattleController.class);
		doAnswer(new Answer<Void>() {
		    public Void answer(InvocationOnMock invocation) {
		      Object[] args = invocation.getArguments();
		      int damage = (int) args[0];
		      server_game_controller.character.current_life -= damage;
		      return null;
		    }
		}).when(server_battle_controller).character_take_damage(anyInt());
	}
	
	DungeonRoom create_and_return_test_dungeon_room_at_coordinates(int coordinate_north, int coordinate_east) {
		return new DungeonRoom(coordinate_north, coordinate_east, server_game_controller, server_messaging_system, server_battle_controller);
	}

}
