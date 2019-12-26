import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DelveClient {
	
	String text_buffer;
	Socket server_connection;
	String server_address = "127.0.0.1";
	int server_port = 1337;
	String version = "0.8";
	
	BufferedReader server_input;
	BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
	ObjectInputStream object_input_from_server;
	ObjectOutputStream object_output_to_server;
	
	public static void main(String[] args) {
		DelveClient s = new DelveClient();
		s.start_client();
	}

	public void start_client() {
		server_connection = null;
		try {
			ask_server_type();
			while(true) {
				try {
					server_connection = new Socket(server_address, server_port);
					ClientGameController game_controller = new ClientGameController(server_connection);
					System.out.println("Client Version " + version);
					game_controller.game_init();
				}
				catch (java.net.ConnectException e){
					System.out.println("ERROR: Delve Server does not appear to be running; Attempted to connect to Server: " + server_address + " Port: " + server_port);
					if(ask_change_server_type_question()) {
						ask_server_type();
					}
				}
				catch (java.net.SocketException e){
					System.out.println("ERROR: Disconnected from server. The Delve Server might have crashed.");
					if(ask_change_server_type_question()) {
						ask_server_type();
					}
				}
			}
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}
	
	public void ask_server_type() throws IOException {
		String reply;
		String question_message = "Is the Delve Server you want to connect to running on your local machine or the network?";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Local Machine", "L"));
		question_options.add(new QuestionOption("Network", "N"));
		Question question = new Question(question_message, question_options);
		
		question.print_question();
		
		reply = question.request_and_validate_local_user_reply(user_input);
		
		if(reply.equals("N")) {
			ask_server_address();
		}
	}
	
	public void ask_server_address() throws IOException {
		System.out.println("Please enter the server IP address");
		System.out.print("> ");
		server_address = user_input.readLine();
	}
	
	public Boolean ask_change_server_type_question() throws IOException {
		String reply;
		String question_message = "Do you want to try and connect to a different server?";
		Question question = new Question(question_message);
		
		question.print_question();
		
		reply = question.request_and_validate_local_user_reply(user_input);
		
		if(reply.equals("Y")) {
			return true;
		}
		return false;
	}
}
