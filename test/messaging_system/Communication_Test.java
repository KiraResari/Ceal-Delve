package messaging_system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Communication_Test {

	@Test
	void communication_set_up_message_should_return_message_type_message_test() {
		String expected = CommunicationTypes.message;
		String communication_type = expected;
		Communication communication = new Communication(communication_type, "Arbitrary message");
		String actual = communication.type;
		assertEquals(expected, actual, "Communication constructor did not create an object of type message");
	}

	@Test
	void communication_set_up_message_should_return_provided_message_test() {
		String expected = "Arbitrary message";
		String communication_type = CommunicationTypes.message;
		Communication communication = new Communication(communication_type, expected);
		String actual = communication.message;
		assertEquals(expected, actual, "Communication constructor did not create object with provided message");
	}

	@Test
	void communication_set_up_question_should_return_message_type_question_test() {
		String expected = CommunicationTypes.question;
		String communication_type = expected;
		Question question = Question.construct_yes_no_question("Arbitrary question message");
		Communication communication = new Communication(communication_type, question);
		String actual = communication.type;
		assertEquals(expected, actual, "Communication constructor did not create an object of type question");
	}

	@Test
	void communication_set_up_question_should_return_provided_question_test() {
		String communication_type = CommunicationTypes.question;
		Question expected = Question.construct_yes_no_question("Arbitrary question message");
		Communication communication = new Communication(communication_type, expected);
		Question actual = communication.question;
		assertEquals(expected, actual, "Communication constructor did not create object with provided question");
	}

}
