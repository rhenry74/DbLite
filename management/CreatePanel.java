package jordb.ms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.*;
import jordb.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/30/1999
 * Version 2.0 Modified 8/29/2006
 * 
 */

public class CreatePanel extends WorkPanel
{
	private JButton mAddColBut;
	private JButton mDelColBut;
	private JButton mCreateBut;

	private JTextField mTableName = new JTextField("",14);
	public void setTableName(String tname)
	{
		mTableName.setText(tname);
	}
	
	private JTable mDef;
	private DefinitionTableModel mDefModel;
	
	private Vector mColumnNames = new Vector();
	private Vector mColumnClasses = new Vector();

	public void setTableDefinition(Vector col_names, Vector classes)
	{
		mColumnNames = (Vector) col_names.clone();
		mColumnClasses = (Vector) classes.clone();
		mDefModel.fireTableDataChanged();
	}
	
	public CreatePanel(String path)
	{
		super(path);
		mControlPanel.add(new JLabel("Table Name:"));
		mControlPanel.add(mTableName);
		ButtonListener bl = new ButtonListener();

		mAddColBut = new JButton("Add Column");
		mControlPanel.add(mAddColBut);
		mAddColBut.addActionListener(bl);
		
		mDelColBut = new JButton("Delete Column");
		mControlPanel.add(mDelColBut);
		mDelColBut.addActionListener(bl);

		mCreateBut = new JButton("Create Table");
		mControlPanel.add(mCreateBut);
		mCreateBut.addActionListener(bl);
		
		mDefModel = new DefinitionTableModel();
		mDef = new JTable(mDefModel);
		mWorkPane.setViewportView(mDef);
		
	}
	
	private class ButtonListener implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("Add Column"))
			{
				addColumn();
			}
			else if(e.getActionCommand().equals("Create Table"))
			{
				createTable();
			}
			else if (e.getActionCommand().equals("Delete Column"))
			{
				deleteColumn();
			}
		}
	}
	
	private void addColumn()
	{
		ColumnDefiner dlg = new ColumnDefiner(ManagementSystem.sFrame);
		dlg.show();
		
		if(dlg.mOk)
		{
			mColumnNames.add(dlg.mName);
			mColumnClasses.add(dlg.mClass);		
			mDefModel.fireTableDataChanged();
		}
	}

	private void deleteColumn()
	{
		int idx = mDef.getSelectedRow();
		if (idx > -1)
		{
			mColumnNames.remove(idx);
			mColumnClasses.remove(idx);
			mDefModel.fireTableDataChanged();
		}
	}
	
	private void createTable()
	{
		append("Creating table " + mTableName.getText() + " in database " + mPath);
		try
		{
			Table t = new Table(mTableName.getText(),mPath);
			for(int each=0;each<mColumnNames.size();each++)
			{
				t.addColumn((String) mColumnNames.elementAt(each),
					(Class) mColumnClasses.elementAt(each));
			}
			t.create();
			t.close();
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
	
	private class ColumnDefiner extends JDialog
	{
		private JTextField mNameField = new JTextField(9);
		private JComboBox mClassBox = new JComboBox(ManagementSystem.getAllClasses());
		
		public String mName=null;
		public Class mClass=null;
		public boolean mOk=false;
		
		public ColumnDefiner(Frame owner)
		{
			super(owner,"Add Table Column",true);
			this.setSize(300,200);
			Point where = new Point();
			SwingUtilities.convertPointToScreen(where,
				SwingUtilities.findFocusOwner(owner));
			this.setLocation(where);
			
			JPanel conpan1 = new JPanel(); //flow
			conpan1.add(new JLabel("Name:"));
			conpan1.add(mNameField);
			
			JPanel conpan2 = new JPanel(); //flow
			conpan2.add(new JLabel("Class:"));
			conpan2.add(mClassBox);
			
			ButtonListener bl = new ButtonListener();
			
			JPanel conpan3 = new JPanel(); //flow
			JButton b = new JButton("Add Class");
			b.addActionListener(bl);
			conpan3.add(b);
			b = new JButton("Ok");
			b.addActionListener(bl);
			conpan3.add(b);
			b = new JButton("Cancel");
			b.addActionListener(bl);
			conpan3.add(b);
			
			this.getContentPane().setLayout(new GridLayout(3,1));
			this.getContentPane().add(conpan1);
			this.getContentPane().add(conpan2);
			this.getContentPane().add(conpan3);
		}
		
		private class ButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("Ok"))
				{
					mClass=(Class) mClassBox.getSelectedItem();
					mName=mNameField.getText();
					mOk=true;
					dispose();
				}
				else if(e.getActionCommand().equals("Cancel"))
				{
					mOk=false;
					dispose();
				}
				else if(e.getActionCommand().equals("Add Class"))
				{
					
				}
			}
		}
	}
}

