package messaging_system;
import java.io.Serializable;

public class Communication implements Serializable
{

	private static final long serialVersionUID = 2954227546182760502L;
	public String type ;
	public String message ;
	public Question question;

    public  Communication( String type, String message )
    {
        this.message=message;
        this.type=type;
    }
    
    public  Communication( String type, Question question )
    {
        this.question=question;
        this.type=type;
    }

    @Override
    public String toString()
    {
        return  "type: " + type + ", message: " + message;
    }
}