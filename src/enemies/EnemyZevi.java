package enemies;
import elements.Elements;

public class EnemyZevi extends Enemy {
	public EnemyZevi() {
		name = "Zevi";
		level = 1;
		entry_narrative = "A tiny little Zevi somehow bars your path.";
		attack_narrative = "The Zevi nibbles at you with its tiny teeth.";
		defeat_narrative = "You trashed the poor little Zevi.";
		player_kill_narrative = "You have been nibbled to death by a Zevi. How pathetic!";
		weakness = Elements.ice;
		max_life = 10;
		current_life = max_life;
		attack = 7;
		defense = 2;
		µ = 5;
		experience = 3;
	}
}
