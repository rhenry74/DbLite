package jordb;

import java.io.*;
import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Statement
 * Version 2.0 Modified 8/29/2006
 */

public class Delete extends ModifyingStatement
{
	private boolean mFirstOnly=false;
	private transient Vector mRollList=null;
	
	public Delete(String path,String sql)
	{
		super(path,sql);
		mFrom = new From();
	}
	
	public void build() throws StatementException
	{
		try
		{
			StreamTokenizer st = new StreamTokenizer(new StringReader(mSQL));
			
			//this guy better be delete
			st.nextToken();
			
			if(!st.sval.equals("delete"))
				throw new StatementException("Not a delete statement.",null,mSQL);
			
			st.nextToken();
			
			if(st.ttype==st.TT_WORD && st.sval.equals("first"))
			{
				mFirstOnly=true;
				st.nextToken();
			}
			
			st.nextToken();
			
			while(!st.sval.equals("where"))
			{
				if(st.ttype==st.TT_WORD)
				{
					Table t = new Table(st.sval,mPath);
					//have the table read it's design from disk so we can call comlmnIndex()
					t.readDesign();
					mFrom.addTable(t);
					st.nextToken();
					if(st.ttype == 44)//only commas are exceptable
					{
						st.nextToken();
					}
				}
				
				if(st.ttype==st.TT_EOF)
				{
					//no where clause, the final condition is always true
					mFinalCondition = new TrueCondition();
					break;
				}
			}
			
			if(mFinalCondition==null)
			{
				st.nextToken();
				processWhere(st);
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
		
		Vector tables = mFrom.getTables();
		//open the tables
		try
		{
			for(int eachtable=0;eachtable<tables.size();eachtable++)
			{
				Table t = (Table) tables.elementAt(eachtable);
				t.open(false);
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not table.",te,mSQL);
		}
		
		try
		{
			//pick rows to delete
			while(mFrom.moveNext())
			{
				if(mFinalCondition.compare())
				{
					
					//open the tables
					for(int eachtable=0;eachtable<tables.size();eachtable++)
					{
						Table t = (Table) tables.elementAt(eachtable);
						mRollList.addElement(new RollData(t.getRecord(),t));
						t.delete();
						modified++;
					}
					
					if(mFirstOnly)
						break;
				}
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not execute delete.",te,mSQL);
		}
		
		//close the tables
		try
		{
			for(int eachtable=0;eachtable<tables.size();eachtable++)
			{
				Table t = (Table) tables.elementAt(eachtable);
				t.close();
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not close table.",te,mSQL);
		}
		
		//reset the from, in case we want to use this statement again
		mFrom.reset();
		
		return modified;
	}
	
	private class RollData
	{
		public Object[] record;
		public Table table;
		
		public RollData(Object[] oa,Table t)
		{
			record = (Object[]) oa.clone();
			table=t;
		}
	}
	
	public void rollBack() throws StatementException
	{
		Vector opened = new Vector();
		try
		{
			for(int each=0;each < mRollList.size();each++)
			{
				RollData rd = (RollData) mRollList.elementAt(each);
				if(!opened.contains(rd.table))
				{
					rd.table.open(false);
					opened.addElement(rd.table);
				}
				rd.table.setRecord(rd.record);
				rd.table.insert();
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

