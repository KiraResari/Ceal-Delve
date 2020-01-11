package server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import enemies.Enemy;
import enemies.EnemyZevi;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import messaging_system.QuestionOption;
import combatants.Character;
import elements.Elements;

class ServerBattleController_Test {
	
	ServerBattleController server_battle_controller;
	ServerMessagingSystem server_messaging_system;
	Character character;
	Enemy enemy;

	@Test
	void battle_end_calls_victory_when_character_is_victorious_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		server_battle_controller.victor = character;
		
		server_battle_controller.battle_end();
		
		verify(server_messaging_system, times(1)).send_message_to_client(enemy.defeat_narrative, false);
	}

	@Test
	void battle_end_calls_defeat_when_enemy_is_victorious_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		server_battle_controller.victor = enemy;
		
		server_battle_controller.battle_end();
		
		verify(server_messaging_system, times(1)).send_message_to_client(enemy.player_kill_narrative, false);
	}

	@Test
	void battle_end_calls_level_up_if_player_victorious_and_exp_are_sufficient_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		character.experience_current = character.experience_to_next_level;
		server_battle_controller.victor = character;
		
		server_battle_controller.battle_end();
		
		verify(server_messaging_system, times(1)).send_message_to_client("|| LEVEL UP! ||", true);
	}
	
	@Test
	void player_turn_attack_works_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.attack;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You attack the " + enemy.name + "!", false);
	}
	
	@Test
	void player_turn_attack_deals_correct_damage_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.attack;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		int expected_damage = server_battle_controller.calculate_attack_damage_dealt_by_player();
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("The " + enemy.name + " takes " + expected_damage + " damage.", false);
	}
	
	@Test
	void player_turn_attack_deals_no_damage_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.attack;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		enemy.defense = character.attack;
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("...but the " + enemy.name + " takes no damage.", false);
	}
	
	@Test
	void player_turn_iserialogy_works_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.iserialogy;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You invoke the Iserialogy " + character.element.name + " Blast!", false);
	}
	
	@Test
	void player_turn_iserialogy_deals_correct_damage_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.iserialogy;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		int expected_damage = server_battle_controller.calculate_iserialogy_damage_dealt_by_player();
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("The " + enemy.name + " takes " + expected_damage + " " + character.element.name + " damage.", false);
	}
	
	@Test
	void player_turn_iserialogy_deals_no_damage_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.iserialogy;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		enemy.defense = server_battle_controller.calculate_iserialogy_raw_strength();
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("...but the " + enemy.name + " takes no damage.", false);
	}
	
	@Test
	void player_turn_iserialogy_hits_weak_point_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.iserialogy;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.element = enemy.weakness;
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You hit the enemy's weak point!", false);
	}
	
	@Test
	void player_turn_defend_works_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.defend;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		
		server_battle_controller.player_turn();
		
		assertEquals(true, character.defending);
	}
	
	@Test
	void player_turn_healing_kykli_works_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.healing_kykli;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_life = 1;
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You use a Healing Kykli to heal yourself.", false);
	}
	
	@Test
	void player_turn_healing_kykli_unavailable_when_fully_healed_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		
		List<QuestionOption> question_options = server_battle_controller.get_player_turn_options();
		
		for(QuestionOption question_option : question_options) {
			if(question_option.hotkey == strings.Hotkeys.healing_kykli) {
				fail("Question options contained Healing Kykli while player's life was full");
			}
		}
	}
	
	@Test
	void player_turn_healing_kykli_unavailable_when_used_up_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		character.current_life = 1;
		character.healing_kykli_count = 0;
		//server_battle_controller.character.healing_kykli_count = character.healing_kykli_count;
		
		List<QuestionOption> question_options = server_battle_controller.get_player_turn_options();
		
		for(QuestionOption question_option : question_options) {
			if(question_option.hotkey == strings.Hotkeys.healing_kykli) {
				fail("Question options contained Healing Kykli while the palyer didn't have Healing Kykli");
			}
		}
	}
	
	@Test
	void player_turn_healing_kykli_heals_correct_amount_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.healing_kykli;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_life = 1;
		character.max_life = 100;
		int expected_character_life = character.current_life + server_battle_controller.healing_kykli_power;
		
		server_battle_controller.player_turn();
		
		assertEquals(expected_character_life, character.current_life);
	}
	
	@Test
	void player_turn_healing_kykli_uses_up_one_healing_kykli_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.healing_kykli;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_life = 1;
		int expected_healing_kykli_count = character.healing_kykli_count - 1;
		
		server_battle_controller.player_turn();
		
		assertEquals(expected_healing_kykli_count, character.healing_kykli_count);
	}
	
	@Test
	void player_turn_energy_water_works_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.energy_water;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_energy = 1;
		
		server_battle_controller.player_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You drink some Energy Water to restore your Energy.", false);
	}
	
	@Test
	void player_turn_energy_water_unavailable_when_energy_full_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		
		List<QuestionOption> question_options = server_battle_controller.get_player_turn_options();
		
		for(QuestionOption question_option : question_options) {
			if(question_option.hotkey == strings.Hotkeys.energy_water) {
				fail("Question options contained Energy Water while player's energy was full");
			}
		}
	}
	
	@Test
	void player_turn_heenergy_water_unavailable_when_used_up_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		character.energy_water_count = 0;
		
		List<QuestionOption> question_options = server_battle_controller.get_player_turn_options();
		
		for(QuestionOption question_option : question_options) {
			if(question_option.hotkey == strings.Hotkeys.energy_water) {
				fail("Question options contained Energy Water while the palyer didn't have any Energy Water");
			}
		}
	}
	
	@Test
	void player_turn_energy_water_heals_correct_amount_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.energy_water;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_energy = 1;
		character.max_energy = 100;
		int expected_character_life = character.current_energy + server_battle_controller.energy_water_power;
		
		server_battle_controller.player_turn();
		
		assertEquals(expected_character_life, character.current_energy);
	}
	
	@Test
	void player_turn_energy_water_uses_up_one_energy_water_test() throws ClientDisconnectedException {
		basic_test_battle_setup();
		String player_response = strings.Hotkeys.energy_water;
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
		character.current_energy = 1;
		int expected_energy_water_count = character.energy_water_count - 1;
		
		server_battle_controller.player_turn();
		
		assertEquals(expected_energy_water_count, character.energy_water_count);
	}
	
	@Test 
	void enemy_turn_deals_correct_damage_test() throws ClientDisconnectedException{
		basic_test_battle_setup();
		int expected_player_life = character.current_life - server_battle_controller.calculate_attack_damage_dealt_by_enemy();
		
		server_battle_controller.enemy_turn();
		
		assertEquals(expected_player_life, character.current_life);
	}
	
	@Test 
	void enemy_turn_deals_no_damage_test() throws ClientDisconnectedException{
		basic_test_battle_setup();
		character.defense = enemy.attack;
		
		server_battle_controller.enemy_turn();
		
		assertEquals(character.max_life, character.current_life);
	}
	
	@Test 
	void enemy_turn_deals_reduced_damage_while_defending_test() throws ClientDisconnectedException{
		basic_test_battle_setup();
		character.defending = true;
		int expected_damage = enemy.attack - character.defense;
		int expected_reduced_damage = server_battle_controller.calculate_damage_reduction_from_defending(expected_damage);
		
		server_battle_controller.enemy_turn();
		
		verify(server_messaging_system, times(1)).send_message_to_client("You take " + expected_reduced_damage + " less damage because you defended and regain " + expected_reduced_damage + " energy.", false);
	}
	
	@Test 
	void enemy_turn_resores_energy_while_defending_test() throws ClientDisconnectedException{
		basic_test_battle_setup();
		character.defending = true;
		character.current_energy = 0;
		int expected_damage = server_battle_controller.calculate_attack_damage_dealt_by_enemy();
		int expected_reduced_damage = server_battle_controller.calculate_damage_reduction_from_defending(expected_damage);
		int expected_character_energy = character.current_energy + expected_reduced_damage;
		
		server_battle_controller.enemy_turn();
		
		assertEquals(expected_character_energy, character.current_energy);
	}
	
	@Test
	void check_victor_none_test() {
		basic_test_battle_setup();
		
		server_battle_controller.check_victor();
		
		assertEquals(null, server_battle_controller.victor);
	}
	
	@Test
	void check_victor_character_test() {
		basic_test_battle_setup();
		enemy.current_life = 0;
		
		server_battle_controller.check_victor();
		
		assertEquals(character, server_battle_controller.victor);
	}
	
	@Test
	void check_victor_enemy_test() {
		basic_test_battle_setup();
		character.current_life = 0;
		
		server_battle_controller.check_victor();
		
		assertEquals(enemy, server_battle_controller.victor);
	}
	
	void basic_test_battle_setup() {
		server_messaging_system = mock(ServerMessagingSystem.class);
		server_battle_controller = new ServerBattleController(server_messaging_system);
		character = new Character();
		character.element = Elements.fauna;
		enemy = new EnemyZevi();
		server_battle_controller.character = character;
		server_battle_controller.enemy = enemy;
	}
}
