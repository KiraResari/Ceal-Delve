package enemies;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AvailableEnemiesLists_Test {

	@Test
	void get_available_enemies_by_depth_should_return_zevi_for_depth_zero_test() {
		int target_depth = 0;
		List<Enemy> available_enemies = AvailableEnemiesLists.get_available_enemies_by_depth(target_depth);
		Enemy expected_enemy= new EnemyZevi();
		String expected = expected_enemy.name;
		String actual = available_enemies.get(0).name;
		assertEquals(expected, actual, "Expected enemy was not actual enemy for depth " + target_depth);
	}
	
	@ParameterizedTest
	@ValueSource(ints = { 0, 22 })
	void get_available_enemies_by_depth_should_return_one_enemy_for_provided_depths_test(int target_depth) {
		List<Enemy> available_enemies = AvailableEnemiesLists.get_available_enemies_by_depth(target_depth);
		int expected = 1;
		int actual = available_enemies.size();
		assertEquals(expected, actual, "Number of available enemies did not match for depth " + target_depth);
	}
	
	@ParameterizedTest
	@ValueSource(ints = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21 })
	void get_available_enemies_by_depth_should_return_two_enemies_for_provided_depths_test(int target_depth) {
		List<Enemy> available_enemies = AvailableEnemiesLists.get_available_enemies_by_depth(target_depth);
		int expected = 2;
		int actual = available_enemies.size();
		assertEquals(expected, actual, "Number of available enemies did not match for depth " + target_depth);
	}
	
	@ParameterizedTest
	@ValueSource(ints = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 })
	void get_available_enemies_by_depth_should_return_three_enemies_for_provided_depths_test(int target_depth) {
		List<Enemy> available_enemies = AvailableEnemiesLists.get_available_enemies_by_depth(target_depth);
		int expected = 3;
		int actual = available_enemies.size();
		assertEquals(expected, actual, "Number of available enemies did not match for depth " + target_depth);
	}

	@Test
	void get_available_enemies_by_depth_should_return_ultimate_raging_omega_death_zevi_for_depth_345_test() {
		int target_depth = 23 * 15;
		List<Enemy> available_enemies = AvailableEnemiesLists.get_available_enemies_by_depth(target_depth);
		Enemy expected_enemy= new EnemyZevi();
		String expected = "Ultimate Raging Omega Death " + expected_enemy.name;
		String actual = available_enemies.get(0).name;
		assertEquals(expected, actual, "Expected enemy was not actual enemy for depth " + target_depth);
	}
}
