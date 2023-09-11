/**$*************************************************************
 *file: modifyClassFrame.java (MUFFIN RELATED)                  *
 *this file is to show frames in the muffin proxy for this filt *
 *er. it is not needed for other proxies.                       * 
 *author : Vijay Ganesh. (vganesh@theory.stanford.edu)          *
 *                                                              *
 *also see:                                                     *
 *JavaClass Library Docs.standard java libraries. java vm       *
 *Paper on 'JavaByteCode Modifcation by Inshik Shin'            *
 *other contact: amitp@cs.stanford.edu, mitchell@cs.stanford.edu*
 *                                                              *
 *License :                                                     * 
 *Copyright (C) 1994 Stanford University. All rights reserved.  * 
 *Permission is given to use, copy, and modify this software for*
 *any non-commercial purpose as long as this copyright notice is* 
 *not removed and the author's names are included in the copy-  *
 *right. All other uses, including redistribution in whole or in* 
 *part, are forbidden without prior written permission of the   * 
 *authors.                                                      * 
 *                                                              *
 *Disclaimer:                                                   *
 *This software is provided by the author AS IS.  The author    * 
 *DISCLAIMS any and all warranties of merchantability and fitnes* 
 *for a particular purpose.  In NO event shall the author be    *
 *LIABLE for any damages whatsoever arising in any way out of   * 
 *use of this software.                                         *
 ****************************************************************/
package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;

public class modifyClassFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    modifyClass parent;
    TextField input = null;
    
    public modifyClassFrame (Prefs prefs, modifyClass parent)
    {
	super ("Muffin: modifyClass");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
        panel.setLayout (layout);
	GridBagConstraints c;
	     
	panel.add (new Label ("Content types to modifyClass:", Label.RIGHT));

	input = new TextField (50);
	input.setText (prefs.getString ("modifyClass.contentTypes"));
	c = new GridBagConstraints ();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints (input, c);
	panel.add (input);

	Label label = new Label ("Example: text/class");
	c = new GridBagConstraints ();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints (label, c);
	panel.add (label);

	add ("Center", panel);

	Panel buttonPanel = new Panel ();
	buttonPanel.setLayout (new GridLayout (1, 4));
	Button b;
	b = new Button ("Apply");
	b.setActionCommand ("doApply");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Save");
	b.setActionCommand ("doSave");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Close");
	b.setActionCommand ("doClose");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Help");
	b.setActionCommand ("doHelp");
	b.addActionListener (this);
	buttonPanel.add (b);
	add ("South", buttonPanel);

	addWindowListener (this);
	
	pack ();
	setSize (getPreferredSize ());
	show ();
    }

    public void actionPerformed (ActionEvent event)
    {
	String arg = event.getActionCommand ();
	
	if ("doApply".equals (arg))
	{
	   //prefs.putString ("Preview.contentTypes", input.getText ());
	}
	else if ("doSave".equals (arg))
	{
	   //parent.save ();
	}
	else if ("doClose".equals (arg))
	{
	   //setVisible (false);
	}
	else if ("doHelp".equals (arg))
	{
	   //new HelpFrame ("Preview");
	}
    }

    public void windowActivated (WindowEvent e)
    {
    }
  
    public void windowDeactivated (WindowEvent e)
    {
    }
  
    public void windowClosing (WindowEvent e)
    {
	setVisible (false);
    }
  
    public void windowClosed (WindowEvent e)
    {
    }
  
    public void windowIconified (WindowEvent e)
    {
    }
  
    public void windowDeiconified (WindowEvent e)
    {
    }
  
    public void windowOpened (WindowEvent e)
    {
    }
}
