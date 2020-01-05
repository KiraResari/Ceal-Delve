package enemies;
import elements.Elements;

public class EnemyNherin extends Enemy {
	public EnemyNherin() {
		name = "Nherin";
		level = 7;
		entry_narrative = "For some reason, a Nherin is prancing through the cave. Unfortunately, it appears to be hostile.";
		attack_narrative = "The Nherin charges at you with its horns.";
		defeat_narrative = "The Nherin collapses at your feet.";
		player_kill_narrative = "The Nherin obviously pierced some rather vital body parts of yours.";
		weakness = Elements.fire;
		max_life = 16;
		current_life = max_life;
		attack = 13;
		defense = 8;
		µ = 10;
		experience = 9;
	}
}
