package messages;

import java.io.IOException;

import enemies.Enemy;
import exceptions.ClientDisconnectedException;
import server.ServerMessagingSystem;

public class Messages {
	
	public static void print_welcome_message(ServerMessagingSystem server_messaging_system, String version) throws IOException, ClientDisconnectedException{
		server_messaging_system.send_message_to_client("        //------------------------//", true);
		server_messaging_system.send_message_to_client("       // Welcome to...          //", true);
		server_messaging_system.send_message_to_client("      //                        //", true);
		server_messaging_system.send_message_to_client("     // THE CHRONICLES OF CEAL //", true);
		server_messaging_system.send_message_to_client("    //          ~             //", true);
		server_messaging_system.send_message_to_client("   //     DEEPER DELVING     //", true);
		server_messaging_system.send_message_to_client("  //                        //", true);
		server_messaging_system.send_message_to_client(" //         by Kira Resari //", true);
		server_messaging_system.send_message_to_client("//------------------------//", true);
		server_messaging_system.send_message_to_client("Server Version " + version, true);
		server_messaging_system.send_message_to_client("", true);
		System.out.println("Sent welcome message");
	}


	public static void print_game_over_message(ServerMessagingSystem server_messaging_system) throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client("//===============\\\\", true);
		server_messaging_system.send_message_to_client("||               ||", true);
		server_messaging_system.send_message_to_client("||   GAME OVER   ||", true);
		server_messaging_system.send_message_to_client("||               ||", true);
		server_messaging_system.send_message_to_client("\\\\===============//", true);
	}
	
	public static void print_battle_init_message(ServerMessagingSystem server_messaging_system, Enemy enemy) throws ClientDisconnectedException{
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client("|=================|", true);
		server_messaging_system.send_message_to_client("|| BATTLE START! ||", true);
		server_messaging_system.send_message_to_client("|=================|", true);
		server_messaging_system.send_message_to_client(" ", true);
		server_messaging_system.send_message_to_client(enemy.entry_narrative, false);
		server_messaging_system.send_message_to_client(" ", true);
	}

	public static void print_enter_town_message(ServerMessagingSystem server_messaging_system) throws ClientDisconnectedException {
		server_messaging_system.send_message_to_client(strings.Town.title_bars, true);
		server_messaging_system.send_message_to_client(strings.Town.title, true);
		server_messaging_system.send_message_to_client(strings.Town.title_bars, true);
		server_messaging_system.send_message_to_client("", true);
	}
}
