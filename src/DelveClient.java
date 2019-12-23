import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.text.AbstractDocument.BranchElement;


public class DelveClient {

	public static void main(String[] args) {
		Socket server = null;
		
		String server_address = "127.0.0.1";
		int server_port = 1337;
		
		try {
			//Connect to the server
			server = new Socket(server_address, server_port);
			
			//The server input stream
			InputStream server_input_stream = server.getInputStream();
			
			//The text that is being received from the server
			BufferedReader server_input = new BufferedReader(new InputStreamReader(server_input_stream));
			
			//The text that is sent to the server
			PrintWriter client_output = new PrintWriter(server.getOutputStream());
			
			//The user input
			BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
			
			//Await welcome message
			String text_buffer;
			while(server_input.ready()) {
				text_buffer = server_input.readLine();
				System.out.println(text_buffer);
			}
			
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

}
