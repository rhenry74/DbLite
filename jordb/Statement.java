package jordb;

import java.io.*;
import java.util.*;
import java.math.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * Version 2.0 Modified 8/29/2006
 * 
 */

public abstract class Statement implements Serializable
{
	
	protected Condition mFinalCondition=null;
	protected String mSQL;
	protected String mPath;
	protected From mFrom=null;
	protected Vector mValues = new Vector();
	
	protected transient Stack mConditions = new Stack();

	public Statement(String path, String sql)
	{
		mPath=path;
		mSQL=sql;
	}
	
	public String getSQL()
	{
		return mSQL;
	}
	
	public void setValue(int index,Object val)
	{
		mValues.setElementAt(val,index);
	}

	public Object getValue(int index)
	{
		return mValues.elementAt(index);
	}
	
	public static Statement getStatement(String path,String filename) throws TableException
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path + "\\" + filename + ".prestate"));
			Statement s = (Statement) ois.readObject();
			ois.close();
			return s;
		}
		catch(Exception e)
		{
			throw new TableException("Could not read statement.",e);
		}
	}
	
	public void save(String filename) throws TableException
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(mPath + "\\" + filename + ".prestate"));
			oos.writeObject(this);
			oos.close();
		}
		catch(Exception e)
		{
			throw new TableException("Could not write statement.",e);
		}
	}
	
	protected void processWhere(StreamTokenizer st) throws StatementException
	{
		try
		{
			//String conditions=null;
			while(st.ttype==st.TT_NUMBER || !st.sval.equals("orderby"))
			{
				
				//get the left side of the statement
				String lefts = null;
				Object leftn = null;
				boolean leftq = false;
				if(st.ttype==st.TT_WORD)
				{
					lefts = new String(st.sval);
					st.nextToken();
					
					if(isCondition(lefts))
					{
						pushCondition(lefts);
						continue;
					}
				}
				else if(st.ttype==st.TT_NUMBER)
				{
					if (Math.floor(st.nval) == st.nval)
						leftn = new Long((long) st.nval);
					else
						leftn = new Double(st.nval);
					st.nextToken();
				}
				else if(st.ttype == 39 || st.ttype == 34)
				{
					lefts = new String(st.sval);
					leftq = true;
					st.nextToken();
				}
				else
				{
					throw new TableException("Bad syntax parsing WHERE. " + st.toString(),null);
				}
				
				int compare=0;
				if(st.ttype == 61)// '='
				{
					compare = Where.EQUALS;
					st.nextToken();
				}
				else if(st.ttype == 60)// '<'
				{
					compare = Where.LESS;
					st.nextToken();
					if(st.ttype == 61)// '='
					{
						compare = Where.LESS_EQUALS;
						st.nextToken();
					}
				}
				else if(st.ttype == 62)// '>'
				{
					compare = Where.GREATER;
					st.nextToken();
					if(st.ttype == 61)// '='
					{
						compare = Where.GREATER_EQUALS;
						st.nextToken();
						
					}
				}
				else if(st.ttype == 33)// '!'
				{
					compare = Where.NOT_EQUALS;
					st.nextToken();
					if(st.ttype == 61)// '='
					{
						compare = Where.NOT_EQUALS;
						st.nextToken();
						
					}
				}
				
				//get the right side of the statement
				String rights = null;
				Object rightn = null;
				boolean rightq = false;
				if(st.ttype==st.TT_WORD)
				{
					rights = new String(st.sval);
					st.nextToken();
				}
				else if(st.ttype==st.TT_NUMBER)
				{
					if(Math.floor(st.nval) == st.nval)
						rightn = new Long((long )st.nval);
					else
						rightn = new Double(st.nval);
					st.nextToken();
				}
				else if(st.ttype == 39 || st.ttype == 34)
				{
					rights = new String(st.sval);
					rightq = true;
					st.nextToken();
				}
				else
				{
					throw new TableException("Bad syntax parsing WHERE. " + st.toString(),null);
				}
				
				/*
				System.out.println("lefts " + lefts);
				System.out.println("leftn " + leftn);
				System.out.println("leftq " + leftq);
				System.out.println("compare " + compare);
				if(!mConditions.empty())
				System.out.println("conditions " + mConditions.peek());
				System.out.println("rights " + rights);
				System.out.println("rightn " + rightn);
				System.out.println("rightq " + rightq);
				*/
				
				if(mConditions.empty())
				{
					//create as simple where and set it as the final condition
					createWhere(lefts,leftn,leftq,compare,rights,rightn,rightq);
				}
				else
				{
					//string the conditions together
					LogicalCondition lc = (LogicalCondition) mConditions.pop();
					lc.setLeft(mFinalCondition);
					createWhere(lefts,leftn,leftq,compare,rights,rightn,rightq);
					lc.setRight(mFinalCondition);
					mFinalCondition=lc;
				}
				
				
				if(st.ttype==st.TT_EOF)
				{
					//no orderby clause, whoohoo
					break;
				}
				
			}
		}
		catch(Exception e)
		{
			throw new StatementException("Could not parse WHERE clause.",e,mSQL);
		}
		
	}
	
	protected boolean isCondition(String s)
	{
		if(s.equals("and") || s.equals("or"))
			return true;
		return false;
	}
	
	protected void pushCondition(String s)
	{
		if(s.equals("and"))
			mConditions.push(new And());
		else if(s.equals("or"))
			mConditions.push(new Or());
		
	}
	
	protected void createWhere(String ls,Object ln,boolean lq,int compare,
		String rs,Object rn,boolean rq) throws StatementException
	{
		//create a key to switch on
		int key=0;
		key = key + (lq ? 1 : 0);
		key = key << 1;
		key = key + (rq ? 1 : 0);
		key = key << 1;
		key = key + ((ln!=null) ? 1 : 0);
		key = key << 1;
		key = key + ((ls!=null) ? 1 : 0);
		key = key << 1;
		key = key + ((rn!=null) ? 1 : 0);
		key = key << 1;
		key = key + ((rs!=null) ? 1 : 0);
		
		//System.out.println(key);
		
		switch(key)
		{
		case 0x9://1001 left number right string
			mValues.addElement(castNumber(mFrom.columnType(rs),ln));
			mFinalCondition = new Where(this,mFrom,mValues.size()-1,mFrom.columnIndex(rs),compare);
			return;
		case 0x6://0110 left string right number
			mValues.addElement(castNumber(mFrom.columnType(ls),rn));
			mFinalCondition = new Where(mFrom,this,mFrom.columnIndex(ls),mValues.size()-1,compare);
			return;
		case 0x5://0101 left string right string
			mFinalCondition = new Where(mFrom,mFrom.columnIndex(ls),mFrom.columnIndex(rs),compare);
			return;
		case 0x25://100101 left quoted string right string
			mValues.addElement(new String(ls));
			mFinalCondition = new Where(this,mFrom,mValues.size()-1,mFrom.columnIndex(rs),compare);
			return;
		case 0x15://010101 left quoted string right string
			mValues.addElement(new String(rs));
			mFinalCondition = new Where(mFrom,this,mFrom.columnIndex(ls),mValues.size()-1,compare);
			return;
		default:
			throw new StatementException("Ill-formed WHERE. " + key,null,mSQL);
		}
	}
	
	protected Object castNumber(Class to, Object from) throws StatementException
	{
		
		BigDecimal bd = new BigDecimal(((Number) from).doubleValue());
		
		Object cast = null;
		if(to.equals(Integer.class))
			cast = new Integer(bd.intValue());
		else if(to.equals(Long.class))
			cast = new Long(bd.longValue());
		else if(to.equals(Float.class))
			cast = new Float(bd.floatValue());
		else if (to.equals(Short.class))
			cast = new Short(bd.shortValue());
		else if (to.equals(Byte.class))
			cast = new Byte(bd.byteValue());
		else if (to.equals(Double.class))
			cast = from;
		else
		{
			try
			{
				cast = from;
			}
			catch(ClassCastException castex)
			{
				throw new StatementException("Cannot cast number " + from + " to " + to.toString(),castex,mSQL);
			}
		}
		
		return cast;
	}
	
	protected void dump(StreamTokenizer st) throws Exception
	{
		while(st.nextToken()!=st.TT_EOF)
		{
			if(st.ttype==st.TT_NUMBER)
				System.out.println("number:" + st.nval + " " + st.toString());
			else if(st.ttype==st.TT_WORD)
				System.out.println("word:" + st.sval);
			else
			{
				System.out.println("special:" + st.ttype);
				if(st.ttype==34)
					System.out.println("quote:" + st.sval);
				if(st.ttype==39)
					System.out.println("tick:" + st.sval);
				if(st.ttype==40)
					System.out.println("paren:" + st.sval);
			}
		}
		
	}
	
	abstract public void build() throws StatementException;
}

