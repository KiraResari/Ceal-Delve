import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

	private static final long serialVersionUID = 1L;
	public String question_message;
	public List<QuestionOption> question_options;
	public List<String> question_option_hotkeys;
	
	//Questions with custom options
	public Question(String question_message, List<QuestionOption> question_options) {
		this.question_message = question_message;
		this.question_options = question_options;
		generateQuestionOptionHotkeyList();
	}
	
	//Yes/No Questions
	public Question(String question_message) {
		this.question_message = question_message;
		question_options = new ArrayList<QuestionOption>();
		this.question_options.add(new QuestionOption("Yes", "Y"));
		this.question_options.add(new QuestionOption("No", "N"));
		generateQuestionOptionHotkeyList();
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
}
