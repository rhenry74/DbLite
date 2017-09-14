package jordb;

/**

 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Condition
 * 
 */

public class TrueCondition implements Condition, java.io.Serializable
{
	public boolean compare() throws TableException
	{
		return true;
	}
}

