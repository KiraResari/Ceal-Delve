package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messaging_system.Communication;
import messaging_system.CommunicationTypes;

public class Client_Object_Stream {
	private ObjectInputStream object_input_from_server;
	private ObjectOutputStream object_output_to_server;
	
	public Client_Object_Stream(Socket server) throws IOException {
		object_output_to_server = new ObjectOutputStream(server.getOutputStream());
		object_input_from_server = new ObjectInputStream(server.getInputStream());
	}

	public void send_message_to_server(ClientGameController clientGameController, String user_input) throws IOException {
		Communication comm = new Communication(CommunicationTypes.message, user_input);
		object_output_to_server.writeObject(comm);
		object_output_to_server.flush();
	}

	public Communication get_communication_from_server() throws ClassNotFoundException, IOException {
		return (Communication) object_input_from_server.readObject();
	}
}
