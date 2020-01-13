package town;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import combatants.Character;
import combatants.CharacterOperations;
import dungeon.Dungeon;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import messaging_system.Question;
import messaging_system.QuestionOption;
import server.ServerGameController;
import server.ServerMessagingSystem;

class Town_Test {
	
	ServerMessagingSystem server_messaging_system;
	ServerGameController server_game_controller;
	Character character;
	Town town;

	@Test
	void ask_what_to_do_works_for_cave_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		String player_response = strings.Hotkeys.cave;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		Communication reply = town.ask_what_to_do();
		
		assertEquals(player_response, reply.message);
	}

	@Test
	void build_activity_question_adds_rest_option_if_character_is_not_fully_healed_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_life = 1;
		
		Question question = town.build_activity_question();
		
		for(QuestionOption question_option : question.question_options) {
			if(question_option.hotkey == strings.Hotkeys.rest) {
				return;
			}
		}
		fail("Activity options did not contain 'Rest' even though player was not fully healed");
	}

	@Test
	void build_activity_question_does_not_add_rest_option_if_character_is_fully_healed_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		
		Question question = town.build_activity_question();
		
		for(QuestionOption question_option : question.question_options) {
			if(question_option.hotkey == strings.Hotkeys.rest) {
				fail("Activity options did contain 'Rest' even though player was fully healed");
			}
		}
	}
	
	@Test
	void perform_activity_cave_calls_dungeon_enter_room_at_coordinates_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		server_game_controller.starting_coordinate_north = 0;
		server_game_controller.starting_coordinate_east = 0;
		Dungeon dungeon =   mock(Dungeon.class);
		server_game_controller.dungeon = dungeon;
		String player_response = strings.Hotkeys.cave;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		
		town.perform_activity(reply_from_client_player_response);
		
		verify(dungeon, times(1)).enter_room_at_coordinates(server_game_controller.starting_coordinate_north, server_game_controller.starting_coordinate_east);
	}
	
	@Test
	void rest_restores_life_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_life = 1;
		character.µ = CharacterOperations.return_missing_life(character);
		String player_response = strings.Hotkeys.yes;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.rest();
		
		assertEquals(character.max_life, character.current_life);
	}
	
	@Test
	void rest_restores_energy_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_energy = 0;
		character.µ = CharacterOperations.return_missing_energy(character);
		String player_response = strings.Hotkeys.yes;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.rest();
		
		assertEquals(character.max_energy, character.current_energy);
	}
	
	@Test
	void rest_subtracts_µ_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_energy = 0;
		character.µ = CharacterOperations.return_missing_energy(character);
		String player_response = strings.Hotkeys.yes;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.rest();
		
		assertEquals(0, character.µ);
	}
	
	@Test
	void rest_decline_does_not_heal_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_energy = 0;
		character.µ = CharacterOperations.return_missing_energy(character);
		String player_response = strings.Hotkeys.no;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.rest();
		
		assertEquals(0, character.current_energy);
	}
	
	@Test
	void rest_cant_afford_with_insufficient_µ_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.current_energy = 0;
		String player_response = "";
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.rest();
		
		assertEquals(0, character.current_energy);
	}

	@Test
	void buy_healing_kykli_should_increase_healing_kykli_count_by_one_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.µ = Town.healing_kykli_cost;
		int expected_healing_kykli_count = character.healing_kykli_count + 1;
		String player_response = strings.Hotkeys.yes;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.buy_healing_kykli();
		
		assertEquals(expected_healing_kykli_count, character.healing_kykli_count);
	}

	@Test
	void buy_healing_kykli_should_subtract_µ_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.µ = Town.healing_kykli_cost;
		int expected_µ = character.µ - Town.healing_kykli_cost;
		String player_response = strings.Hotkeys.yes;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.buy_healing_kykli();
		
		assertEquals(expected_µ, character.µ);
	}

	@Test
	void buy_healing_kykli_declining_should_not_subtract_µ_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		character.µ = Town.healing_kykli_cost;
		int expected_µ = Town.healing_kykli_cost;
		String player_response = strings.Hotkeys.no;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.buy_healing_kykli();
		
		assertEquals(expected_µ, character.µ);
	}

	@Test
	void buy_healing_kykli_cant_afford_should_not_increase_healing_kykli_count_test() throws ClientDisconnectedException {
		basic_town_test_setup();
		int expected_healing_kykli_count = character.healing_kykli_count;
		String player_response = strings.Hotkeys.no;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		town.buy_healing_kykli();
		
		assertEquals(expected_healing_kykli_count, character.healing_kykli_count);
	}
	
	void basic_town_test_setup() {
		server_messaging_system = mock(ServerMessagingSystem.class);
		server_game_controller = mock(ServerGameController.class);
		character = new Character();
		town = new Town(server_game_controller, server_messaging_system, character);
	}
}
