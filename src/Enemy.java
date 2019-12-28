
public class Enemy extends Combatant {
	String name;
	String entry_narrative;
	String attack_narrative;
	String defeat_narrative;
	String player_kill_narrative;
	Element weakness;
	int level;
	int max_life;
	int current_life;
	int attack;
	int defense;
	int µ;
	int experience;
	
	int rage_level;
	int level_rage_mod = 12;
	int max_life_rage_mod = 12;
	int attack_rage_mod = 18;
	int defense_rage_mod = 6;
	int µ_rage_mod = 12;
	int experience_rage_mod = 12;
	
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
