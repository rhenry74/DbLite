package jordb;

import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Table
 * 
 */
public class CachedTable extends Table
{
	
	private Vector mCache;
	private int mRow=-1;
	
	public CachedTable(String name,String path)
	{
		super(name,path);
		mCache = new Vector();
	}
	
	public boolean moveFirst() throws TableException
	{
		mRow=-1;
		if(mCache.size()>0)
			return true;
		else
			return super.moveFirst();
	}
	
	public boolean moveNext() throws TableException
	{
		mRow++;
		
		if(mRow>=mCache.size())
		{
			if(!super.moveNext())
				return false;
			mCache.addElement(mRecord);
		}
		return true;		
	}
	
	public boolean open(boolean readOnly) throws TableException
	{
		if(readOnly)
			return super.open(readOnly);
		else
			throw new TableException("Cached Tables must be read only.",null);
	}
	
	public void create() throws TableException
	{
		throw new TableException("Cannot call create() on a cached table.",null);
	}
	
	public Object get(int index) throws TableException
	{
		try
		{
			Object[] record = (Object[]) mCache.elementAt(mRow);
			return record[index];
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
		
	}
	
	public void close() throws TableException
	{
		mCache=new Vector();
		mRow=-1;
		super.close();
	}
	
}

