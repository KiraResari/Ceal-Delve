package enemies;
import elements.Elements;

public class EnemyXervo extends Enemy {
	public EnemyXervo() {
		name = "Xervo";
		level = 11;
		entry_narrative = "A majestic Xervo challenges you.";
		attack_narrative = "The Xervo kicks you with its mighty paws.";
		defeat_narrative = "You triumphed over the Xervo.";
		player_kill_narrative = "You were no match for the Xervo, and paid for it with your life.";
		weakness = Elements.darkness;
		max_life = 21;
		current_life = max_life;
		attack = 20;
		defense = 9;
		µ = 14;
		experience = 13;
	}
}
