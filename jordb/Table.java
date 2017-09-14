package jordb;

import java.io.*;
import java.util.*;

/**
The Table class is the heart of jordb. It is the programmers interface to the data stored in
files.
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class Table implements TableInterface, Serializable
{
	private Vector mClasses = new Vector();
	private Hashtable mNames = new Hashtable();
	private String mFileBase;
	private String mName;
	protected transient Object[] mRecord=null;
	protected transient int mCurrentRow=0;
	private transient int mDeleted=0;
	private transient int mInserted=0;
	
	private transient ObjectOutputStream mObjectOutput=null;
	private transient ObjectInputStream mObjectInput=null;
	
	private transient boolean mUpdated = false;
	public boolean isUpdated(){return mUpdated;}
	
	private int mRowCount;
	private long mIDSeed;
	
	private static Hashtable mLocks = new Hashtable();
	
	public Table(String name,String path)
	{
		mFileBase = new String(path);
		mName = new String(name);
	}
	
	/**
	Specify the record design
	**/
	public void addColumn(String name, Class c)
	{
		mNames.put(name,new Integer(mClasses.size()));
		mClasses.addElement(c);
	}
	
	/**
	Read the record design
	**/
	public Class columnType(int index)
	{
		return (Class) mClasses.elementAt(index);
	}
	
	public Class columnType(String name)
	{
		return (Class) mClasses.elementAt(columnIndex(name));
	}
	
	/**
	Return the zero offset of the column by it's name.
	**/
	public int columnIndex(String name)
	{
		
		StringTokenizer stok = new StringTokenizer(name,".");
		String field = name;
		String table = stok.nextToken();
		if(stok.hasMoreTokens())
		{
			field = stok.nextToken();
			if(!mName.equals(table))
				return -1;
		}
		
		Integer i = (Integer) mNames.get(field);
		if(i!=null)
			return i.intValue();
		else
			return -1;
	}
	
	/**
	Get the name of the column at a given zero offset.
	**/
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
	
	/**
	Returns the number of columns in the table.
	**/
	public int columnCount()
	{
		return mClasses.size();
	}
	
	/**
	Return the name of this table.
	**/
	public String name()
	{
		return mName;
	}
	
	private void writeDesign() throws TableException
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				mFileBase + "\\" + mName + ".design"));
			oos.writeObject(mClasses);
			oos.writeObject(mNames);
			oos.writeInt(mRowCount-mDeleted+mInserted);
			oos.writeLong(mIDSeed);
			mDeleted = mInserted = 0;
			oos.close();
		}
		catch(Exception e)
		{
			throw new TableException("Could not write table design.",e);
		}
		
	}
	
	/**
	Read the design infor mation from disk. This does not open the tables data file.
	**/
	public void readDesign() throws TableException
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				mFileBase + "\\" + mName + ".design"));
			mClasses = (Vector) ois.readObject();
			mNames = (Hashtable) ois.readObject();
			mRowCount = ois.readInt();
			mIDSeed = ois.readLong();
			ois.close();
		}
		catch(Exception e)
		{
			throw new TableException("Could not read table design.",e);
		}
		
	}
	
	/**
	Write the table design to disk and prepare the data file for inserts.
	**/
	public void create() throws TableException
	{
		
		writeDesign();
		
		try
		{
			mObjectInput = null;
			mObjectOutput = new ObjectOutputStream(new FileOutputStream(mFileBase + "\\" + 
				mName + ".temp"));
		}
		catch(Exception e)
		{
			throw new TableException("Could not create table data file.",e);
		}
		
		mRecord = new Object[mClasses.size()+2];
		
		checkLocks();
	}
	
	/**
	Read a table design from disk and opens the data file. Tables opened with the readOnly
	flag set to true cannot be updated. (duh)	The first row is not yet available. 
	Returns true if there are records to read.
	**/
	public boolean open(boolean readOnly) throws TableException
	{
		checkLocks();
		
		readDesign();
		
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(mFileBase + "\\" + mName + ".data");
			mObjectInput = new ObjectInputStream(fis);
		}
		catch(Exception e)
		{
			throw new TableException("Could not open table data file.",e);
		}
		
		try
		{
			if(readOnly==false)
			{
				mObjectOutput = new ObjectOutputStream(new 
					FileOutputStream(mFileBase + "\\" + mName + ".temp"));
			}		
		}
		catch(Exception e)
		{
			throw new TableException("Could not create table data file.",e);
		}
		
		mCurrentRow=0;
		if(mRowCount>0)
		{
			return true;
		}
		return false;
	}
	
	/**
	Returns the number of rows in the table.
	**/
	public int rowCount()
	{
		return mRowCount;
	}
	
	/**
	Returns the zero offset of the row cursor.
	**/
	public int currentRow()
	{
		return mCurrentRow;
	}
	
	/**
	Move the row cursor to the beginning of the table. The first row is not yet available.
	Returns true if there are records to read.
	**/
	public boolean moveFirst() throws TableException
	{
		//brutal
		if(mCurrentRow>0)
		{
			boolean readOnly = (mObjectOutput==null);
			close();
			return open(readOnly);
		}
		else
		{
			return mRowCount>0;
		}
	}
	
	/**
	Move the record cursor to the next row.
	Returns true if a record was read.
	**/
	public boolean moveNext() throws TableException
	{
		if(mObjectInput == null)
			return false;
		
		try
		{
			if(mObjectOutput != null)
			{
				if(!mUpdated)
				{
					if(mRecord != null)//don't write the first one
					{
						mObjectOutput.writeObject(mRecord);
					}
				}
			}
			mRecord = null;
			if(mCurrentRow < mRowCount)
				mRecord = (Object[]) mObjectInput.readObject();
			else
				return false;
			mUpdated = false;
			mCurrentRow++;
			return true;
		}
		catch(Exception e)
		{
			throw new TableException("Could not read record from file.",e);
		}
	}
	
	/**
	Get the object in the current row from the column with the given name.
	**/
	public Object get(String name) throws TableException
	{
		return get(columnIndex(name));
	}
	
	/**
	Get the object from the current row from the column with the given zero offset.
	**/
	public Object get(int index) throws TableException
	{
		try
		{
			return mRecord[index];
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
	}
	
	public long getID() throws TableException
	{		
		try
		{
			return ((Long) mRecord[columnCount()]).longValue();
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
	}
	
	public long getTime() throws TableException
	{		
		try
		{
			return ((Long) mRecord[columnCount()+1]).longValue();
		}
		catch(Throwable t)
		{
			throw new TableException("Could not get data from record.",t);
		}
	}

	/**
	In the current row, set the object at the given column name to the given data. The class
	of the data is checked against the table design. 
	**/
	public void set(String name,Object data) throws TableException
	{
		set(columnIndex(name),data);
	}
	
	/**
	In the current row, set the object at the given column to the given data. The class
	of the data is checked against the table design. The column is a zero offset.
	**/
	public void set(int index,Object data) throws TableException
	{
		try
		{
			if(mRecord==null)
			{
				mRecord = new Object[mClasses.size()+2];
			}
			Class c = (Class) mClasses.elementAt(index);
			if (c.isInstance(data))
				mRecord[index] = data;
			else
			{
				try
				{
					mRecord[index] = data;
				}
				catch(Throwable castex)
				{
					throw new TableException("Bad data class.", castex);
				}
			}
		}
		catch(Throwable t)
		{
			throw new TableException("Could not set data in record.",t);
		}
	}
	
	/**
	Insert the current record to the table.
	**/
	public void insert() throws TableException
	{
		try
		{
			mIDSeed++;
			mRecord[columnCount()] = new Long(mIDSeed);
			mRecord[columnCount()+1] = new Long(Calendar.getInstance()
				.getTime().getTime());
			mObjectOutput.writeObject(mRecord.clone());//it won't write the same object twice?
			mInserted++;
			mUpdated = true;
		}
		catch(Exception e)
		{
			throw new TableException("Could not insert record into file.",e);
		}
	}
	
	/**
	Update the current record in the table.
	**/
	public void update(boolean freshen) throws TableException
	{
		try
		{
			if(!mUpdated)
			{
				if(freshen)
				{
					mIDSeed++;
					mRecord[columnCount()] = new Long(mIDSeed);
					mRecord[columnCount()+1] = new Long(Calendar.getInstance()
						.getTime().getTime());
				}
				mObjectOutput.writeObject(mRecord);
				mUpdated = true;
			}
			else
				throw new TableException("Cannot update same record twice.",null);
		}
		catch(Exception e)
		{
			throw new TableException("Could not update record in file.",e);
		}
	}
	
	public void update() throws TableException
	{
		update(false);
	}
	
	/**
	Delete the current record from the table.
	**/
	public void delete() throws TableException
	{
		if(!mUpdated)
		{
			mRecord = null; // if mRecord is null, moveNext will not write before reading
			mDeleted++;
			mUpdated=true;
		}
		else
			throw new TableException("Cannot delete the same row twice.",null);
	}
	
	/**
	Close the table, I repete, close the table. If updateable tables are not closed the files 
	may be corrupted.
	**/
	public void close() throws TableException
	{
		if(mObjectOutput != null)
		{
			//copy the rest of the input to the output
			while(this.moveNext());
			
			try
			{
				//write the last record
				if(mRecord != null && (mObjectInput!=null && !mUpdated))
				{
					mObjectOutput.writeObject(mRecord);
				}
				mObjectOutput.close();
				
				if(mObjectInput != null)
					mObjectInput.close();
				mObjectInput = null;
				
				//juggle the files
				File f = new File(mFileBase + "\\" + mName + ".data");
				f.delete();
				f = new File(mFileBase + "\\" + mName + ".temp");
				f.renameTo(new File(mFileBase + "\\" + mName + ".data"));
				
				writeDesign();
			}
			catch(Exception e)
			{
				throw new TableException("Could not close table files.",e);
			}
			
		}
		
		try
		{
			if(mObjectInput != null)
			{
				mObjectInput.close();
				mObjectInput = null;
			}
		}
		catch(Exception e)
		{
			throw new TableException("Could not close table input file.",e);
		}
		removeLock();
	}
	
	/**
	Package access method for our Statement derived friends to use for roll backs.
	**/
	void moveTo(long id) throws TableException
	{
		while(moveNext())
		{
			if(getID() == id)
				break;
		}
	}
	
	private synchronized void checkLocks() throws TableException
	{
		Integer rocnt = (Integer) mLocks.get(mFileBase + mName);
		
		if(rocnt==null)
		{
			int i=-1;//this is not a read only table
			if(isReadOnly())
				i=1;
			mLocks.put(mFileBase + mName,new Integer(i));
			return;
		}
		
		if(rocnt.intValue()==-1)
			throw new TableException("Table " + mFileBase + " " + mName + " is locked.",null);
		
		if(!isReadOnly())
			throw new TableException("Table " + mFileBase + " " + mName + " is locked.",null);
		
		int i=rocnt.intValue();
		i++;
		mLocks.put(mFileBase + mName,new Integer(i));
		
	}
	
	private synchronized void removeLock() throws TableException
	{
		Integer rocnt = (Integer) mLocks.get(mFileBase + mName);
		int i=rocnt.intValue();
		if(i==-1)
		{
			mLocks.remove(mFileBase + mName);
			return;
		}
		i--;
		if(i==0)
			mLocks.remove(mFileBase + mName);
		else
			mLocks.put(mFileBase + mName,new Integer(i));
		
	}
	
	public boolean isReadOnly()
	{
		return mObjectOutput==null;
	}
	
	/**
	Package access for our statement derived friends who want to do quick rollbacks.
	**/
	Object[] getRecord()
	{
		return mRecord;
	}
	
	/**
	Package access for our statement derived friends who want to do quick rollbacks.
	**/
	void setRecord(Object[] oa)
	{
		mRecord=oa;
	}
	
} // Table

