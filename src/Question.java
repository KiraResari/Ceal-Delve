import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

	private static final long serialVersionUID = 1L;
	public String question_message;
	public List<QuestionOption> question_options;
	public List<String> question_option_hotkeys;
	
	//Questions with custom options
	private Question(String question_message, List<QuestionOption> question_options) {
		this.question_message = question_message;
		this.question_options = question_options;
		generateQuestionOptionHotkeyList();
	}
	
	static public Question construct_yes_no_question(String question_message) {
		List<QuestionOption> question_options = new ArrayList<QuestionOption>();
		question_options.add(new QuestionOption("Yes", "Y"));
		question_options.add(new QuestionOption("No", "N"));
		Question question = new Question(question_message, question_options);
		return question;
	}
	
	static public Question construct_question_with_custom_options(String question_message, List<QuestionOption> question_options) {
		Question question = new Question(question_message, question_options);
		return question;
	}

	public Boolean validateReply(String reply) {
		if (question_option_hotkeys.contains(reply)){
			return true;
		}
		else {
			return false;
		}
			
	}
	
	private void generateQuestionOptionHotkeyList() {
		question_option_hotkeys = new ArrayList<String>();
		for(QuestionOption option : question_options) {
			question_option_hotkeys.add(option.hotkey);
		}
	}
	
	public void print_question() {
		//Prints the question
		System.out.println(question_message);
		
		//Prints the answers in one line
		for(QuestionOption question_option : question_options) {
			System.out.print("| [" + question_option.hotkey + "] " + question_option.option_text + " |");
		}
		System.out.println();
	}
	
	public String request_and_validate_local_user_reply(BufferedReader user_input) throws IOException {
		String reply;
		while(true) {
			System.out.print("> ");
			reply = user_input.readLine();
			
			if(validateReply(reply)) {
				break;
			}
			else {
				System.out.println("Please pick from the provided options:");
				System.out.print(question_option_hotkeys.toString());
			}
		}
		return reply;
	}
}
