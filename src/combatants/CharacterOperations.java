package combatants;

public class CharacterOperations {
	public static void fully_heal_energy(Character character) {
		character.current_energy = character.max_energy;
	}

	public static void fully_heal_life(Character character) {
		character.current_life = character.max_life;
	}

	public static void subtract_µ(Character character, int amount) {
		character.µ -= amount;
		
	}

	public static int return_missing_life(Character character) {
		return character.max_life - character.current_life;
	}

	public static int return_missing_energy(Character character) {
		return character.max_energy - character.current_energy;
	}
}
