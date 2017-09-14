package jordb;

import java.io.*;
import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see ModifyingStatement
 * 
 */

public class Transaction implements Serializable
{
	private String mPath;
	private Vector mStatements = new Vector();
	private transient int mFailer;
	
	public Transaction(String path)
	{
		mPath=path;
	}
	
	public void save(String filename) throws TableException
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(mPath + "\\" + filename + ".pretrans"));
			oos.writeObject(this);
			oos.close();
		}
		catch(Exception e)
		{
			throw new TableException("Could not write transaction.",e);
		}
	}
	
	public static Transaction getTransaction(String path,String filename) throws TableException
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path + "\\" + filename + ".pretrans"));
			Transaction t = (Transaction) ois.readObject();
			ois.close();
			return t;
		}
		catch(Exception e)
		{
			throw new TableException("Could not read transaction.",e);
		}
	}
	
	public void addStatement(ModifyingStatement st)
	{
		mStatements.addElement(st);
	}
	
	public ModifyingStatement getStatement(int index)
	{
		return (ModifyingStatement) mStatements.elementAt(index);
	}
	
	/**
	Execute the list of Statements.
	**/
	public int execute() throws TransactionException
	{
		int modified=0;
		for(int each=0;each<mStatements.size();each++)
		{
			mFailer=each;
			ModifyingStatement ms = (ModifyingStatement) mStatements.elementAt(each);
			
			int i =0;
			try
			{
				i = ms.execute();
			}
			catch(Exception e)
			{
				throw new TransactionException("Statement would not execute.", 
					TransactionException.NOEXEC, e, ms);
			}
			if(i==0)
				throw new TransactionException("Statement altered no rows.", 
					TransactionException.NOROWS, null, ms);
			
			modified=modified+i;
		}
		return modified;
	}
	
	/**
	Retry the transaction from the failing Statement.
	**/
	public int retry() throws TransactionException
	{
		int modified=0;
		for(int each=mFailer;each<mStatements.size();each++)
		{
			mFailer=each;
			ModifyingStatement ms = (ModifyingStatement) mStatements.elementAt(each);
			
			int i =0;
			try
			{
				i = ms.execute();
			}
			catch(Exception e)
			{
				throw new TransactionException("Statement would not execute.", 
					TransactionException.NOEXEC, e, ms);
			}
			if(i==0)
				throw new TransactionException("Statement altered no rows.", 
					TransactionException.NOROWS, null, ms);
			
			modified=modified+i;
		}
		return modified;
	}
	
	/**
	Continue the transaction skipping the failing Statement.
	**/
	public int cont() throws TransactionException
	{
		int modified=0;
		for(int each=mFailer+1;each<mStatements.size();each++)
		{
			mFailer=each;
			ModifyingStatement ms = (ModifyingStatement) mStatements.elementAt(each);
			
			int i =0;
			try
			{
				i = ms.execute();
			}
			catch(Exception e)
			{
				throw new TransactionException("Statement would not execute.",
					TransactionException.NOEXEC, e, ms);
			}
			if(i==0)
				throw new TransactionException("Statement altered no rows.",
					TransactionException.NOROWS, null, ms);
			
			modified=modified+i;
		}
		return modified;
	}
	
	public void rollBack() throws TransactionException
	{
		for(int each=mFailer;each>=0;each--)
		{
			ModifyingStatement ms = (ModifyingStatement) mStatements.elementAt(each);
			
			try
			{
				ms.rollBack();
			}
			catch(Exception e)
			{
				if(each != mFailer) //we'll let the busted one slide
					throw new TransactionException("Statement would not rollback.", 
						TransactionException.NOROLL, e, ms);
			}
		}
		
	}

}

