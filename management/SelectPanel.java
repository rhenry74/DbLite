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
 * 
 */

public class SelectPanel extends WorkPanel
{
	private JTextArea mSqlArea = new JTextArea();
	
	public SelectPanel(String path)
	{
		super(path);
		JButton b = new JButton("Build");
		ButtonListener bl = new ButtonListener();
		b.addActionListener(bl);
		mControlPanel.add(b);
		b = new JButton("Get Record Set");
		b.addActionListener(bl);
		mControlPanel.add(b);
		b = new JButton("Save");
		b.addActionListener(bl);
		mControlPanel.add(b);
		b = new JButton("Load");
		b.addActionListener(bl);
		mControlPanel.add(b);
		
		mWorkPane.setViewportView(mSqlArea);
		
	}
	
	private class ButtonListener implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("Build"))
			{
				build();
			}
			else if(e.getActionCommand().equals("Get Record Set"))
			{
				getSet();
			}
			else if(e.getActionCommand().equals("Save"))
			{
				save();
			}
			else if(e.getActionCommand().equals("Load"))
			{
				load();
				mSqlArea.setText(mStatement.getSQL());
			}
		}
	}

	private void build()
	{
		try
		{
			mStatement = new Select(mPath,mSqlArea.getText());
			mStatement.build();
			append("Ok");
		}
		catch(JordbException t)
		{
			append(t);
		}
	}
	
	private void getSet()
	{
		try
		{
			RecordSet rs = ((Select) mStatement).getRecordSet();
			append("Got " + rs.rowCount() + " rows");
			RecordSetFrame f = new RecordSetFrame(rs);
			append("Ok");
		}
		catch(JordbException je)
		{
			append(je);
		}
		catch(Throwable t)
		{
			append(t.toString());
		}
	}
	
}

