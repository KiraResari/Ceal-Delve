package test_case_suite;

import static org.mockito.Mockito.when;

import java.io.IOException;

import client.Client_Object_Stream;
import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;

public class Client_Object_Stream_Test_Utils {
	public Client_Object_Stream client_object_stream;
	
	public Client_Object_Stream_Test_Utils(Client_Object_Stream client_object_stream) {
		this.client_object_stream = client_object_stream;
	}
		
	public void prepare_single_answer_from_server(String server_message_type, String server_message) throws ClientDisconnectedException, ClassNotFoundException, IOException {
		Communication reply_from_server = new Communication(server_message_type, server_message);
		when(client_object_stream.get_communication_from_server()).thenReturn(reply_from_server);
	}

	public void prepare_single_answer_from_server(String server_message_type, Question test_question) throws ClassNotFoundException, IOException {
		Communication reply_from_server = new Communication(server_message_type, test_question);
		when(client_object_stream.get_communication_from_server()).thenReturn(reply_from_server);
	}
}