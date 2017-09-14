package jordb;

import java.io.*;
import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Statement
 * Version 2.0 Modified 8/29/2006
 * 
 */

public class Update extends ModifyingStatement
{
	private boolean mFirstOnly=false;
	private Vector mFields=new Vector();
	private transient Vector mRollList=null;
	
	public Update(String path,String sql)
	{
		super(path,sql);
		mFrom = new From();
	}
	
	public void build() throws StatementException
	{
		try
		{
			StreamTokenizer st = new StreamTokenizer(new StringReader(mSQL));
			
			//dump(st);
			
			//this guy better be update
			st.nextToken();
			
			if(!st.sval.equals("update"))
				throw new TableException("Not a update statement.",null);
			
			st.nextToken();
			
			if(st.ttype==st.TT_WORD && st.sval.equals("first"))
			{
				mFirstOnly=true;
				st.nextToken();
			}
			
			while(!st.sval.equals("set"))
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
				
			}
			
			st.nextToken();
			
			while(!st.sval.equals("where"))
			{
				mFields.addElement(st.sval); //must be a field name
				st.nextToken();
				if(st.ttype == 61)//must be '='
					st.nextToken();
				else
					throw new TableException("Could not parse SET statement. " + st,null);
				
				if(st.ttype==st.TT_WORD)
					mValues.addElement(st.sval);
				else if(st.ttype==st.TT_NUMBER)
					if(Math.floor(st.nval) == st.nval)
						mValues.addElement(new Long((long) st.nval));
					else
						mValues.addElement(new Double(st.nval));
				else if(st.ttype==39 || st.ttype==34) //tick or quote
					mValues.addElement(new String(st.sval));
				else
					throw new TableException("Could not parse SET statement. " + st,null);
				
				st.nextToken();
				
				if(st.ttype==st.TT_EOF)
				{
					//no where clause, the final condition is always true
					mFinalCondition = new TrueCondition();
					break;
				}
				
				if(st.ttype == 44)//commas are ok
					st.nextToken();
				
			}
			
			if(mFinalCondition==null)
			{
				st.nextToken();
				processWhere(st);
			}
			
			//process paramaters and cast any numbers to the right data type
			Vector tables = mFrom.getTables();
			for(int eachfield=0;eachfield<mFields.size();eachfield++)
			{
				String  field = (String) mFields.elementAt(eachfield);
				Object  val = mValues.elementAt(eachfield);
				
				if(val instanceof Double)
				{
					for(int eachtable=0;eachtable<tables.size();eachtable++)
					{
						Table t = (Table) tables.elementAt(eachtable);
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
			throw new StatementException("Could not open table.",te,mSQL);
		}
		
		
		//create an array of indexes into the From to speed access
		int[] findex = new int[mFields.size()];
		for(int eachfield=0;eachfield<mFields.size();eachfield++)
		{
			String field = (String) mFields.elementAt(eachfield);
			findex[eachfield] = mFrom.columnIndex(field);
		}		
		
		//create an array to save rollback records in
		Object[] saverecs = new Object[tables.size()];
		
		try
		{
			//blast the values into the tables
			while(mFrom.moveNext())
			{
				if(mFinalCondition.compare())
				{
					
					//save the records for rollback
					for(int eachtable=0;eachtable<tables.size();eachtable++)
					{
						Table t = (Table) tables.elementAt(eachtable);
						saverecs[eachtable]=t.getRecord().clone();
					}
					
					for(int each=0;each<findex.length;each++)
						mFrom.set(findex[each],mValues.elementAt(each));
					
					//update the tables
					for(int eachtable=0;eachtable<tables.size();eachtable++)
					{
						Table t = (Table) tables.elementAt(eachtable);
						if(!t.isUpdated())
						{
							mRollList.addElement(new RollData((Object[]) saverecs[eachtable],t,
								t.getID()));
							t.update();
						}
					}
					
					modified++;
					
					if(mFirstOnly)
						break;
				}
			}
		}
		catch(TableException te)
		{
			try
			{
				for (int eachtable = 0; eachtable < tables.size(); eachtable++)
				{
					Table t = (Table)tables.elementAt(eachtable);
					t.close();
				}
			}
			catch (Throwable tclose)
			{
			}
			throw new StatementException("Could not run querry.",te,mSQL);
		}
		
		//close the tables, frees memory
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
		public long id;
		
		public RollData(Object[] oa,Table t,long l)
		{
			record = (Object[]) oa.clone();
			table=t;
			id=l;
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
				rd.table.setRecord(rd.record);
				rd.table.update();
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

