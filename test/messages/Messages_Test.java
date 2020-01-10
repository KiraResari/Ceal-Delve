package messages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import server.ServerMessagingSystem;
import server.Server_Object_Stream;
import server.Server_Object_Stream_Interface;
import static org.mockito.Mockito.*;

class Messages_Test {

	Server_Object_Stream server_object_stream;
	ServerMessagingSystem server_messaging_system;

	@Test
	void print_welcome_message_test() {
		server_object_stream = mock(Server_Object_Stream.class);
		ServerMessagingSystem server_messaging_system = new ServerMessagingSystem(server_object_stream);
		String version;
	}

}
