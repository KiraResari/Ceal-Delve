import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class DelveServer {

	public static void main(String[] args) {
		DelveServer s = new DelveServer();
		s.start_server();
	}
	
	public void start_server() {
		try {
			// Start Server
			ServerSocket server = new ServerSocket(1337);
			
			//Print IP Address
			System.out.println("Server started");
			InetAddress server_address = InetAddress.getLocalHost();
			String host_address = server_address.getHostAddress();
			System.out.println("Server is available under: " + host_address);
			
			//Run Server
			run_server(server);
		}
		catch (Exception e){
			System.out.println("Error Occurred: " + e);
			e.printStackTrace();
		}
		
	}
	
	public void run_server(ServerSocket server) throws IOException{
		while(true) {
			//Accept incoming connections
			Socket client = server.accept();
			
			//Record client Address
			String client_address = client.getInetAddress().getHostAddress() + client.getInetAddress().getHostName();
			System.out.println("Incoming client from: " + client_address);
			
			//Sends welcome message
			welcome_message(client);
			
			try {
				BufferedReader text_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter text_output = new PrintWriter(client.getOutputStream());
				
				String text_buffer;
				while((text_buffer = text_input.readLine()) != null) {
					text_buffer = "Re: " + text_buffer;
					text_output.println(text_buffer);
					text_output.flush();
					System.out.println("Sent message: " + text_buffer);
				}
			}
			catch (java.net.SocketException e){
				System.out.println("Client disconnected");
			}
			catch (Exception e){
				System.out.println("Runtime Error: " + e);
				e.printStackTrace();
			}
			
		}
	}
	
	public void welcome_message(Socket client) throws IOException{
		PrintWriter text_output = new PrintWriter(client.getOutputStream());
		String text_buffer;
		text_output.println("        //------------------------//");
		text_output.println("       // Welcome to...          //");
		text_output.println("      //                        //");
		text_output.println("     // THE CHRONICLES OF CEAL //");
		text_output.println("    //          ~             //");
		text_output.println("   //     DEEPER DELVING     //");
		text_output.println("  //                        //");
		text_output.println(" //         by Kira Resari //");
		text_output.println("//------------------------//");
		text_output.flush();
		System.out.println("Sent welcome message");
	}

}
