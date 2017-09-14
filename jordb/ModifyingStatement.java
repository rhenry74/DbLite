package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public abstract class ModifyingStatement extends Statement
{

	public ModifyingStatement(String path, String sql)
	{
		super(path,sql);
	}
	
	abstract public int execute() throws StatementException;
	
	abstract public void rollBack() throws StatementException;
}

