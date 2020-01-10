package server;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import combatants.Character;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import town.Town;

class ServerGameController_Test {
	
	Server_Object_Stream server_object_stream;
	ServerMessagingSystem server_messaging_system;
	ServerGameController server_game_controller;
	Character character;
	static String version = "Test";

	@Test
	void initialize_town_works_test() {
		character = new Character();
		server_object_stream = mock(Server_Object_Stream.class);
		server_game_controller = new ServerGameController(server_object_stream, version);
		
		server_game_controller.initialize_town();
		
		assertNotNull("Town object did not exist", server_game_controller.town);
	}

	@Test
	void character_creation_saves_name_correctly_test() throws ClassNotFoundException, IOException, ClientDisconnectedException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_game_controller = new ServerGameController(server_object_stream, version);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String character_name_reply = "Sylvia Zerin";
		String blank_reply = "";
		String character_element_reply = "9";
		String character_element_confirm_reply = "Y";
		Communication reply_from_client_name = new Communication(CommunicationTypes.message, character_name_reply);
		Communication reply_from_client_blank = new Communication(CommunicationTypes.message, blank_reply);
		Communication reply_from_client_element = new Communication(CommunicationTypes.message, character_element_reply);
		Communication reply_from_client_element_confirm = new Communication(CommunicationTypes.message, character_element_confirm_reply);
		when(server_object_stream.get_reply_from_client())
		.thenReturn(reply_from_client_name)
		.thenReturn(reply_from_client_blank)
		.thenReturn(reply_from_client_blank)
		.thenReturn(reply_from_client_element)
		.thenReturn(reply_from_client_element_confirm);
		
		server_game_controller.character_creation();
		
		assertEquals(character_name_reply, server_game_controller.character.name);
	}
}
