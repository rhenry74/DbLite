package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class TransactionException extends TableException
{
	public static final int NOROWS = 2001;
	public static final int NOEXEC = 2002;
	public static final int NOROLL = 2003;
	
	private Statement mStatement;
	
	public TransactionException(String message, Throwable source,Statement s)
	{
		super(message,source);
		mStatement = s;
	}
	
	public TransactionException(String message, int code, Throwable source,Statement s)
	{
		super(message,code,source);
		mStatement = s;
	}
	
	public Statement getStatement() {return mStatement;}
	
} // TableException
/**
 * $Log$
 * 
 * 
 **/
