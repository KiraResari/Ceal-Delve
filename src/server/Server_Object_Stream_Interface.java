package server;

import java.io.IOException;

import messaging_system.Communication;

public interface Server_Object_Stream_Interface {
	public void send_to_client(Communication outgoing_communication) throws IOException;
	public Communication get_reply_from_client() throws IOException, ClassNotFoundException;
}