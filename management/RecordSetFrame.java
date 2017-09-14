package jordb.ms;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.*;
import jordb.*;
import javax.swing.event.*;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/30/1999
 * 
 */

public class RecordSetFrame extends JInternalFrame
{
	private RecordSet mRS;
	
	public RecordSetFrame(RecordSet rs)
	{
		super("Record Set - " + rs.rowCount() + " rows", true, true, true, true);
		
		mRS = rs;
		
		JTable t = new JTable(new SetTableModel());
		t.setAutoResizeMode(t.AUTO_RESIZE_OFF);
		JScrollPane sp = new JScrollPane(t);
		JPanel p = new JPanel(new GridLayout(1,1));
		p.add(sp);
		this.setContentPane(p);
		this.pack();
		this.setSize(new Dimension(400,400));
		this.addInternalFrameListener(new Disposer());
		
		ManagementSystemFrame.desktop.add(this, ManagementSystemFrame.DOCLAYER);
		try 
		{ 
			this.setSelected(true); 
			this.setVisible(true); 
		} 
		catch (java.beans.PropertyVetoException e2) 
		{
			e2.printStackTrace();
		}
		
	}
	
	private class SetTableModel extends AbstractTableModel
	{
		
		public int getColumnCount() 
		{ 
			return mRS.columnCount();
		}
		
		public String getColumnName(int cidx)
		{
			return mRS.columnName(cidx);
		}
		
		public int getRowCount() 
		{ 
			return mRS.rowCount(); 
		}
		
		public Object getValueAt(int row, int col) 
		{ 
			try
			{
				mRS.moveTo(row);
				return mRS.get(col);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				return null;
			}
		}
	}
	
	private class Disposer extends InternalFrameAdapter
	{
		public void internalFrameClosing(InternalFrameEvent e)
		{
			((JInternalFrame)e.getSource()).dispose();
		}
	}
}

