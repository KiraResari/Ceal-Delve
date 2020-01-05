package enemies;
import elements.Elements;

public class EnemyUnia extends Enemy {
	public EnemyUnia() {
		name = "Unia";
		level = 8;
		entry_narrative = "A reptilian Unia scuttles towards you.";
		attack_narrative = "The Unia violently snaps at you.";
		defeat_narrative = "You pacified the Unia... rather violently.";
		player_kill_narrative = "You have been snapped in two by the Unia.";
		weakness = Elements.order;
		max_life = 18;
		current_life = max_life;
		attack = 14;
		defense = 9;
		µ = 11;
		experience = 10;
	}
}
