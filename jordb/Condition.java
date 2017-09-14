package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */
public interface Condition
{
	public boolean compare() throws TableException;
}

