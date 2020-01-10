package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import messaging_system.Question;
import messaging_system.QuestionOption;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ServerMessagingSystem_Test {
	
	Server_Object_Stream server_object_stream;
	ServerMessagingSystem server_messaging_system;

	@Test
	void send_message_to_client_with_autoscroll_works_test() throws ClientDisconnectedException, IOException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String message = "Arbitrary test message";
		Boolean autoscroll_flag = true;  
		
		server_messaging_system.send_message_to_client(message, autoscroll_flag);
		
		verify(server_object_stream, times(1)).send_to_client(any());
	}

	@Test
	void send_message_to_client_without_autoscroll_works_test() throws ClientDisconnectedException, IOException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String message = "Arbitrary test message";
		Boolean autoscroll_flag = false;  
		
		server_messaging_system.send_message_to_client(message, autoscroll_flag);
		
		verify(server_object_stream, times(1)).send_to_client(any());
	}

	@Test
	void send_message_to_client_throws_client_disconnected_exception_test() throws ClientDisconnectedException, IOException {
		assertThrows(ClientDisconnectedException.class, () ->{
			server_object_stream = mock(Server_Object_Stream.class);
			server_messaging_system = new ServerMessagingSystem(server_object_stream);
			doThrow(new IOException("Arbitrary string")).when(server_object_stream).send_to_client(any());
			String message = "Arbitrary test message";
			Boolean autoscroll_flag = false;  
			
			server_messaging_system.send_message_to_client(message, autoscroll_flag);
		});
	}
	
	@Test
	void send_question_with_custom_options_to_client_returns_correct_reply_test() throws ClientDisconnectedException, ClassNotFoundException, IOException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String question_message = "Arbitrary question message";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		QuestionOption question_option_a = new QuestionOption("Option A", "A");
		QuestionOption question_option_b = new QuestionOption("Option B", "B");
		QuestionOption question_option_c = new QuestionOption("Option C", "C");
		question_options.add(question_option_a);
		question_options.add(question_option_b);
		question_options.add(question_option_c);
		Communication reply_from_client = new Communication(CommunicationTypes.message, question_option_b.hotkey);
		when(server_object_stream.get_reply_from_client()).thenReturn(reply_from_client);
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		
		Communication reply = server_messaging_system.send_question_to_client(question);
		
		assertEquals(question_option_b.hotkey, reply.message);
	}
	
	@Test
	void send_question_to_client_with_yes_no_returns_correct_reply_test() throws ClientDisconnectedException, ClassNotFoundException, IOException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String question_message = "Arbitrary question message";
		String mocked_user_input = "Y";
		Communication reply_from_client = new Communication(CommunicationTypes.message, mocked_user_input);
		when(server_object_stream.get_reply_from_client()).thenReturn(reply_from_client);
		Question question = Question.construct_yes_no_question(question_message);
		
		Communication reply = server_messaging_system.send_question_to_client(question);
		
		assertEquals(mocked_user_input, reply.message);
	}

	@Test
	void send_question_to_client_throws_client_disconnected_exception_while_sending_test() throws ClientDisconnectedException, IOException {
		assertThrows(ClientDisconnectedException.class, () ->{
			server_object_stream = mock(Server_Object_Stream.class);
			doThrow(new IOException("Arbitrary string")).when(server_object_stream).send_to_client(any());
			server_messaging_system = new ServerMessagingSystem(server_object_stream);
			String question_message = "Arbitrary question message";
			String mocked_user_input = "Y";
			Communication reply_from_client = new Communication(CommunicationTypes.message, mocked_user_input);
			when(server_object_stream.get_reply_from_client()).thenReturn(reply_from_client);
			Question question = Question.construct_yes_no_question(question_message);
			
			server_messaging_system.send_question_to_client(question);
		});
	}

	@Test
	void send_question_to_client_throws_client_disconnected_exception_while_receiving_test() throws ClientDisconnectedException, IOException {
		assertThrows(ClientDisconnectedException.class, () ->{
			server_object_stream = mock(Server_Object_Stream.class);
			server_messaging_system = new ServerMessagingSystem(server_object_stream);
			String question_message = "Arbitrary question message";
			doThrow(new NullPointerException("Arbitrary string")).when(server_object_stream).get_reply_from_client();
			Question question = Question.construct_yes_no_question(question_message);
			
			server_messaging_system.send_question_to_client(question);
		});
	}
	
	@Test
	void send_question_to_client_prompts_again_on_invalid_reply() throws ClientDisconnectedException, ClassNotFoundException, IOException {
		server_object_stream = mock(Server_Object_Stream.class);
		server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String question_message = "Arbitrary question message";
		String invalid_mocked_user_input = "Z";
		String valid_mocked_user_input = "Y";
		Communication invalid_reply_from_client = new Communication(CommunicationTypes.message, invalid_mocked_user_input);
		Communication valid_reply_from_client = new Communication(CommunicationTypes.message, valid_mocked_user_input);
		when(server_object_stream.get_reply_from_client()).thenReturn(invalid_reply_from_client).thenReturn(valid_reply_from_client);
		Question question = Question.construct_yes_no_question(question_message);
		
		server_messaging_system.send_question_to_client(question);
		
		verify(server_object_stream, times(3)).send_to_client(any());
	}

	@Test
	void send_free_text_entry_request_to_client_throws_client_disconnected_exception_while_sending_test() throws ClientDisconnectedException, IOException {
		assertThrows(ClientDisconnectedException.class, () ->{
			server_object_stream = mock(Server_Object_Stream.class);
			doThrow(new IOException("Arbitrary string")).when(server_object_stream).send_to_client(any());
			server_messaging_system = new ServerMessagingSystem(server_object_stream);
			String message = "Arbitrary message";
			
			server_messaging_system.send_free_text_entry_request_to_client(message);
		});
	}
}
