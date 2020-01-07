package combatants;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CharacterOperationsTest {

	@Test
	public void fully_heal_energy_should_fully_heal_energy_test() {
		Character character = new Character();
		character.current_energy = 1;
		int expected = character.max_energy;
		CharacterOperations.fully_heal_energy(character);
		int actual = character.current_energy;
		assertEquals(expected, actual, "Character's Energy was not at maximum after restore");
	}

	@Test
	public void fully_heal_life_should_fully_heal_life_test() {
		Character character = new Character();
		character.current_life = 1;
		int expected = character.max_life;
		CharacterOperations.fully_heal_life(character);
		int actual = character.current_life;
		assertEquals(expected, actual, "Character's Life was not at maximum after restore");
	}

	@Test
	public void subtract_µ_should_return_subtract_amount_test() {
		Character character = new Character();
		int starting_value = 10;
		int subtraction_value = 3;
		character.µ = starting_value;
		CharacterOperations.subtract_µ(character, subtraction_value);
		int expected = starting_value - subtraction_value;
		int actual = character.µ;
		assertEquals(expected, actual, "Character's µ was not correctly subtracted");
	}
	
	@Test
	public void return_missing_life_should_return_correct_amount() {
		Character character = new Character();
		int expected = 5;
		character.current_life -= expected;
		int actual = CharacterOperations.return_missing_life(character);
		assertEquals(expected, actual, "Character's missing Life was not correctly returned");
	}
	
	@Test
	public void return_missing_energy_should_return_correct_amount() {
		Character character = new Character();
		int expected = 5;
		character.current_energy -= expected;
		int actual = CharacterOperations.return_missing_energy(character);
		assertEquals(expected, actual, "Character's missing Energy was not correctly returned");
	}

}
