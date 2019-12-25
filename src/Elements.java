import java.util.ArrayList;
import java.util.List;

public class Elements {
	public static Element lightning = new Element("Lightning", "⚡", 1);
	public static Element light = new Element("Light", "✨", 2);
	public static Element flora = new Element("Flora", "☘", 3);
	public static Element order = new Element("Order", "⬡", 4);
	public static Element earth = new Element("Earth", "♦", 5);
	public static Element ice = new Element("Ice", "i", 6);
	public static Element water = new Element("Water", "💧", 7);
	public static Element darkness = new Element("Darkness", "🌙", 8);
	public static Element fauna = new Element("Fauna", "f", 9);
	public static Element chaos = new Element("Chaos", "☣", 10);
	public static Element wind = new Element("Wind", "🌀", 11);
	public static Element fire = new Element("Fire", "▲", 12);
	
	public static List<Element> get_available_elements(){
		List<Element> available_elements = new ArrayList<Element>();
		available_elements.add(lightning);
		available_elements.add(light);
		available_elements.add(flora);
		available_elements.add(order);
		available_elements.add(earth);
		available_elements.add(ice);
		available_elements.add(water);
		available_elements.add(darkness);
		available_elements.add(fauna);
		available_elements.add(chaos);
		available_elements.add(wind);
		available_elements.add(fire);
		return available_elements;
	}
	
	public static List<Integer> get_available_element_ids(){
		List<Integer> available_element_ids = new ArrayList<Integer>();
		available_element_ids.add(lightning.id);
		available_element_ids.add(light.id);
		available_element_ids.add(flora.id);
		available_element_ids.add(order.id);
		available_element_ids.add(earth.id);
		available_element_ids.add(ice.id);
		available_element_ids.add(water.id);
		available_element_ids.add(darkness.id);
		available_element_ids.add(fauna.id);
		available_element_ids.add(chaos.id);
		available_element_ids.add(wind.id);
		available_element_ids.add(fire.id);
		return available_element_ids;
	}
	
	public static Element get_element_by_id(int id){
		Element element = null;
		List<Element> available_elements = get_available_elements();
		for(Element available_element : available_elements) {
			if(available_element.id == id) {
				element = available_element;
			}
		}
		return element;
	}
}

