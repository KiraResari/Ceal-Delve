package elements;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class Elements_Test {

	@Test
	void get_available_elements_should_return_list_with_twelve_entries_test() {
		List<Element> elements = Elements.get_available_elements();
		int expected = 12;
		int actual = elements.size();
		assertEquals(expected, actual, "List of available elements did not contain expected number of entries");
	}

	@Test
	void get_available_element_ids_should_return_list_with_twelve_entries_test() {
		List<Integer> elements = Elements.get_available_element_ids();
		int expected = 12;
		int actual = elements.size();
		assertEquals(expected, actual, "List of available element ids did not contain expected number of entries");
	}

	@Test
	void get_element_by_id_eleven_should_return_wind_test() {
		Element element = new Element("Wind", "🌀", 11);
		String expected = element.name;
		String actual = Elements.get_element_by_id(element.id).name;
		assertEquals(expected, actual, "Returned element was not expected element");
	}
}
