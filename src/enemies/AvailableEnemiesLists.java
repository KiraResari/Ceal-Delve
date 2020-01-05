package enemies;
import java.util.ArrayList;
import java.util.List;

public class AvailableEnemiesLists {
	static int depth_levels_per_rage_level = 23;
	
	public static List<Enemy> get_available_enemies_by_depth(int depth){
		List<Enemy> available_enemies = new ArrayList<Enemy>();
		int modified_depth = depth % depth_levels_per_rage_level;
		int rage_level = depth / depth_levels_per_rage_level;
		
		if(modified_depth == 0) {
			available_enemies.add(new EnemyZevi());
		} else
		if(modified_depth == 1) {
			available_enemies.add(new EnemyZevi());
			available_enemies.add(new EnemyGiya());
		}
		else if(modified_depth == 2) {
			available_enemies.add(new EnemyZevi());
			available_enemies.add(new EnemyGiya());
			available_enemies.add(new EnemyBunneroo());
		}
		else if(modified_depth == 3) {
			available_enemies.add(new EnemyGiya());
			available_enemies.add(new EnemyBunneroo());
		}
		else if(modified_depth == 4) {
			available_enemies.add(new EnemyGiya());
			available_enemies.add(new EnemyBunneroo());
			available_enemies.add(new EnemyMazoi());
		}
		else if(modified_depth == 5) {
			available_enemies.add(new EnemyBunneroo());
			available_enemies.add(new EnemyMazoi());
		}
		else if(modified_depth == 6) {
			available_enemies.add(new EnemyBunneroo());
			available_enemies.add(new EnemyMazoi());
			available_enemies.add(new EnemyMiacis());
		}
		else if(modified_depth == 7) {
			available_enemies.add(new EnemyMazoi());
			available_enemies.add(new EnemyMiacis());
		}
		else if(modified_depth == 8) {
			available_enemies.add(new EnemyMazoi());
			available_enemies.add(new EnemyMiacis());
			available_enemies.add(new EnemyThyla());
		}
		else if(modified_depth == 9) {
			available_enemies.add(new EnemyMiacis());
			available_enemies.add(new EnemyThyla());
		}
		else if(modified_depth == 10) {
			available_enemies.add(new EnemyMiacis());
			available_enemies.add(new EnemyThyla());
			available_enemies.add(new EnemyNherin());
		}
		else if(modified_depth == 11) {
			available_enemies.add(new EnemyThyla());
			available_enemies.add(new EnemyNherin());
		}
		else if(modified_depth == 12) {
			available_enemies.add(new EnemyThyla());
			available_enemies.add(new EnemyNherin());
			available_enemies.add(new EnemyUnia());
		}
		else if(modified_depth == 13) {
			available_enemies.add(new EnemyNherin());
			available_enemies.add(new EnemyUnia());
		}
		else if(modified_depth == 14) {
			available_enemies.add(new EnemyNherin());
			available_enemies.add(new EnemyUnia());
			available_enemies.add(new EnemyRouiean());
		}
		else if(modified_depth == 15) {
			available_enemies.add(new EnemyUnia());
			available_enemies.add(new EnemyRouiean());
		}
		else if(modified_depth == 16) {
			available_enemies.add(new EnemyUnia());
			available_enemies.add(new EnemyRouiean());
			available_enemies.add(new EnemyChelaine());
		}
		else if(modified_depth == 17) {
			available_enemies.add(new EnemyRouiean());
			available_enemies.add(new EnemyChelaine());
		}
		else if(modified_depth == 18) {
			available_enemies.add(new EnemyRouiean());
			available_enemies.add(new EnemyChelaine());
			available_enemies.add(new EnemyXervo());
		}
		else if(modified_depth == 19) {
			available_enemies.add(new EnemyChelaine());
			available_enemies.add(new EnemyXervo());
		}
		else if(modified_depth == 20) {
			available_enemies.add(new EnemyChelaine());
			available_enemies.add(new EnemyXervo());
			available_enemies.add(new EnemyKimara());
		}
		else if(modified_depth == 21) {
			available_enemies.add(new EnemyXervo());
			available_enemies.add(new EnemyKimara());
		}
		else if(modified_depth == 22) {
			available_enemies.add(new EnemyKimara());
		}
		
		for(Enemy enemy : available_enemies) {
			enemy.enrage(rage_level);
		}
		
		return available_enemies;
	}
}
