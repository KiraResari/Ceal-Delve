package client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class DelveClient_Test {
	
	Console console;
	DelveClient delve_client;

	@Test
	void ask_server_type_uses_local_server_for_reply_local_test() throws IOException {
		setup_client_test_environment();
		when(console.get_user_input()).thenReturn(strings.Hotkeys.local_machine);
		String expected_server_address = "127.0.0.1";
		
		delve_client.ask_server_type();
		
		assertEquals(expected_server_address, delve_client.server_address);
	}

	@Test
	void ask_server_type_uses_network_server_for_reply_network_test() throws IOException {
		setup_client_test_environment();
		String expected_server_address = "999.999.999.999";
		when(console.get_user_input())
			.thenReturn(strings.Hotkeys.network)
			.thenReturn(expected_server_address);
		
		delve_client.ask_server_type();
		
		assertEquals(expected_server_address, delve_client.server_address);
	}

	@Test
	void ask_change_server_type_question_should_return_true_for_yes_test() throws IOException {
		setup_client_test_environment();
		when(console.get_user_input()).thenReturn(strings.Hotkeys.yes);
		
		Boolean reply = delve_client.ask_change_server_type_question();
		
		assertTrue(reply);
	}

	@Test
	void ask_change_server_type_question_should_return_false_for_no_test() throws IOException {
		setup_client_test_environment();
		when(console.get_user_input()).thenReturn(strings.Hotkeys.no);
		
		Boolean reply = delve_client.ask_change_server_type_question();
		
		assertFalse(reply);
	}
	
	void setup_client_test_environment() {
		console = mock(Console.class);
		delve_client = new DelveClient();
		delve_client.console = console;
	}

}
