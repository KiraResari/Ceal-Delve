import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.text.AbstractDocument.BranchElement;


public class DelveClient {

	public static void main(String[] args) {
		Socket server = null;
		
		try {
			// Connect to the server
			server = new Socket("127.0.0.1", 1337);
			
			// The text that is being received from the server
			BufferedReader server_input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			// The text that is sent to the server
			PrintWriter client_output = new PrintWriter(server.getOutputStream());
			
			// The user input
			BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
			
			String text_buffer;
			while(true) {
				// Get user input
				System.out.println("What do you want to do? ");
				text_buffer = user_input.readLine();
				
				// Send input to server
				client_output.println(text_buffer);
				client_output.flush();
				
				// Await server answer and print it
				System.out.println(server_input.readLine());
			}
			
			
		}
		
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}

	}

}
