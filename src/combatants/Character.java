package combatants;

import elements.Element;

public class Character extends Combatant {
	public String name;
	public Element element;
	public int level = 1;
	public int experience_current = 0;
	public int experience_to_next_level = 8;
	public String character_class = "Elite Fighter with a Lot of Experience";
	public int max_life = 20;
	public int current_life = 20;
	public int max_energy = 10;
	public int current_energy = 10;
	public int attack = 5;
	public int defense = 5;
	public int iserialogy = 3;
	public int µ = 0;
	public int healing_kykli_count = 3;
	public int energy_water_count = 1;
	public Boolean defending = false;
}
