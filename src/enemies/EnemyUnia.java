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
		max_life = 30;
		current_life = max_life;
		attack = 18;
		defense = 7;
		µ = 11;
		experience = 11;
	}
}
