import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;


public class DelveClient {
	
	String text_buffer;
	Socket server;
	String server_address = "127.0.0.1";
	int server_port = 1337;
	
	InputStream server_input_stream;
	BufferedReader server_input;
	PrintWriter client_output;
	BufferedReader user_input;
	
	public static void main(String[] args) {
		DelveClient s = new DelveClient();
		s.start_client();
		
	}

	public void start_client() {
		server = null;
		
		try {
			//Connect to the server
			server = new Socket(server_address, server_port);
			
			//The server input stream
			server_input_stream = server.getInputStream();
			
			//The text that is being received from the server
			server_input = new BufferedReader(new InputStreamReader(server_input_stream));
			
			//The text that is sent to the server
			client_output = new PrintWriter(server.getOutputStream());
			
			//The user input
			user_input = new BufferedReader(new InputStreamReader(System.in));
			
			//Await welcome message
			await_server_message_single();
			send_over();
			
			//Await Name entry
			await_server_message_single();
			get_user_input();
			await_server_message_single();
			
			
			while(true) {
				//Get user input
				System.out.println("What do you want to do? ");
				text_buffer = user_input.readLine();
				
				//Send input to server
				client_output.println(text_buffer);
				client_output.flush();
				
				//Await server answer and print it
				System.out.println(server_input.readLine());
			}
			
		}
		
		catch (java.net.ConnectException e){
			System.out.println("ERROR: Delve Server does not appear to be running; Attempted to connect to Server: " + server_address + " Port: " + server_port);
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}

	}
	
	//This function sends an "over"-message to the server, signaling that the client is ready for the next request
	public void send_over() {
		client_output.println("");
		client_output.flush();
	}
	
	//This function awaits a single message from the server and prints it
	public void await_server_message_single() throws IOException, InterruptedException {
		while(!server_input.ready()) {
			Thread.sleep(100);
		}
		while(server_input.ready()) {
			text_buffer = server_input.readLine();
			System.out.println(text_buffer);
		}
	}
	
	//This function awaits multiple messages from the server and prints them until an <OVER> message is sent
	public void await_server_message_multi() throws IOException, InterruptedException {
		while(!server_input.ready()) {
			Thread.sleep(100);
		}
		while(server_input.ready()) {
			text_buffer = server_input.readLine();
			System.out.println(text_buffer);
		}
	}
	
	//Requests user input and sends it to the server
	public void get_user_input() {
		try {
			//Get user input
			System.out.print("> ");
			text_buffer = user_input.readLine();
			
			//Send input to server
			client_output.println(text_buffer);
			client_output.flush();
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
	}

}
