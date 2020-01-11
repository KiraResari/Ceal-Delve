package messages;

import org.junit.jupiter.api.Test;

import enemies.Enemy;
import enemies.EnemyZevi;
import exceptions.ClientDisconnectedException;
import server.ServerMessagingSystem;
import static org.mockito.Mockito.*;

import java.io.IOException;

class Messages_Test {

	ServerMessagingSystem server_messaging_system;

	@Test
	void print_welcome_message_is_called_test() throws IOException, ClientDisconnectedException {
		server_messaging_system = mock(ServerMessagingSystem.class);
		String version = "Test";

		Messages.print_welcome_message(server_messaging_system, version);
	
		verify(server_messaging_system, times(1)).send_message_to_client("        //------------------------//", true);
		verify(server_messaging_system, times(1)).send_message_to_client("       // Welcome to...          //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("      //                        //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("     // THE CHRONICLES OF CEAL //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("    //          ~             //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("   //     DEEPER DELVING     //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("  //                        //", true);
		verify(server_messaging_system, times(1)).send_message_to_client(" //         by Kira Resari //", true);
		verify(server_messaging_system, times(1)).send_message_to_client("//------------------------//", true);
		verify(server_messaging_system, times(1)).send_message_to_client("Server Version " + version, true);
		verify(server_messaging_system, times(1)).send_message_to_client("", true);
	}

	@Test
	void print_game_over_message_is_called_test() throws ClientDisconnectedException {
		server_messaging_system = mock(ServerMessagingSystem.class);

		Messages.print_game_over_message(server_messaging_system);
	
		verify(server_messaging_system, times(1)).send_message_to_client("//===============\\\\", true);
		verify(server_messaging_system, times(2)).send_message_to_client("||               ||", true);
		verify(server_messaging_system, times(1)).send_message_to_client("||   GAME OVER   ||", true);
		verify(server_messaging_system, times(1)).send_message_to_client("\\\\===============//", true);
	}

	@Test
	void print_battle_init_message_is_called_test() throws ClientDisconnectedException {
		server_messaging_system = mock(ServerMessagingSystem.class);
		Enemy enemy = new EnemyZevi();

		Messages.print_battle_init_message(server_messaging_system, enemy);
	
		verify(server_messaging_system, times(3)).send_message_to_client(" ", true);
		verify(server_messaging_system, times(2)).send_message_to_client("|=================|", true);
		verify(server_messaging_system, times(1)).send_message_to_client("|| BATTLE START! ||", true);
		verify(server_messaging_system, times(1)).send_message_to_client(enemy.entry_narrative, false);
	}

	@Test
	void print_enter_town_message_is_called_test() throws ClientDisconnectedException {
		server_messaging_system = mock(ServerMessagingSystem.class);

		Messages.print_enter_town_message(server_messaging_system);
	
		verify(server_messaging_system, times(1)).send_message_to_client(strings.Town.title, true);
		verify(server_messaging_system, times(2)).send_message_to_client(strings.Town.title_bars, true);
		verify(server_messaging_system, times(1)).send_message_to_client("", true);
	}
}
