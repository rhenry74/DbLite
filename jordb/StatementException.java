package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class StatementException extends TableException
{
	private String mSQL;
	
	public StatementException(String message, Throwable source,String sql)
	{
		super(message,source);
		mSQL = sql;
	}

	public String getSQL() {return mSQL;}
	
} // TableException
/**
 * $Log$
 * 
 * 
**/
