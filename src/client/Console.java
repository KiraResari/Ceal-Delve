package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
	BufferedReader user_input;
	
	public Console() {
		user_input = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void println(String message) {
		System.out.println(message);
	}
	
	public void print(String message) {
		System.out.print(message);
	}

	public String get_user_input() throws IOException {
		print("> ");
		return user_input.readLine();
	}
}
