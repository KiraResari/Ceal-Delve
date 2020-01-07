package elements;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Element_Test {

	@Test
	void element_should_have_correct_name_test() {
		String expected = "Lightning";
		Element element = new Element(expected, "⚡", 1);
		String actual = element.name;
		assertEquals(expected, actual, "Element did not have correct name");
	}

	@Test
	void element_should_have_correct_symbol_test() {
		String expected = "⚡";
		Element element = new Element("Lightning", expected, 1);
		String actual = element.symbol;
		assertEquals(expected, actual, "Element did not have correct symbol");
	}

	@Test
	void element_should_have_correct_id_test() {
		int expected = 1;
		Element element = new Element("Lightning", "⚡", expected);
		int actual = element.id;
		assertEquals(expected, actual, "Element did not have correct id");
	}

}
