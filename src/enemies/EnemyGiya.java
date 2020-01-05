package enemies;
import elements.Elements;

public class EnemyGiya extends Enemy {
	public EnemyGiya() {
		name = "Giya";
		level = 2;
		entry_narrative = "A cute feathered Giya swopps down from the walls.";
		attack_narrative = "The Giya cutely slams into you.";
		defeat_narrative = "You've beaten the Giya into submission.";
		player_kill_narrative = "The Giya has managed to defeat you with the power of cuteness!";
		weakness = Elements.wind;
		max_life = 11;
		current_life = max_life;
		attack = 9;
		defense = 2;
		µ = 6;
		experience = 5;
	}
}
