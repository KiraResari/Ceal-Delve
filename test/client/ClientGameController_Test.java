package client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import exceptions.ClientDisconnectedException;
import exceptions.ServerDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import messaging_system.Question;
import test_case_suite.Client_Object_Stream_Test_Utils;

class ClientGameController_Test {
	private ClientGameController client_game_controller;
	private Client_Object_Stream client_object_stream;
	private Client_Object_Stream_Test_Utils client_object_stream_test_utils;
	private Console console;

	@Test
	void await_server_communication_should_print_message_for_mcommunication_type_message_test() throws IOException, ServerDisconnectedException, ClassNotFoundException, ClientDisconnectedException {
		setup_client_game_controller_test_environment();
		String expected_message = "Arbitrary Test Message";
		client_object_stream_test_utils.prepare_single_answer_from_server(CommunicationTypes.message, expected_message);
		when(console.get_user_input_without_prompt()).thenReturn("");
		
		client_game_controller.await_server_communication();
		
		verify(console, times(1)).print(expected_message);
	}
	
	@Test
	void await_server_communication_should_print_autoscroll_message_for_communication_type_message_autoscroll_test() throws IOException, ServerDisconnectedException, ClassNotFoundException, ClientDisconnectedException {
		setup_client_game_controller_test_environment();
		String test_message = "Arbitrary Test Message";
		Communication reply_from_server_1 = new Communication(CommunicationTypes.message_autoscroll, test_message);
		Communication reply_from_server_2 = new Communication(CommunicationTypes.message, test_message);
		when(client_object_stream.get_communication_from_server())
			.thenReturn(reply_from_server_1)
			.thenReturn(reply_from_server_2);
		when(console.get_user_input_without_prompt()).thenReturn("");
		
		client_game_controller.await_server_communication();
		
		verify(console, times(1)).print(" »");
	}

	@Test
	void await_server_communication_should_get_user_input_for_communication_type_question_test() throws IOException, ServerDisconnectedException, ClassNotFoundException, ClientDisconnectedException {
		setup_client_game_controller_test_environment();
		String test_message = "Arbitrary Test Message";
		Question test_question = Question.construct_yes_no_question(test_message);
		client_object_stream_test_utils.prepare_single_answer_from_server(CommunicationTypes.question, test_question);
		when(console.get_user_input_with_prompt()).thenReturn(strings.Hotkeys.yes);
		
		client_game_controller.await_server_communication();
		
		verify(console, times(1)).get_user_input_with_prompt();
	}

	@Test
	void await_server_communication_should_get_user_input_for_communication_type_free_text_entry_test() throws IOException, ServerDisconnectedException, ClassNotFoundException, ClientDisconnectedException {
		setup_client_game_controller_test_environment();
		String test_message = "Arbitrary Test Message";
		client_object_stream_test_utils.prepare_single_answer_from_server(CommunicationTypes.free_text_entry, test_message);
		when(console.get_user_input_with_prompt()).thenReturn("Arbitrary Test Input");
		
		client_game_controller.await_server_communication();
		
		verify(console, times(1)).get_user_input_with_prompt();
	}

	@Test
	void await_server_communication_should_print_error_for_unimplemented_communication_type_test() throws IOException, ServerDisconnectedException, ClassNotFoundException, ClientDisconnectedException {
		setup_client_game_controller_test_environment();
		String test_message = "Arbitrary Test Message";
		String incoming_communication = "Random Not Implemented Communication Name";
		client_object_stream_test_utils.prepare_single_answer_from_server(incoming_communication, test_message);
		when(console.get_user_input_with_prompt()).thenReturn("Arbitrary Test Input");
		
		client_game_controller.await_server_communication();
		
		verify(console, times(1)).println("ERROR: Communication Type '" + incoming_communication + "' is not implemented in the client.");
	}
	
	private void setup_client_game_controller_test_environment() throws IOException {
		console = mock(Console.class);
		client_object_stream = mock(Client_Object_Stream.class);
		client_object_stream_test_utils = new Client_Object_Stream_Test_Utils(client_object_stream);
		client_game_controller = new ClientGameController(console, client_object_stream);
	}

}
