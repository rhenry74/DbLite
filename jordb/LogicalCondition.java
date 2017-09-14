package jordb;

 /**

 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Condition
 * 
 */
public abstract class LogicalCondition implements Condition, java.io.Serializable
{

	protected Condition mLeft;
	protected Condition mRight;
	
	public LogicalCondition()
	{
		super();
	}

	public LogicalCondition(Condition l,Condition r)
	{
		mLeft=l;
		mRight=r;
	}

	public void setLeft(Condition l)
	{
		mLeft=l;
	}
	
	public void setRight(Condition r)
	{
		mRight=r;
	}
	
	abstract public boolean compare() throws TableException;
}

