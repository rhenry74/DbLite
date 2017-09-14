package jordb.ms;

/*
 * @(#)Metalworks.java	1.5 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * This application is a demo of the Metal Look & Feel
 *
 * @version 1.5 08/26/98
 * @author Steve Wilson
 */
public class ManagementSystem 
{
	
	private static Vector sAllClasses = new Vector();
	public static JFrame sFrame=null;
	
	public static void main( String[] args ) 
	{
		
		initClasses();
		
		sFrame = new ManagementSystemFrame();
		sFrame.setVisible(true);
	}
	
	private static void initClasses()
	{
		sAllClasses.addElement(java.lang.Integer.class);
		sAllClasses.addElement(java.lang.String.class);
		sAllClasses.addElement(java.lang.Double.class);
		sAllClasses.addElement(java.lang.Float.class);
		sAllClasses.addElement(java.lang.Long.class);
		sAllClasses.addElement(java.lang.Short.class);
		sAllClasses.addElement(java.lang.Byte.class);
		sAllClasses.addElement(java.util.Date.class);
	}
	
	public static Vector getAllClasses()
	{
		return sAllClasses;
	}
}
