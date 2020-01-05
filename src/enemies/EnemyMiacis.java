package enemies;
import elements.Elements;

public class EnemyMiacis extends Enemy {
	public EnemyMiacis() {
		name = "Miacis";
		level = 5;
		entry_narrative = "A lonely Miacis engages you.";
		attack_narrative = "The Miacis slashes at you with its claws.";
		defeat_narrative = "You managed to fight off the Miacis.";
		player_kill_narrative = "The Miacis shredded you to pieces.";
		weakness = Elements.earth;
		max_life = 18;
		current_life = max_life;
		attack = 13;
		defense = 3;
		µ = 8;
		experience = 8;
	}
}
