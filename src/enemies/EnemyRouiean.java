package enemies;
import elements.Elements;

public class EnemyRouiean extends Enemy {
	public EnemyRouiean() {
		name = "Rouiean";
		level = 9;
		entry_narrative = "A Rouiean bounds towards you at great speed.";
		attack_narrative = "The Rouiean crashes into you with astounding force.";
		defeat_narrative = "You managed to put down the Rouiean.";
		player_kill_narrative = "The Rouiean tackled you to pulp.";
		weakness = Elements.flora;
		max_life = 19;
		current_life = max_life;
		attack = 15;
		defense = 10;
		µ = 12;
		experience = 11;
	}
}
