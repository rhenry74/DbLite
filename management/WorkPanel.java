package jordb.ms;

import javax.swing.*;
import java.awt.*;
import jordb.*;
import java.awt.event.*;

/**
 *
 * @author Robert Henry
 * @Last Updated: 06/30/1999
 * 
 */

public class WorkPanel extends JPanel
{
	protected String mPath;
	protected JScrollPane mWorkPane = new JScrollPane();
	protected JPanel mControlPanel = new JPanel();
	private JTextArea mStatus = new JTextArea();
	
	protected Statement mStatement = null;
	
	public WorkPanel(String path)
	{
		super();
		
		mPath = path;
		
		this.setLayout(new BorderLayout());
		this.add(mWorkPane,"Center");
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(mControlPanel,"North");
		JScrollPane sp = new JScrollPane(mStatus);
		sp.setMinimumSize(new Dimension(500,80));
		sp.setPreferredSize(new Dimension(500,80));
		southPanel.add(sp,"Center");
		this.add(southPanel,"South");
		
	}
	
	protected void append(String message)
	{
		mStatus.append(message);
		mStatus.append("\n");
		
	}
	
	/*
	protected void append(Throwable t)
	{
		mStatus.append(t.getMessage());
		mStatus.append("\n");
	}
	*/
	
	protected void append(JordbException e)
	{
		mStatus.append(e.getMessage());
		e.printStackTrace();
		Throwable source = e.getSource();
		while(source != null)
		{
			mStatus.append("\n" + source.getMessage());
			source.printStackTrace();
			if(source instanceof JordbException)
				source = ((JordbException) source).getSource();
			else
				source = null;
		}
		mStatus.append("\n");
	}
	
	protected void save()
	{
		StatementDefiner dlg = new StatementDefiner(ManagementSystem.sFrame);
		dlg.show();
		
		if(dlg.mOk)
		{
			try
			{
				mStatement.save(dlg.mName);
			}
			catch(JordbException e)
			{
				append(e);
			}
		}
		append("Ok");
	}
	
	protected void load()
	{
		StatementDefiner dlg = new StatementDefiner(ManagementSystem.sFrame);
		dlg.show();
		
		if(dlg.mOk)
		{
			try
			{
				mStatement = Statement.getStatement(mPath,dlg.mName);
			}
			catch(JordbException e)
			{
				append(e);
			}
		}
		append("Ok");
	}

	private class StatementDefiner extends JDialog
	{
		private JTextField mNameField = new JTextField(9);
		
		public String mName=null;
		public boolean mOk=false;
		
		public StatementDefiner(Frame owner)
		{
			super(owner,"Enter Statement Name",true);
			this.setSize(200,100);
			Point where = new Point();
			SwingUtilities.convertPointToScreen(where,
				SwingUtilities.findFocusOwner(owner));
			this.setLocation(where);
			
			JPanel conpan1 = new JPanel(); //flow
			conpan1.add(new JLabel("Name:"));
			conpan1.add(mNameField);
			
			ButtonListener bl = new ButtonListener();
			
			JPanel conpan3 = new JPanel(); //flow
			JButton b = new JButton("Ok");
			b.addActionListener(bl);
			conpan3.add(b);
			b = new JButton("Cancel");
			b.addActionListener(bl);
			conpan3.add(b);
			
			this.getContentPane().setLayout(new GridLayout(2,1));
			this.getContentPane().add(conpan1);
			this.getContentPane().add(conpan3);
		}
		
		private class ButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("Ok"))
				{
					mName=mNameField.getText();
					mOk=true;
					dispose();
				}
				else if(e.getActionCommand().equals("Cancel"))
				{
					mOk=false;
					dispose();
				}
			}
		}
	}

	protected void execute()
	{
		try
		{
			append("Modifyed " + ((ModifyingStatement) mStatement).execute() + " rows");
		}
		catch(JordbException t)
		{
			append(t);
		}
		append("Ok");
	}
	
	protected void rollback()
	{
		try
		{
			((ModifyingStatement) mStatement).rollBack();
		}
		catch(JordbException t)
		{
			append(t);
		}
		append("Ok");
	}

}
