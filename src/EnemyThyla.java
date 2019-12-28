
public class EnemyThyla extends Enemy {
	public EnemyThyla() {
		name = "Thyla";
		level = 6;
		entry_narrative = "A terretorial Thyla considers you an invader and attacks.";
		attack_narrative = "The Thyla bites you with its fangs.";
		defeat_narrative = "You conquered the Thyla threat.";
		player_kill_narrative = "The Thyla completely savaged you, and will now chew on your bones.";
		weakness = Elements.light;
		max_life = 15;
		current_life = max_life;
		attack = 12;
		defense = 7;
		µ = 9;
		experience = 8;
	}
}
