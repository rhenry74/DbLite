package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class TableException extends JordbException
{
	public TableException(String message, Throwable source)
	{
		super(message,source);
	}

	public TableException(String message, int code, Throwable source)
	{
		super(message,code,source);
	}
	
} // TableException
/**
 * $Log$
 * 
 * 
**/
