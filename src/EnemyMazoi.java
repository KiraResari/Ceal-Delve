
public class EnemyMazoi extends Enemy {
	public EnemyMazoi() {
		name = "Mazoi";
		level = 4;
		entry_narrative = "A scaled Mazoi creeps from the dark.";
		attack_narrative = "The Mazoi bites you with it fangs.";
		defeat_narrative = "You defeated the mazoi.";
		player_kill_narrative = "The Mazoi stylishly ripped out your throat.";
		weakness = Elements.lightning;
		max_life = 13;
		current_life = max_life;
		attack = 9;
		defense = 6;
		� = 8;
		experience = 6;
	}
}
