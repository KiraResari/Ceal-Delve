package server;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.Question;

public interface Server_Messaging_System_Interface {
	public void send_message_to_client(String message, Boolean autoscroll) throws ClientDisconnectedException;
	public Communication send_question_to_client(Question question) throws ClientDisconnectedException;
	public Communication send_free_text_entry_request_to_client(String message) throws ClientDisconnectedException;
	
}
