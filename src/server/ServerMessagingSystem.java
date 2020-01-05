package server;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import exceptions.ClientDisconnectedException;
import messaging_system.Communication;
import messaging_system.CommunicationTypes;
import messaging_system.Question;

public class ServerMessagingSystem {

	ObjectInputStream object_input_from_client;
	ObjectOutputStream object_output_to_client;
	
	public ServerMessagingSystem(Socket client) {
		try {
			object_output_to_client = new ObjectOutputStream(client.getOutputStream());
			object_input_from_client = new ObjectInputStream(client.getInputStream());
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	
	
	//Sends a message to the client
	public void send_message_to_client(String message, Boolean autoscroll) throws ClientDisconnectedException {
		String type;
		if(autoscroll) {
			type = CommunicationTypes.message_autoscroll;
		}
		else {
			type = CommunicationTypes.message;
		}
		
		Communication outgoing_communication = new Communication(type, message);
		
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent message to client: " + message);
		if(!autoscroll) {
			 await_client_reply();
		}
	}
	
	public Communication send_question_to_client(Question question) throws ClientDisconnectedException {
		Communication outgoing_communication = new Communication(CommunicationTypes.question, question);
		//Communication outgoing_communication = new Communication(CommunicationTypes.message, "Test question");
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
		} catch(SocketException e) {
			throw new ClientDisconnectedException("SocketException in send_question_to_client");
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent question to client: " + question.question_message);
		
		// Waits for a reply
		Communication reply = await_client_reply();
		
		while(true) {
			
			//Checks for a valid reply
			try {
				if (question.validateReply(reply.message)){
					break;
				}
				else {
					send_message_to_client("Please pick from the provided options:", true);
					reply = send_free_text_entry_request_to_client(question.question_option_hotkeys.toString());
				}
			}catch(NullPointerException e) {
				throw new ClientDisconnectedException("Null Pointer Exception in send_question_to_client");
			}
		}
		return reply;
	}
	
	//Sends a message to the client
	public Communication send_free_text_entry_request_to_client(String message) throws ClientDisconnectedException {
		String type = CommunicationTypes.free_text_entry;
		
		Communication outgoing_communication = new Communication(type, message);
		
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
		} 
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		System.out.println("Sent free text entry request to client: " + message);
		
		Communication reply = await_client_reply();
		return reply;
	}
	
	//Awaits a reply from the client
	public Communication await_client_reply() throws ClientDisconnectedException {
		try {
			Communication comm = (Communication) object_input_from_client.readObject();
			String message = comm.message;
			
			System.out.println("Received message from client: " + message);
			return comm;
		}
		catch (java.net.SocketException | EOFException e){
			throw new ClientDisconnectedException("Occurred in: await_client_reply");
		}
		catch (Exception e){
			System.out.println("Runtime Error: " + e);
			e.printStackTrace();
			return null;
		}
	}


	public void print_game_over_message() throws ClientDisconnectedException {
		send_message_to_client("//===============\\\\", true);
		send_message_to_client("||               ||", true);
		send_message_to_client("||   GAME OVER   ||", true);
		send_message_to_client("||               ||", true);
		send_message_to_client("\\\\===============//", true);
	}
}