package jordb;

import java.io.*;
import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * Version 2.0 Modified 8/29/2006
 * 
 */

public class Insert extends ModifyingStatement
{
	private Vector mTables = new Vector();
	private Vector mFields = new Vector();
	private transient Vector mRollList=null;
	
	public Insert(String path,String sql)
	{
		super(path,sql);
	}
	
	public void build() throws StatementException
	{
		try
		{
			StreamTokenizer st = new StreamTokenizer(new StringReader(mSQL));
			
			//dump(st);
			
			//this guy better be insert
			st.nextToken();
			
			if(!st.sval.equals("insert"))
				throw new TableException("Not a insert statement.",null);
			
			st.nextToken();
			
			if(!st.sval.equals("into"))
				throw new TableException("Bad insert statement systax, no into.",null);
			
			st.nextToken();
			
			while(st.ttype!=40)
			{
				if(st.ttype==st.TT_WORD)
				{
					Table t = new Table(st.sval,mPath);
					//have the table read it's design from disk so we can call comlmnIndex()
					t.readDesign();
					mTables.addElement(t);
					st.nextToken();
					if(st.ttype == 44)//only commas are exceptable
					{
						st.nextToken();
					}
				}
				else 
				{
					throw new TableException("Bad syntax reading tables.",null);
				}
			}
			
			st.nextToken(); //should be 40
			
			while(st.ttype!=41)
			{
				if(st.ttype==st.TT_WORD)
				{
					mFields.addElement(st.sval);
					st.nextToken();
					if(st.ttype==44)//only commas are exceptable
					{
						st.nextToken();
					}
				}
				else if(st.ttype==st.TT_EOF)
				{
					throw new TableException("Bad syntax, incomplete select statement.",null);				
				}
				else 
				{
					throw new TableException("Bad syntax reading fields.",null);
				}
			}
			
			st.nextToken();
			
			if(!st.sval.equals("values"))
				throw new TableException("Bad insert statement systax, no values.",null);
			
			st.nextToken(); //should be 40
			
			st.nextToken();
			
			while(st.ttype!=41)
			{
				if(st.ttype==39 || st.ttype==34) //tick or quote
				{
					mValues.addElement(new String(st.sval));
					st.nextToken();
					if(st.ttype == 44)//only commas are exceptable
					{
						st.nextToken();
					}
				}
				else if(st.ttype==st.TT_NUMBER)
				{
					if (Math.floor(st.nval) == st.nval)
					{
						mValues.addElement(new Long((long) st.nval));
					}
					else
					{
						mValues.addElement(new Double(st.nval));
					}
					st.nextToken();
					if(st.ttype == 44)//only commas are exceptable
					{
						st.nextToken();
					}
				}
				else if(st.ttype==st.TT_EOF)
				{
					throw new TableException("Bad syntax, incomplete select statement.",null);				
				}
				else 
				{
					throw new TableException("Bad syntax reading fields.",null);
				}
			}
			
			//lets go ahead an cast any numbers to the right data type
			for(int eachfield=0;eachfield<mFields.size();eachfield++)
			{
				String  field = (String) mFields.elementAt(eachfield);
				Object  val = mValues.elementAt(eachfield);
				
				if(val instanceof Double)
				{
					for(int eachtable=0;eachtable<mTables.size();eachtable++)
					{
						Table t = (Table) mTables.elementAt(eachtable);
						int index = t.columnIndex(field);
						
						if(index!=-1)
						{
							mValues.removeElementAt(eachfield);
							mValues.insertElementAt(castNumber(t.columnType(index),(Double) val),eachfield);
							break;
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			throw new StatementException("Could not parse SQL statement.",e,mSQL);
		}
	}
	
	public int execute() throws StatementException
	{
		mRollList = new Vector();
		
		int modified=0;
		try
		{
			//open the tables
			for(int eachtable=0;eachtable<mTables.size();eachtable++)
			{
				Table t = (Table) mTables.elementAt(eachtable);
				t.open(false);
			}
		}
		catch(Throwable th)
		{
			throw new StatementException("Could not open table(s).",th,mSQL);
		}
		
		Table t = null;
		Object val = null;
		try
		{
			for(int eachfield=0;eachfield<mFields.size();eachfield++)
			{
				String  field = (String) mFields.elementAt(eachfield);
				val = mValues.elementAt(eachfield);
				
				for(int eachtable=0;eachtable<mTables.size();eachtable++)
				{
					t = (Table) mTables.elementAt(eachtable);
					int index = t.columnIndex(field);
					
					if(index!=-1)
					{
						t.set(index,val);
						break;
					}
				}
			}
		}
		catch(Throwable th)
		{
			try
			{
				t.close();
			}
			catch (Throwable tclose)
			{
			}
			throw new StatementException("Could not set value [" + val + "] on " + t,th,mSQL);
		}
		
		//run the inserts
		try
		{
			for(int eachtable=0;eachtable<mTables.size();eachtable++)
			{
				t = (Table) mTables.elementAt(eachtable);
				t.insert();
				modified++;
				mRollList.addElement(new RollData(t.getID(),t));
			}
		}
		catch(Throwable th)
		{
			throw new StatementException("Could not insert into " + t,th,mSQL);
		}
		
		//close the tables
		try
		{
			for(int eachtable=0;eachtable<mTables.size();eachtable++)
			{
				t = (Table) mTables.elementAt(eachtable);
				t.close();
			}
		}
		catch(Throwable th)
		{
			throw new StatementException("Could not close table " + t,th,mSQL);
		}
		
		return modified;
	}
	
	private class RollData
	{
		public long id;
		public Table table;
		
		public RollData(long l,Table t)
		{
			id=l;
			table=t;
		}
	}
	
	public void rollBack() throws StatementException
	{
		try
		{
			Vector opened = new Vector();
			for(int each=0;each < mRollList.size();each++)
			{
				RollData rd = (RollData) mRollList.elementAt(each);
				if(!opened.contains(rd.table))
				{
					rd.table.open(false);
					opened.addElement(rd.table);
				}
				rd.table.moveTo(rd.id);
				rd.table.delete();
			}
			for(int each=0;each<opened.size();each++)
			{
				Table t = (Table) opened.elementAt(each);
				t.close();
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not rollback.",te,mSQL);
		}
		
	}
}

