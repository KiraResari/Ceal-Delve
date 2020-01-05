package enemies;

public class EnrageEnemy {
	private static int level_rage_mod = 12;
	private static double max_life_rage_mod = 4.1;
	private static double attack_rage_mod = 4;
	private static double defense_rage_mod = 3.4;
	private static double µ_rage_mod = 2.5;
	private static double experience_rage_mod = 2.5;
	
	private static String name_rage_prefix_4 = "Ultimate ";
	private static String name_rage_prefix_3 = "Raging ";
	private static String name_rage_prefix_2 = "Omega ";
	private static String name_rage_prefix_1 = "Death ";
	
	public static void enrage(Enemy enemy, int rage_level) {
		for(int i = 0; i < rage_level; i++) {
			increase_level(enemy);
			increase_max_life(enemy);
			fully_heal(enemy);
			increase_attack(enemy);
			increase_defense(enemy);
			increase_µ(enemy);
			increase_experience(enemy);
		}
		generate_eraged_name(enemy, rage_level);
	}

	public static void increase_experience(Enemy enemy) {
		enemy.experience = (int) Math.ceil(enemy.experience * experience_rage_mod);
	}

	public static void increase_µ(Enemy enemy) {
		enemy.µ = (int) Math.ceil(enemy.µ * µ_rage_mod);
	}

	public static void increase_defense(Enemy enemy) {
		enemy.defense = (int) Math.ceil(enemy.defense * defense_rage_mod);
	}

	public static void increase_attack(Enemy enemy) {
		enemy.attack = (int) Math.ceil(enemy.attack * attack_rage_mod);
	}

	public static void fully_heal(Enemy enemy) {
		enemy.current_life = enemy.max_life;
	}

	public static void increase_max_life(Enemy enemy) {
		enemy.max_life = (int) Math.ceil(enemy.max_life * max_life_rage_mod);
	}

	public static void increase_level(Enemy enemy) {
		enemy.level += level_rage_mod;
	}

	private static void generate_eraged_name(Enemy enemy, int rage_level) {
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
		enemy.name = prefixes + enemy.name;
	}
}
