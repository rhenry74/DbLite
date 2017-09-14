package jordb.test;

import java.util.*;
import java.io.*;

/**
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
**/

class Rockosaur implements Serializable, Comparable
{
	private String mName;
	public String getName() {return mName;}
	private Date mDOB;
	private double mEffeciency;
	public double getEffeciency() {return mEffeciency;}
	public void setEffeciency(double d){mEffeciency = d;}
	private double mWeight;
	public double getWeight() {return mWeight;}
	public void setWeight(double d){mWeight = d;}
	private double mHeight;
	public double getHeight() {return mHeight;}
	public void setHeight(double d){mHeight = d;}
	
	private Rockosaur mFather=null;
	public Rockosaur getFather() {return mFather;}
	public void setFather(Rockosaur r){mFather = r;}
	private Rockosaur mMother=null;
	public Rockosaur getMother() {return mMother;}
	public void setMother(Rockosaur r){mMother = r;}
	private Rockosaur mChild=null;
	public Rockosaur getChild() {return mChild;}
	public void setChild(Rockosaur r){mChild = r;}
	
	public Rockosaur(String name)
	{
		mName = new String(name);
	}
	

	public void setDOB(Date date)
	{
		mDOB = new Date(date.getTime());
	}
	
	public int getAge()
	{
		Calendar aCal = Calendar.getInstance();
		aCal.setTime(mDOB);
		
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR) - aCal.get(Calendar.YEAR);
		
	}
	
	public String toString()
	{
		return mName;
	}
	
	public int compareTo(Object o)
	{
		return mName.compareTo(o);
	}
}

