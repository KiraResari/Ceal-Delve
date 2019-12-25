import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	public void send_message_to_client(String message, Boolean autoscroll) {
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
	}
	
	public Communication send_question_to_client(Question question) {
		Communication outgoing_communication = new Communication(CommunicationTypes.question, question);
		//Communication outgoing_communication = new Communication(CommunicationTypes.message, "Test question");
		try {
			object_output_to_client.writeObject(outgoing_communication);
			object_output_to_client.flush();
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
			if (question.validateReply(reply.message)){
				break;
			}
			else {
				send_message_to_client("Please pick from the provided options:", true);
				send_message_to_client(question.question_option_hotkeys.toString(), false);
			}
			reply = await_client_reply();
		}
		return reply;
	}
	
	//Awaits a reply from the client
	public Communication await_client_reply() {
		try {
			Communication comm = (Communication) object_input_from_client.readObject();
			String message = comm.message;
			
			System.out.println("Received message from client: " + message);
			return comm;
		}
		catch (java.net.SocketException e){
			System.out.println("Client disconnected");
			return null;
		}
		catch (Exception e){
			System.out.println("Runtime Error: " + e);
			e.printStackTrace();
			return null;
		}
	}
}