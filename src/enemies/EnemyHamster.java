package enemies;
import elements.Elements;

public class EnemyHamster extends Enemy {
	public EnemyHamster() {
		name = "Hamster";
		level = 40;
		entry_narrative = "An inconspicuous Hamster engages you with suspicous boldness.";
		attack_narrative = "A horde of Hamsters appears out of nowhere and tramples you.";
		defeat_narrative = "You somehow managed to hold your groudn agaisnt the Hamster.";
		player_kill_narrative = "You have been trampled to death by the Hamsters.";
		weakness = Elements.fire;
		max_life = 1000;
		current_life = max_life;
		attack = 700;
		defense = 100;
		� = 250;
		experience = 500;
	}
}
