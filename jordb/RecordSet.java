package jordb;

import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class RecordSet implements TableInterface, java.io.Serializable
{
	private Vector mRecords;
	private Vector mClasses = new Vector();
	private Hashtable mNames = new Hashtable();
	protected int mCurrentRow=-1;
	
	public RecordSet()
	{
		mRecords = new Vector();
	}
	
	public int currentRow()
	{
		return mCurrentRow;
	}
	
	public boolean moveFirst() throws TableException
	{
		mCurrentRow=-1;
		return (mRecords.size()>0);
	}
	
	public boolean moveNext() throws TableException
	{
		mCurrentRow++;
		return (mCurrentRow<mRecords.size());
	}
	
	public boolean moveTo(int row)
	{
		mCurrentRow=row;
		return (mCurrentRow<mRecords.size());
	}
	
	public String columnName(int index)
	{
		for(Enumeration enum1=mNames.keys();enum1.hasMoreElements();)
		{
			String aName = (String) enum1.nextElement();
			Integer anIndex = (Integer) mNames.get(aName);
			if(anIndex.intValue() == index)
				return aName;
		}
		return null;
	}
	
	public Class columnType(int index)
	{
		return (Class) mClasses.elementAt(index);
	}

	public int columnIndex(String name)
	{
		
		Integer i = (Integer) mNames.get(name);
		if(i!=null)
			return i.intValue();
		else
			return -1;
	}
	
	public int columnCount()
	{
		return mClasses.size();
	}
	
	public int rowCount()
	{
		return mRecords.size();
	}

	public void addColumn(String name, Class c)
	{
		mNames.put(name,new Integer(mClasses.size()));
		mClasses.addElement(c);
	}
	
	/**
	Package access for our friend Select
	**/
	void addRecord(Object[] objArry)
	{
		mRecords.addElement(objArry);
	}
	
	/**
	Package access for our friend Select
	**/
	boolean hasRecord(Object[] objArry)
	{
		//call equals on each element of the two arrays for ever row we already have
		
		for(int each=0;each<mRecords.size();each++)
		{
			Object[] compto = (Object[]) mRecords.elementAt(each);
			int yescnt=0;
			for(int index=0;index<compto.length;index++)
			{
				if(compto[index].equals(objArry[index]))
					yescnt++;
			}
			if(yescnt==compto.length)
				return true;
		}
		return false;
	}
	
	public Object get(int index) throws TableException
	{
		try
		{
			Object[] record = (Object[]) mRecords.elementAt(mCurrentRow);
			return record[index];
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
	}
	
	public void set(int index,Object ob) throws TableException
	{
		try
		{
			Object[] record = (Object[]) mRecords.elementAt(mCurrentRow);
			record[index]=ob;
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
	}
}

