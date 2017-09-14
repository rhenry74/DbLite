package jordb;

/**
A condition that is found in the whwere clause of an SQL statement.
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Condition
 * Version 2.0 Modified 8/29/2006
 * 
 */

public class Where implements Condition, java.io.Serializable
{
	private From mFrom;
	private int mLtIndex;
	private int mRtIndex;
	private int mRtObjIdx=-1;
	private int mLtObjIdx=-1;
	private Statement mValSrc;
	
	private int mType;
	
	public static final int EQUALS = 0;
	public static final int GREATER = 1;
	public static final int LESS = 2;
	public static final int GREATER_EQUALS = 3;
	public static final int LESS_EQUALS = 4;
	public static final int NOT_EQUALS = 5;
	
	public Where(From from, int ltIndex, int rtIndex, int type)
	{
		mFrom = from;
		mLtIndex = ltIndex;
		mRtIndex = rtIndex;
		mType = type;
	}
	
	public Where(From from,Statement src,int ltIndex,int rsrcidx,int type)
	{
		mFrom=from;
		mLtIndex = ltIndex;
		mRtObjIdx=rsrcidx;
		mType = type;
		mValSrc=src;
	}
	
	public Where(Statement src,From from,int lsrcidx,int rtIndex,int type)//the obsrc is on the right, ick
	{
		mFrom=from;
		mLtObjIdx=lsrcidx;
		mRtIndex = rtIndex;
		mType = type;
		mValSrc=src;
	}
	
	public boolean compare() throws TableException
	{
		
		Object l=null;
		Object r=null;

		try
		{
			if (mLtObjIdx == -1)
				l = mFrom.get(mLtIndex);
			else
				l = mValSrc.getValue(mLtObjIdx);

			if (mRtObjIdx == -1)
				r = mFrom.get(mRtIndex);
			else
				r = mValSrc.getValue(mRtObjIdx);
		}
		catch (java.lang.Exception e)
		{
			String hint = "?";
			if (l != null) hint = l.ToString();
			if (r != null) hint = r.ToString();
			throw new TableException("Error near " + hint, e);
		}
		
		return compare(l,r);
	}
	
	private boolean compare(Object lt, Object rt) throws TableException
	{
		try
		{
			if (lt instanceof java.util.Date && rt instanceof String)
			{
				java.util.Date dt = new java.util.Date((String) rt);
				rt = dt;
			}
			else if (rt instanceof java.util.Date && lt instanceof String)
			{
				java.util.Date dt = new java.util.Date((String) lt);
				lt = dt;
			}

			if(lt instanceof Comparable && rt instanceof Comparable)
			{
				//we are in luck
				Comparable l = (Comparable) lt;
				Comparable r = (Comparable) rt;
				int result = -1;
				try
				{
					result = l.compareTo(r);
				}
				catch(ClassCastException castex)
				{
					//the Comparable interface has failed us, were probably dealing with numbers
					double ln = ((Number) lt).doubleValue();
					double rn = ((Number) rt).doubleValue();
					switch(mType) 
					{
						case EQUALS:
							return ln==rn;
						case GREATER:
							return ln>rn;
						case LESS:
							return ln<rn;
						case GREATER_EQUALS:
							return ln>=rn;
						case LESS_EQUALS:
							return ln<=rn;
						case NOT_EQUALS:
							return ln!=rn;
						default:
							throw new TableException("Bad comparator", castex);
					}
				}
								
				switch(mType) 
				{
					case EQUALS:
						return result==0;
					case GREATER:
						return result>0;
					case LESS:
						return result<0;
					case GREATER_EQUALS:
						return result>=0;
					case LESS_EQUALS:
						return result<=0;
					case NOT_EQUALS:
						return result!=0;
					default:
						throw new TableException("Bad comparator", null);
				}
			}
			
			if(mType==EQUALS)
			{
				//rely on Object equals() function
				return lt.equals(rt);
			}
			
			throw new TableException("Cannot compare fields",null);
		}
		catch(Throwable th)
		{
			throw new TableException("Cannot compare fields",th);
		}
	}
}
