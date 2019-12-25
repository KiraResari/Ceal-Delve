
public class ServerBattleController {
	public Character character;
	public Enemy enemy;
	public ServerMessagingSystem server_messaging_system;
	
	public ServerBattleController(ServerMessagingSystem server_messaging_system) {
		this.server_messaging_system = server_messaging_system;
	}
	
	public void battle_init(Character character, Enemy enemy){
		
	}
}
