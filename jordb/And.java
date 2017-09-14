package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Condition
 * 
 */
public class And extends LogicalCondition implements Condition
{

	public And()
	{
		super();
	}

	public And(Condition l,Condition r)
	{
		super(l,r);
	}

	public boolean compare() throws TableException
	{
		return mLeft.compare() && mRight.compare();
	}

}

