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

public class Select extends Statement
{
	private boolean mFirstOnly=false;
	private boolean mAllFields=false;
	private boolean mUnique=false;
	private Vector mFields=new Vector();
	
	public Select(String path,String sql)
	{
		super(path,sql);
		mFrom = new From();
	}
	
	public void build() throws StatementException
	{
		try
		{
			StreamTokenizer st = new StreamTokenizer(new StringReader(mSQL));
			
			//this guy better be select
			st.nextToken();
			
			if(!st.sval.equals("select"))
				throw new TableException("Not a select statement.",null);
			
			st.nextToken();
			
			if(st.ttype==st.TT_WORD && st.sval.equals("first"))
			{
				mFirstOnly=true;
				st.nextToken();
			}
			
			if(st.ttype==st.TT_WORD && st.sval.equals("unique"))
			{
				mUnique=true;
				st.nextToken();
			}
			
			if(st.ttype==42) // select *
			{
				mAllFields = true;
				st.nextToken(); //from
			}
			
			while(!mAllFields && !st.sval.equals("from"))
			{
				if(st.ttype==st.TT_WORD)
				{
					mFields.addElement(st.sval);
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
			
			st.nextToken();
			
			while(!st.sval.equals("where"))
			{
				if(st.ttype==st.TT_WORD)
				{
					Table t = new CachedTable(st.sval,mPath);
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
			
			if(mAllFields)
			{
				Vector tables = mFrom.getTables();
				//build up the field vector from all tables
				for(int eachtable=0;eachtable<tables.size();eachtable++)
				{
					Table t = (Table) tables.elementAt(eachtable);
					for(int eachfield=0;eachfield<t.columnCount();eachfield++)
					{
						mFields.addElement(t.name() + "." + t.columnName(eachfield));
					}
				}
			}
			
			
			//will save order by stuff here
			
			
			
		}
		catch(Exception e)
		{
			throw new StatementException("Could not parse SQL statement.",e,mSQL);
		}
	}
	
	public RecordSet getRecordSet() throws StatementException
	{
		RecordSet rs = new RecordSet();
		
		Vector tables = mFrom.getTables();
		//open the tables
		try
		{
			for(int eachtable=0;eachtable<tables.size();eachtable++)
			{
				Table t = (Table) tables.elementAt(eachtable);
				t.open(true);
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not open tables.",te,mSQL);
		}
		
		
		//add the fields to the record set
		for(int eachfield=0;eachfield<mFields.size();eachfield++)
		{
			String field = (String) mFields.elementAt(eachfield);
			
			for(int eachtable=0;eachtable<tables.size();eachtable++)
			{
				Table t = (Table) tables.elementAt(eachtable);
				
				int index = t.columnIndex(field);
				if(index!=-1)
				{
					rs.addColumn(field,t.columnType(index));
					break;
				}
			}
		}
		
		
		//create an array of indexes into the From to speed access
		int[] findex = new int[mFields.size()];
		for(int eachfield=0;eachfield<mFields.size();eachfield++)
		{
			String field = (String) mFields.elementAt(eachfield);
			findex[eachfield] = mFrom.columnIndex(field);
		}		
		
		try
		{
			//blast the query into the RecordSet
			while(mFrom.moveNext())
			{
				if(mFinalCondition.compare())
				{
					//create an object array to store the records in
					Object[] record = new Object[mFields.size()];	
					
					for(int each=0;each<findex.length;each++)
						record[each]=mFrom.get(findex[each]);
					
					if(!mUnique)
						rs.addRecord(record);
					else if(!rs.hasRecord(record))
						rs.addRecord(record);
					
					if(mFirstOnly)
						break;
				}
			}
		}
		catch(TableException te)
		{
			throw new StatementException("Could not run query.",te,mSQL);
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
			throw new StatementException("Could not close tables.",te,mSQL);
		}
		
		//reset the from, in case we want to use this statement again
		mFrom.reset();
		
		return rs;
	}
	
}

