package enemies;
import combatants.Combatant;
import elements.Element;

public class Enemy extends Combatant {
	public String name;
	public String entry_narrative;
	public String attack_narrative;
	public String defeat_narrative;
	public String player_kill_narrative;
	public Element weakness;
	public int level;
	public int max_life;
	public int current_life;
	public int attack;
	public int defense;
	public int µ;
	public int experience;
	
	int rage_level;
	private int level_rage_mod = 12;
	private int max_life_rage_mod = 12;
	private int attack_rage_mod = 18;
	private int defense_rage_mod = 6;
	private int µ_rage_mod = 12;
	private int experience_rage_mod = 12;
	
	String name_rage_prefix_4 = "Ultimate ";
	String name_rage_prefix_3 = "Raging ";
	String name_rage_prefix_2 = "Omega ";
	String name_rage_prefix_1 = "Death ";
	
	public void enrage(int rage_level) {
		this.rage_level = rage_level;
		for(int i = 0; i < rage_level; i++) {
			level += level_rage_mod;
			max_life += max_life_rage_mod;
			current_life = max_life;
			attack+= attack_rage_mod;
			defense += defense_rage_mod;
			µ += µ_rage_mod;
			experience += experience_rage_mod;
		}
		generate_eraged_name();
	}

	private void generate_eraged_name() {
		String prefixes = "";
		int modified_rage_level = rage_level;
		if(modified_rage_level / 8 > 0) {
			prefixes += name_rage_prefix_4;
			modified_rage_level -= 8;
		}
		if(modified_rage_level / 4 > 0) {
			prefixes += name_rage_prefix_3;
			modified_rage_level -= 4;
		}
		if(modified_rage_level / 2 > 0) {
			prefixes += name_rage_prefix_2;
			modified_rage_level -= 2;
		}
		if(modified_rage_level == 1) {
			prefixes += name_rage_prefix_1;
		}
		name = prefixes + name;
	}
}
