package messaging_system;
import java.io.Serializable;

public class QuestionOption implements Serializable {

	private static final long serialVersionUID = 1L;
	public String option_text;
	public String hotkey;
	
	public QuestionOption(String option_text, String hotkey) {
		this.option_text = option_text;
		this.hotkey = hotkey;
	}
}
