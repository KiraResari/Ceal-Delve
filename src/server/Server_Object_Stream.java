package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messaging_system.Communication;

public class Server_Object_Stream implements Server_Object_Stream_Interface {
	public ObjectInputStream object_input_from_client;
	public ObjectOutputStream object_output_to_client;

	public Server_Object_Stream(Socket client) throws IOException {
		object_output_to_client = new ObjectOutputStream(client.getOutputStream());
		object_input_from_client = new ObjectInputStream(client.getInputStream());
	}
	
	public void send_to_client(Communication outgoing_communication) throws IOException {
		object_output_to_client.writeObject(outgoing_communication);
		object_output_to_client.flush();
	}
	
	public Communication get_reply_from_client() throws IOException, ClassNotFoundException {
		return (Communication) object_input_from_client.readObject();
	}
}