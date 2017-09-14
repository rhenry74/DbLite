package jordb.ms;

/*
 * Adapted from:
 * MetalworksFrame.java	1.10 98/08/26
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * Modified for jordb Version 1.0 1999
 * Version 2.0 Modified 8/29/2006
 * by Robert Alan Henry
 */

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import javax.swing.plaf.metal.*;


/**
 * This is the main container frame for the Metalworks demo app
 *
 * @version 1.10 08/26/98
 * @author Steve Wilson
 * 
 * Modified for jordb by Robert Henry 1998
 */
public class ManagementSystemFrame extends JFrame {
	
	JMenuBar menuBar;
	public static JDesktopPane desktop;
	JInternalFrame toolPalette;
	JCheckBoxMenuItem showToolPaletteMenuItem;
	
	String mDatabasePath = new String("C:\\jordb");
	
	public static final Integer DOCLAYER = new Integer(5);
	public static final Integer TOOLLAYER = new Integer(6);
	public static final Integer HELPLAYER = new Integer(7);
	
	static final String ABOUTMSG = "Metalworks \n \nAn application written to show off the Java Look & Feel. \n \nWritten by the JavaSoft Look & Feel Team \n  Michael Albers\n  Tom Santos\n  Jeff Shapiro\n  Steve Wilson";
	
	
	public ManagementSystemFrame() {
		super("Java Object Relational Database Management System");
		final int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds ( inset, inset, screenSize.width - inset*2, screenSize.height - inset*2 );
		buildContent();
		buildMenus();
		this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					quit();
				}});
		UIManager.addPropertyChangeListener(new UISwitchListener((JComponent)getRootPane()));		
	}
	
	protected void buildMenus() {
		menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		JMenu file = buildFileMenu();
		JMenu edit = buildEditMenu();
		JMenu views = buildViewsMenu();
		JMenu help = buildHelpMenu();
		
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(views);
		menuBar.add(help);
		setJMenuBar(menuBar);	
	}
	
	protected JMenu buildFileMenu() {
		JMenu file = new JMenu("File");
		//JMenuItem newWin = new JMenuItem("New Database");
		JMenuItem open = new JMenuItem("Open Database");
		JMenuItem quit = new JMenuItem("Quit");
		/*
		newWin.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		newDatabase();
		}});
		 */
		open.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openDatabase();
				}});
		
		quit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}});
		
		//file.add(newWin);
		file.add(open);
		file.addSeparator();
		file.add(quit);
		return file;
	}
	
	protected JMenu buildEditMenu() {
		JMenu edit = new JMenu("Edit");
		JMenuItem undo = new JMenuItem("Undo");
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem paste = new JMenuItem("Paste");
		JMenuItem prefs = new JMenuItem("Preferences...");
		
		undo.setEnabled(false);
		copy.setEnabled(false);
		cut.setEnabled(false);
		paste.setEnabled(false);
		
		prefs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openPrefsWindow();
				}});
		
		edit.add(undo);
		edit.addSeparator();
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(prefs);
		return edit;
	}
	
	protected JMenu buildViewsMenu() {
		JMenu views = new JMenu("Views");
		
		JMenuItem create = new JMenuItem("Create Table");
		JMenuItem design = new JMenuItem("Show Design");
		JMenuItem select = new JMenuItem("Select");
		JMenuItem insert = new JMenuItem("Insert");
		JMenuItem delete = new JMenuItem("Delete");
		JMenuItem update = new JMenuItem("Update");
		JMenuItem mm = new JMenuItem("Memory Monitor");
		
		create.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openCreate();
				}});
		design.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openDesign();
				}});
		select.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openSelect();
				}});
		insert.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openInsert();
				}});
		delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openDelete();
				}});
		update.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openUpdate();
				}});
		mm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openMM();
				}});
		
		views.add(create);
		views.add(design);
		views.add(select);
		views.add(insert);
		views.add(delete);
		views.add(update);
		views.add(mm);
		return views;
	}
	
	protected JMenu buildHelpMenu() {
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About...");
		JMenuItem openHelp = new JMenuItem("Open Help Window");
		
		about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showAboutBox();
				}
			});
		
		openHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openHelpWindow();
				}
			});
		
		help.add(about);
		help.add(openHelp);
		
		return help;
	}
	
	protected void buildContent() {
		desktop = new JDesktopPane();
		getContentPane().add(desktop);
	}
	
	public void quit() {
		System.exit(0);
	}
	
	/* there is no new database, you just have to create the directory
	public void newDatabase() 
	{
	
	JInternalFrame doc = new MetalworksDocumentFrame();
	desktop.add(doc, DOCLAYER);
	try 
	{ 
	doc.setSelected(true); 
	} 
	catch (java.beans.PropertyVetoException e2) 
	{}
	
	}
	 */
	
	public void openDatabase() {
		JFileChooser chooser = new JFileChooser(mDatabasePath);
		chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(this);
		mDatabasePath = new String(chooser.getSelectedFile().getPath());
		System.out.println(mDatabasePath);
	}
	
	private class Disposer extends InternalFrameAdapter
	{
		public void internalFrameClosing(InternalFrameEvent e)
		{
			((JInternalFrame)e.getSource()).dispose();
		}
	}
	
	public void openHelpWindow() 
	{
		//JInternalFrame help = new ManagementSystemHelp();
		//desktop.add(help, HELPLAYER);
		//help.addInternalFrameListener(new Disposer());
		//try 
		//{ 
		//    help.setSelected(true); 
		//    help.setVisible(true); 
		//} catch (java.beans.PropertyVetoException e2) {}
	}
	
	public void showAboutBox() 
	{
		JOptionPane.showMessageDialog(this, ABOUTMSG);
	}
	
	public void openPrefsWindow() 
	{
		
	}
	
	public void openCreate() 
	{
		System.out.println("new Create Table Frame");
		JInternalFrame fr = new JInternalFrame("Create Table", true, true, true, true);
		fr.setContentPane(new CreatePanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(600,400));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openDesign()
	{
		
		JInternalFrame fr = new JInternalFrame("Table Design", true, true, true, true);
		fr.setContentPane(new DesignPanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(500,400));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openSelect() 
	{
		JInternalFrame fr = new JInternalFrame("Select Statement", true, true, true, true);
		fr.setContentPane(new SelectPanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(500,300));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openInsert() 
	{
		
		JInternalFrame fr = new JInternalFrame("Insert Statement", true, true, true, true);
		fr.setContentPane(new InsertPanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(500,300));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openDelete() 
	{
		JInternalFrame fr = new JInternalFrame("Delete Statement", true, true, true, true);
		fr.setContentPane(new DeletePanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(500,300));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openUpdate() 
	{
		JInternalFrame fr = new JInternalFrame("Update Statement", true, true, true, true);
		fr.setContentPane(new UpdatePanel(mDatabasePath));
		fr.pack();
		fr.setSize(new Dimension(500,300));
		fr.addInternalFrameListener(new Disposer());
		
		desktop.add(fr, DOCLAYER);
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
	
	public void openMM() 
	{
		//JInternalFrame fr = new JInternalFrame("Memory", true, true, true, true);
		//fr.setContentPane(new MemoryMonitor());
		//fr.pack();
		//fr.setSize(new Dimension(200,200));
		//fr.addInternalFrameListener(new Disposer());
		
		//desktop.add(fr, DOCLAYER);
		//try 
		//{ 
		//    fr.setSelected(true); 
		//    fr.setVisible(true); 
		//} 
		//catch (java.beans.PropertyVetoException e2) 
		//{
		//    e2.printStackTrace();
		//}
		
	}
	
	
}


