package enemies;
import elements.Elements;

public class EnemyKimara extends Enemy {
	public EnemyKimara() {
		name = "Kimara";
		level = 12;
		entry_narrative = "A mighty Kimara obstructs your path.";
		attack_narrative = "The Kimara attacks you with all its might.";
		defeat_narrative = "You managed to best the Kimara!";
		player_kill_narrative = "The Kimara painfully demonstrated your own mortality.";
		weakness = Elements.chaos;
		max_life = 50;
		current_life = max_life;
		attack = 30;
		defense = 9;
		� = 15;
		experience = 15;
	}
}
