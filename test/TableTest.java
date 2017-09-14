package jordb.test;

import jordb.*;
import java.util.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * 
 */

public class TableTest
{
	
	//create a directory to store the data files in on your hard drive
	private static String sTableBase = new String("/henryr/jordbtestdata");
	
	public static void main(String[] args)
	{
		
		try
		{
			testTable();
			
			genRocks();
			
			//testJoin();
			
			testSelect();
			
			testInsert();
			
			testDelete();
			
			testUpdate();
			
			testParameter();
			
			testTransaction();
			
			testOO();
		}
		catch(JordbException e)
		{
			System.out.println(e.getMessage());
			boolean dumping = !(e.getSource()==null);
			while(dumping)
			{
				System.out.println(e.getSource().getMessage());
				if(e.getSource() instanceof JordbException)
					e = (JordbException) e.getSource();
				else
					dumping=false;
			}
		}
		
	}
	
	
	
	private static void testTable() throws TableException
	{
		
		
		Table table = new Table("employee",sTableBase);
		
		table.addColumn("first",String.class);
		table.addColumn("last",String.class);
		table.addColumn("ssn",String.class);
		table.addColumn("rate",Double.class);
		table.addColumn("years",Integer.class);
		
		table.create();
		
		/*
		String first = new String();
		String last = new String();
		String ssn = new String();
		Double rate = new Double();
		Integer years = new Integer();
		*/
		table.set(0,new String("Fred"));
		table.set(1,new String("Flintstone"));
		table.set(2,new String("012-34-5678"));
		table.set(3,new Double(12.50));
		table.set(4,new Integer(5));
		table.insert();
		
		table.set(0,new String("Mark"));
		table.set(1,new String("Stone"));
		table.set(2,new String("667-56-3337"));
		table.set(3,new Double(10.00));
		table.set(4,new Integer(2));
		table.insert();
		
		table.set(0,new String("Barney"));
		table.set(1,new String("Rubble"));
		table.set(2,new String("012-56-8765"));
		table.set(3,new Double(10.50));
		table.set(4,new Integer(4));
		table.insert();
		
		table.set(0,new String("Andy"));
		table.set(1,new String("Shale"));
		table.set(2,new String("617-16-3137"));
		table.set(3,new Double(15.00));
		table.set(4,new Integer(5));
		table.insert();
		
		table.set(0,new String("Harry"));
		table.set(1,new String("Slate"));
		table.set(2,new String("231-56-4567"));
		table.set(3,new Double(25.00));
		table.set(4,new Integer(8));
		table.insert();
		
		table.set(0,new String("Chip"));
		table.set(1,new String("Rock"));
		table.set(2,new String("211-44-6723"));
		table.set(3,new Double(17.25));
		table.set(4,new Integer(3));
		table.insert();
		
		table.set(0,new String("Louis"));
		table.set(1,new String("Coal"));
		table.set(2,new String("111-24-6453"));
		table.set(3,new Double(17.50));
		table.set(4,new Integer(6));
		table.insert();
		
		table.set("last",new String("Ruby"));
		table.set("first",new String("James"));
		table.set("ssn",new String("221-24-9871"));
		table.set("rate",new Double(14.50));
		table.set("years",new Integer(3));
		table.insert();
		
		table.set(0,new String("Felix"));
		table.set(1,new String("Diamond"));
		table.set(2,new String("356-14-6334"));
		table.set(3,new Double(16.50));
		table.set(4,new Integer(7));
		table.insert();
		
		table.close();
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(true)); //read only
		
