package test_case_suite;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import server.ServerMessagingSystem;

public class Server_Messaging_System_Test_Utils {
	public ServerMessagingSystem server_messaging_system;
	
	public Server_Messaging_System_Test_Utils(ServerMessagingSystem server_messaging_system) {
		this.server_messaging_system = server_messaging_system;
	}
		
	public void prepare_single_answer_from_client(String player_response) throws ClientDisconnectedException {
		Communication reply_from_client_player_response = new Communication(CommunicationTypes.message, player_response);
		when(server_messaging_system.send_question_to_client(any())).thenReturn(reply_from_client_player_response);
	}

}