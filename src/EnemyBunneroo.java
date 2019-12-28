
public class EnemyBunneroo extends Enemy {
	public EnemyBunneroo() {
		name = "Bunneroo";
		level = 3;
		entry_narrative = "A Bunneroo hops across your path. It's clearly evil.";
		attack_narrative = "The Bunneroo bounces on you.";
		defeat_narrative = "This Bunneroo will hop no more.";
		player_kill_narrative = "The Bunneroo keeps bouncing on you until you can no longer get up.";
		weakness = Elements.fauna;
		max_life = 12;
		current_life = max_life;
		attack = 9;
		defense = 4;
		µ = 7;
		experience = 5;
	}
}
