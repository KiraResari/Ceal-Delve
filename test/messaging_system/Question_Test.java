package messaging_system;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class Question_Test {

	@Test
	public void construct_yes_no_question_should_return_question_with_yes_as_first_option_test() {
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		String expected = "Y";
		String actual = question.question_options.get(0).hotkey;
		assertEquals(expected, actual, "Yes/No question does not have 'Yes' as first option.");
	}

	@Test
	public void construct_yes_no_question_should_return_question_with_no_as_second_option_test() {
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		String expected = "N";
		String actual = question.question_options.get(1).hotkey;
		assertEquals(expected, actual, "Yes/No question does not have 'No' as second option.");
	}
	
	@Test
	public void construct_question_with_custom_options_should_return_question_with_provided_question_message_test() {
		String expected = "Arbirary question message";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Arbitrary test option", "T"));
		String question_message = expected;
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		String actual = question.question_message;
		assertEquals(expected, actual, "Text of question message did not match provided text.");
	}
	
	@Test
	public void construct_question_with_custom_options_should_return_question_with_provided_option_test() {
		String expected = "Arbitrary test option";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption(expected, "T"));
		String question_message = "Arbirary question message";
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		String actual = question.question_options.get(0).option_text;
		assertEquals(expected, actual, "Text of question option did not match provided text.");
	}
	
	@Test
	public void construct_question_with_custom_options_should_return_question_with_provided_hotkey_test() {
		String expected = "T";
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Arbitrary test option", expected));
		String question_message = "Arbirary question message";
		Question question = Question.construct_question_with_custom_options(question_message, question_options);
		String actual = question.question_options.get(0).hotkey;
		assertEquals(expected, actual, "Hotkey of question option did not match provided hotkey.");
	}
	
	@Test
	public void validate_reply_returns_true_for_valid_uppercase_reply() {
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		Boolean result = question.validate_reply("Y");
		assertTrue(result, "Valid uppercase reply was not recognized as valid");
	}
	
	@Test
	public void validate_reply_returns_true_for_valid_lowercase_reply() {
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		Boolean result = question.validate_reply("n");
		assertTrue(result, "Valid lowercase reply was not recognized as valid");
	}
	
	@Test
	public void validate_reply_returns_false_for_invalid_reply() {
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		Boolean result = question.validate_reply("z");
		assertFalse(result, "Invalid reply was wrongly recognized as valid");
	}

}
