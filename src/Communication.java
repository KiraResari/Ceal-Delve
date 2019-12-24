import java.io.Serializable;

public class Communication implements Serializable
{

	private static final long serialVersionUID = 2954227546182760502L;
	String type ;
    String message ;

    public  Communication( String type, String message )
    {
        this.message=message;
        this.type=type;
    }

    @Override
    public String toString()
    {
        return  "type: " + type + ", message: " + message;
    }
}