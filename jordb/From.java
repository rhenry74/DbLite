package jordb;

import java.util.*;
import java.io.*;

/**
Given a list of tables, moveNext will iterate through each possible combination of rows.

 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see <relatedclassname>
 * 
 */
public class From implements Serializable
{
	
	private Vector mTables;
	public Vector getTables() {return mTables;}
	private boolean mFirst=true;
	
	public From()
	{
		mTables = new Vector();
	}
	
	public void reset()
	{
		mFirst=true;
	}
	
	public void addTable(TableInterface aTable)
	{
		mTables.addElement(aTable);
	}
	
	public boolean moveNext() throws TableException
	{
		if(mFirst==true)
		{
			for(int each=0;each<mTables.size();each++)
			{
				TableInterface ti = (TableInterface) mTables.elementAt(each);
				if(!ti.moveNext())
					return false;
			}
			mFirst=false;
			return true;
		}
		
		return next(0);		
		
	}
	
	private boolean next(int tableIndex) throws TableException
	{
		if(tableIndex==mTables.size())
			return false;
		
		TableInterface ti = (TableInterface) mTables.elementAt(tableIndex);
		if(!ti.moveNext())
		{			
			ti.moveFirst();
			ti.moveNext();
			return next(tableIndex+1);
		}
		else
		{
			return true;
		}
		
	}
	
	public boolean moveFirst()  throws TableException
	{
		for(int each=0;each<mTables.size();each++)
		{
			TableInterface ti = (TableInterface) mTables.elementAt(each);
			if(!ti.moveFirst())
				return false;
		}
		mFirst=true;
		return true;
	}
	
	public int columnIndex(String name)
	{		
		int columnOffset=0;
		for(int each=0;each<mTables.size();each++)
		{
			TableInterface ti = (TableInterface) mTables.elementAt(each);
			int index=ti.columnIndex(name);
			if(index==-1)
			{
				columnOffset=columnOffset+ti.columnCount();
			}
			else
			{
				return columnOffset + index;
			}
		}
		return -1;
	}
	
	public Class columnType(String name)
	{		
		for(int each=0;each<mTables.size();each++)
		{
			TableInterface ti = (TableInterface) mTables.elementAt(each);
			int index=ti.columnIndex(name);
			if(index!=-1)
				return ti.columnType(index);
		}
		return null;
	}
	
	
	public Object get(int index) throws TableException
	{
		try
		{
			for(int each=0;each<mTables.size();each++)
			{
				TableInterface ti = (TableInterface) mTables.elementAt(each);
				int current=ti.columnCount();
				if(index>=current)
				{
					index=index-current;
				}
				else
				{
					return ti.get(index);
				}
			}
			
			throw new TableException("Bad field index. Not enough fields in FROM.",null);
		}
		catch(Throwable th)
		{
			throw new TableException("Bad field index. " + index,th);
		}
	}
	
	public void set(int index,Object data) throws TableException
	{
		for(int each=0;each<mTables.size();each++)
		{
			TableInterface ti = (TableInterface) mTables.elementAt(each);
			int current=ti.columnCount();
			if(index>=current)
			{
				index=index-current;
			}
			else
			{
				ti.set(index,data);
				return;
			}
		}
		throw new TableException("Bad field index. Not enough fields in FROM.",null);
	}
	
}
/**
 * $Log$
 * 
 * 
 **/
