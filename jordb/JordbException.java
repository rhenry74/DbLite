package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class JordbException extends Exception
{
	public static final int UNKNOWN=0;
	
	private Throwable mSource=null;
	private int mCode=0;
	
	public JordbException(String message)
	{
		super(message);
	}
	
	public JordbException(String message, int code, Throwable t)
	{
		super(message);
		mCode=code;
		mSource=t;
	}
	
	public JordbException(String message, Throwable t)
	{
		super(message);
		mSource=t;
	}
	
	public JordbException(String message, int code)
	{
		super(message);
		mCode=code;
	}
	
	protected void setCode(int c) {mCode=c;}
	
	public int getCode(){return mCode;}
	
	public Throwable getSource()
	{
		return mSource;
	}
	
}

