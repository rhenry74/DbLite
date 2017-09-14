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

public class DeletePanel extends WorkPanel
{
	private JTextArea mSqlArea = new JTextArea();
	
	public DeletePanel(String path)
	{
		super(path);
		JButton b = new JButton("Build");
		ButtonListener bl = new ButtonListener();
		b.addActionListener(bl);
		mControlPanel.add(b);
		b = new JButton("Execute");
		b.addActionListener(bl);
		mControlPanel.add(b);
		b = new JButton("Rollback");
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
			else if(e.getActionCommand().equals("Execute"))
			{
				execute();
			}
			else if(e.getActionCommand().equals("Rollback"))
			{
				rollback();
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
			mStatement = new Delete(mPath,mSqlArea.getText());
			mStatement.build();
		}
		catch(JordbException t)
		{
			append(t);
		}
		append("Ok");
	}
	
}

