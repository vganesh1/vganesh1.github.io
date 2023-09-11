/**$*************************************************************
 *file: modifyMethod.java (MUFFIN RELATED)                       *
 *this file contains the modifyMethod class as needed by the muff*
 *in api. it is not necessary for other proxies.                * 
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
 ****************************************************************/
package org.doit.muffin.filter;

import org.doit.muffin.*;
import java.awt.Frame;

public class modifyMethod implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    Frame modifyMethodFrame = new Frame ();
    Frame frame = null;

    public void setManager (FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }

    public Prefs getPrefs ()
    {
	return prefs;
    }

    public void viewPrefs ()
    {
	if (frame == null)
	{
	    frame = new modifyMethodFrame (prefs, this);
	}
	frame.setVisible (true);
    }
    
    public Filter createFilter ()
    {
	Filter f = new modifyMethodFilter (this);
	f.setPrefs (prefs);
	return f;
    }

    public void shutdown ()
    {
	if (frame != null)
	{
	    frame.dispose ();
	}
    }

    void save ()
    {
	manager.save (this);
    }
}
