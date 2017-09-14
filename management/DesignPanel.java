package jordb.ms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.*;
import javax.swing.event.*;

import jordb.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/30/1999
 * Version 2.0 Modified 8/29/2006
 * 
 */

public class DesignPanel extends WorkPanel
{
	protected JTextField mTableName = new JTextField("",14);
	
	private JTable mDef;
	private DefinitionTableModel mDefModel;
	
	private Vector mColumnNames = new Vector();
	private Vector mColumnClasses = new Vector();
	
	private String mDatabasePath = "";

	public DesignPanel(String path)
	{
		super(path);
		mDatabasePath = path;

		mControlPanel.add(new JLabel("Table Name:"));
		mControlPanel.add(mTableName);
		JButton b = new JButton("Show Design");
		ButtonListener bl = new ButtonListener();
		b.addActionListener(bl);
		mControlPanel.add(b);
		
		b = new JButton("Alter");
		b.addActionListener(bl);
		mControlPanel.add(b);
		
		mDefModel = new DefinitionTableModel();
		mDef = new JTable(mDefModel);
		mWorkPane.setViewportView(mDef);
		
	}
	
	private class ButtonListener implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("Show Design"))
			{
				showDesign();
			}
			else if (e.getActionCommand().equals("Alter"))
			{
				System.out.println("new Alter Table Frame");
				JInternalFrame fr = new JInternalFrame("Alter Table", true, true, true, true);

				CreatePanel p = new CreatePanel(mDatabasePath);
				p.setTableName(mTableName.getText());
				p.setTableDefinition(mColumnNames, mColumnClasses);
				fr.setContentPane(p);
				fr.pack();
				fr.setSize(new Dimension(600, 400));
				fr.addInternalFrameListener(new Disposer());

				ManagementSystemFrame.desktop.add(fr, ManagementSystemFrame.DOCLAYER);
				try
				{
					fr.setSelected(true);
					fr.setVisible(true);
				}
				catch (java.beans.PropertyVetoException e2)
				{
					e2.printStackTrace();
				}
			}
		}
	}

	public class Disposer extends InternalFrameAdapter
	{
		public void internalFrameClosing(InternalFrameEvent e)
		{
			((JInternalFrame)e.getSource()).dispose();
		}
	}

	private void showDesign()
	{
		try
		{
			mColumnClasses.clear();
			mColumnNames.clear();

			Table t = new Table(mTableName.getText(),mPath);
			t.readDesign();
			
			for(int each=0;each<t.columnCount();each++)
			{
				Class c = t.columnType(each);
				mColumnClasses.addElement(c);
				String s = t.columnName(each);
				mColumnNames.addElement(s);
			}
			
			mDefModel.fireTableDataChanged();
		}
		catch(JordbException t)
		{
			append(t);
		}
		append("Ok");
	}
	
	private class DefinitionTableModel extends AbstractTableModel
	{
		private final String[] cname = {new String("Name"), new String("Class")};
		
		public int getColumnCount() 
		{ 
			return 2;
		}
		
		public String getColumnName(int cidx)
		{
			return cname[cidx];
		}
		
		public int getRowCount() 
		{ 
			return mColumnNames.size(); 
		}
		public Object getValueAt(int row, int col) 
		{
			if (row == -1) { return null; }
			if(col==0)
			{
				return mColumnNames.elementAt(row);
			}
			else
			{
				return mColumnClasses.elementAt(row);
			}
		}
	}
	
}

