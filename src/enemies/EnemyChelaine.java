package enemies;
import elements.Elements;

public class EnemyChelaine extends Enemy {
	public EnemyChelaine() {
		name = "Chelaine";
		level = 10;
		entry_narrative = "A fierce Chelaine is determined to make you its next meal.";
		attack_narrative = "The Chelaine takes a painful bite out of you.";
		defeat_narrative = "You managed to beat the Chelaine!";
		player_kill_narrative = "The Chelaine found a wholesome meal in you.";
		weakness = Elements.water;
		max_life = 40;
		current_life = max_life;
		attack = 23;
		defense = 7;
		� = 13;
		experience = 13;
	}
}