		while(table.currentRow() < table.rowCount())
		{
			System.out.println(table.moveNext());
			System.out.println(table.get(0));
			System.out.println(table.get(1));
			System.out.println(table.get(2));
			System.out.println(table.get(3));
			System.out.println(table.get(4));
		}
		table.close();
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(false)); //update
		
		while(table.moveNext())
		{
			if(((String) table.get(1)).equals("Rubble"))
			{
				table.set(3,new Double(13.75)); //change Barney's rate
				table.update();
				break;
			}
		}
		table.close();			
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(true)); //read only
		
		while(table.currentRow() < table.rowCount())
		{
			System.out.println(table.moveNext());
			System.out.println(table.get(0));
			System.out.println(table.get(1));
			System.out.println(table.get(2));
			System.out.println(table.get(3));
			System.out.println(table.get(4));
		}			
		table.close();
		
		System.out.println("--------------------Testing delete");
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(false));
		
		while(table.moveNext())
		{
			if(((String) table.get(1)).equals("Stone"))//delete mister Stone
			{
				table.delete();
				break;
			}
		}
		
		table.close();
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(true)); //read only
		
		while(table.currentRow() < table.rowCount())
		{
			System.out.println(table.moveNext());
			System.out.println(table.get(0));
			System.out.println(table.get(1));
			System.out.println(table.get(2));
			System.out.println(table.get(3));
			System.out.println(table.get(4));
		}			
		table.close();
		
		
		System.out.println("--------------------Testing insert into");
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(false));
		
		while(table.moveNext())
		{
			if(((String) table.get(1)).equals("Shale"))//insert Mr. Stone
			{
				table.update();//write Mr.Shale
				table.set(0,new String("Mark"));
				table.set(1,new String("Stone"));
				table.set(2,new String("667-56-3337"));
				table.set(3,new Double(10.00));
				table.set(4,new Integer(4));
				table.insert();
				break;
			}
		}
		table.close();
		
		table = new Table("employee",sTableBase);
		System.out.println(table.open(true)); //read only
		
		for(int each=0;each<table.columnCount();each++)
		{
			System.out.println(table.columnName(each) + "->" + table.columnType(each));
		}
		
		while(table.currentRow() < table.rowCount())
		{
			System.out.println(table.moveNext());
			System.out.println(table.getID());
			System.out.println(table.getTime());
			System.out.println(table.get("last"));
			System.out.println(table.get("first"));
			System.out.println(table.get("rate"));
			System.out.println(table.get("years"));
			System.out.println(table.get("ssn"));
		}			
		table.close();
		
		
	}
	
	private static void genRocks() throws TableException
	{
		//create a family of rockosaurs
		Rockosaur[] dinos = genDinos();
		
		//for each employee generate a years worth of productivity data
		
		//design the prod table
		Table prod = new Table("product",sTableBase);
		prod.addColumn("date",Date.class);
		prod.addColumn("empid",String.class);
		prod.addColumn("tons",Integer.class);
		prod.addColumn("rockosaur",Rockosaur.class);
		prod.create();
		
		Table emptab = new Table("employee",sTableBase);
		emptab.open(true); //read only
		
		while(emptab.currentRow() < emptab.rowCount())
		{
			emptab.moveNext();
			
			String id = (String) emptab.get("ssn");
			
			Calendar date = Calendar.getInstance();
			date.clear();//reset the time values
			date.set(1997,1,1);
			
			prod.set("empid",id);
			
			Random rand = new Random(Calendar.getInstance().getTime().getTime());
			
			int saurcnt=0;
			for(int each=0;each<365;each++)
			{
				int tons = rand.nextInt(1000);
				if(tons < 100)
					tons = 0;
				if(tons>0)
				{
					prod.set("date",new Date());
					
					prod.set("rockosaur",dinos[saurcnt]);
					int ti = (int) (tons * dinos[saurcnt].getEffeciency());
					prod.set("tons",new Integer(ti));
					
					saurcnt++;
					if(saurcnt==dinos.length)
						saurcnt=0;
					
					prod.insert();
				}
				date.roll(date.DAY_OF_YEAR,true);
			}
			
		}
		prod.close();
		emptab.close();
		
	}
	
	/*
	changes to Where to support parameters made this test defunct
	private static void testJoin() throws TableException
	{
	
	Table emptab = new CachedTable("employee",sTableBase);
	emptab.open(true); //read only
	
	Table prod = new CachedTable("product",sTableBase);
	prod.open(true); //read only
	
	From from = new From();
	from.addTable(emptab);
	from.addTable(prod);
	
	Where join = new Where(from,from.columnIndex("employee.ssn"),from.columnIndex("product.empid"),Where.EQUALS);
	
	Where tonsless = new Where(from,from.columnIndex("product.tons"),new Integer(200),Where.LESS);
	
	Where servemore = new Where(from,new Integer(6),from.columnIndex("employee.years"),Where.LESS_EQUALS);
	
	And yearsandtons = new And(servemore,tonsless);
	
	And finalwhere = new And(yearsandtons,join);
	
	System.out.println("GO!");
	while(from.moveNext())
	{
	if(finalwhere.compare())
	System.out.println(emptab.get("last") + " " + prod.get("date") + " " + prod.get("tons") + 
	" " + emptab.get("years"));
	}
	
	emptab.close();
	prod.close();
	}
	*/
	
	private static void testSelect() throws TableException
	{
		
		Select query = new Select(sTableBase,
			"select employee.last, product.date, product.tons " +
			"from employee, product " +
			"where employee.ssn=product.empid " +
			"and product.tons<200 " +
			"and 6 <= employee.years " 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		
		System.out.println("------------------");
		
		query = new Select(sTableBase,
			"select employee.last, employee.first, product.date, employee.rate, product.tons, product.rockosaur " +
			"from employee, product " +
			"where employee.ssn = product.empid " +
			"and product.tons < 200 " +
			"and employee.rate >= 15.0 " +
			"and employee.last != 'Slate'" //coz he's the boss
			);
		
		query.build();
		query.save("overpaid");
		
		
		System.out.println("------------------");
		
		query = (Select) Select.getStatement(sTableBase,"overpaid");
		
		rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}		
	}
	
	private static Rockosaur[] genDinos()
	{
		//create a family of rockosaurs
		Rockosaur[] dinos = new Rockosaur[6];
		
		//mom
		dinos[0] = new Rockosaur("Iggy");
		dinos[0].setDOB(new Date(1879,2,12));
		dinos[0].setEffeciency(0.32);
		dinos[0].setHeight(234.3);
		dinos[0].setWeight(1223);
		
		//dead dad
		dinos[1] = new Rockosaur("Ziggy");
		dinos[1].setDOB(new Date(1843,7,2));
		dinos[1].setEffeciency(0.0);
		dinos[1].setHeight(334.3);
		dinos[1].setWeight(1423);
		
		//son
		dinos[2] = new Rockosaur("Biggy");
		dinos[2].setDOB(new Date(1921,3,2));
		dinos[2].setEffeciency(0.88);
		dinos[2].setHeight(312.93);
		dinos[2].setWeight(1388);
		dinos[2].setMother(dinos[0]);
		dinos[2].setFather(dinos[1]);
		
		//son's wife
		dinos[3] = new Rockosaur("Twiggy");
		dinos[3].setDOB(new Date(1934,11,15));
		dinos[3].setEffeciency(0.71);
		dinos[3].setHeight(212.93);
		dinos[3].setWeight(1188);
		
		//son
		dinos[4] = new Rockosaur("Piggy");
		dinos[4].setDOB(new Date(1944,4,5));
		dinos[4].setEffeciency(0.79);
		dinos[4].setHeight(292.2);
		dinos[4].setWeight(1276);
		dinos[4].setMother(dinos[0]);
		dinos[4].setFather(dinos[1]);
		
		//grandson
		dinos[5] = new Rockosaur("Wiggy");
		dinos[5].setDOB(new Date(1978,2,8));
		dinos[5].setEffeciency(0.51);
		dinos[5].setHeight(192.0);
		dinos[5].setWeight(876);
		
		dinos[2].setChild(dinos[5]);
		dinos[3].setChild(dinos[5]);
		
		return dinos;
	}
	
	private static void testOO() throws TableException
	{
		System.out.println("who is Biggy's son?");
		
		Select query = new Select(sTableBase,
			"select first product.rockosaur " +
			"from product " +
			"where product.rockosaur = 'Biggy' " 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		rs.moveNext(); //only want to see the first row
		String s = rs.currentRow() + "->" +
			rs.columnName(0) + " " +
			rs.columnType(0).toString() + "=" +
			rs.get(0);
		System.out.println(s);
		
		Rockosaur biggy = (Rockosaur) rs.get(0);
		Rockosaur child = biggy.getChild();
		System.out.println(child);
	}
	
	private static void testInsert() throws TableException
	{
		/*
		table.addColumn("first",String.class);
		table.addColumn("last",String.class);
		table.addColumn("ssn",String.class);
		table.addColumn("rate",Double.class);
		table.addColumn("years",Integer.class);
		*/
		
		System.out.println("~~~~~~~~~~Insert statement~~~~~~~~~~~");
		
		Insert newemp = new Insert(sTableBase,
			"insert into employee " +
			"(employee.last, employee.first, employee.ssn, employee.rate, employee.years) " +
			"values ('Slate', 'Nancy', \"456-22-1232\", 6.75, 1) " 
			);
		
		newemp.build();
		newemp.execute();
		
		
		Select query = new Select(sTableBase,
			"select * " +
			"from employee" 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		System.out.println("~~~~~~~~~~Insert rollback~~~~~~~~~~~");
		newemp.rollBack();
		
		rs=query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
	}
	
	private static void testDelete() throws TableException
	{
		
		System.out.println("~~~~~~~~~~Delete statement~~~~~~~~~~~");
		
		//		Delete byebarn = new Delete(sTableBase,
		//			"delete from employee " 
		//			);
		Delete byeboys = new Delete(sTableBase,
			"delete from employee " +
			"where employee.rate < 13.00 " +
			"or employee.years < 4"
			);
		
		byeboys.build();
		byeboys.execute();
		
		Select query = new Select(sTableBase,
			"select * " +
			"from employee" 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		System.out.println("~~~~~~~~~~Delete rollback~~~~~~~~~~~");
		byeboys.rollBack();
		
		rs=query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
	}
	
	private static void testUpdate() throws TableException
	{
		
		System.out.println("~~~~~~~~~~Update statement~~~~~~~~~~~");
		
		Update raise = new Update(sTableBase,
			"update employee " +
			"set employee.rate=14.00, employee.years=6 " +
			"where employee.last='Rubble' " +
			"and employee.first='Barney'"
			);
		
		raise.build();
		raise.execute();
		
		Select query = new Select(sTableBase,
			"select * " +
			"from employee" 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		System.out.println("~~~~~~~~~~Update rollback~~~~~~~~~~~");
		raise.rollBack();
		
		rs=query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
	}
	
	private static void testParameter() throws TableException
	{
		System.out.println("~~~~Barney gets a raise~~~~~~~~~");
		Update raise = new Update(sTableBase,
			"update employee " +
			"set employee.rate=0.0, employee.years=0 " +
			"where employee.last='Rubble' " +
			"and employee.first='Barney'"
			);
		
		raise.build();
		
		raise.setValue(0,new Double(14.25));
		raise.setValue(1,new Integer(6));
		
		raise.execute();
		
		Select query = new Select(sTableBase,
			"select * " +
			"from employee" 
			);
		
		query.build();
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		System.out.println("~~~~Barney's statement is used to give Fred a raise~");
		raise.setValue(0,new Double(12.25));
		raise.setValue(1,new Integer(5));
		raise.setValue(2,new String("Flintstone"));
		raise.setValue(3,new String("Fred"));
		
		raise.execute();
		
		rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
		//how cool is that?
	}
	
	static void testTransaction()  throws TableException
	{
		
		System.out.println("~~~~Find the poor performers~~~~~~~~~");
		Select query = new Select(sTableBase,
			"select unique employee.last, employee.first, employee.ssn " +
			"from employee, product " +
			"where employee.ssn = product.empid " +
			"and product.tons < 200 " +
			"and employee.rate >= 15.0 " +
			"and employee.last != 'Slate' " +//coz he's the boss
			"and product.rockosaur != 'Iggy' " +//coz she's old
			"and product.rockosaur != 'Wiggy' " //coz he's young
			);
		
		query.build();
		
		//create a transaction
		Delete fireemp = new Delete(sTableBase,
			"delete from employee " +
			"where employee.ssn = '000-00-0000' "
			);
		
		fireemp.build();
		
		Delete remprod = new Delete(sTableBase,
			"delete from product " +
			"where product.empid = '000-00-0000' "
			);
		
		remprod.build();
		
		Transaction fire = new Transaction(sTableBase);
		//add only built statements to a transaction
		fire.addStatement(fireemp);
		fire.addStatement(remprod);
		fire.save("fireemployee");
		
		fireemp=null;
		remprod=null;
		
		//a precompiled transaction
		Transaction loadedfire = Transaction.getTransaction(sTableBase,"fireemployee");
		
		System.out.println("---------running fireemployee");
		
		//open the production table so the transaction will fail
		Table failer = new Table("employee",sTableBase);
		failer.open(true);
		
		RecordSet rs = query.getRecordSet();
		
		while(rs.moveNext())
		{
			System.out.println("Firing " + rs.get(0) + " " + rs.get(1) + " " + rs.get(2));
			loadedfire.getStatement(0).setValue(0,rs.get(2));//substitute params	
			loadedfire.getStatement(1).setValue(0,rs.get(2));
			try
			{
				loadedfire.execute();
			}
			catch(TransactionException te)
			{
				if(te.getCode()==te.NOEXEC)
				{
					failer.close();
					loadedfire.retry();
				}
			}
			
			//lets unfire ol' Chip
			if(((String) rs.get(1)).equals("Chip"))
				loadedfire.rollBack();
			
		}
		
		Select dump = new Select(sTableBase,
			"select * " +
			"from employee" 
			);
		
		dump.build();
		
		rs = dump.getRecordSet();
		
		while(rs.moveNext())
		{
			for(int each=0;each<rs.columnCount();each++)
			{
				String s = rs.currentRow() + "->" +
					rs.columnName(each) + " " +
					rs.columnType(each).toString() + "=" +
					rs.get(each);
				System.out.println(s);
			}
		}
		
	}
} // TableTest

