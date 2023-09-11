/**$*************************************************************
 *file: modifyClass.java (MUFFIN RELATED)                       *
 *this file contains the modifyClass class as needed by the muff*
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
 *                                                              *
 *Disclaimer:                                                   * 
 *This software is provided by the author AS IS.  The author    * 
 *DISCLAIMS any and all warranties of merchantability and fitnes* 
 *for a particular purpose.  In NO event shall the author be    *
 *LIABLE for any damages whatsoever arising in any way out of   * 
 *use of this software.                                         *
 ****************************************************************/
package org.doit.muffin.filter;

import org.doit.muffin.*;
import java.awt.Frame;

public class modifyClass implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    Frame modifyClassFrame = new Frame ();
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
	    frame = new modifyClassFrame (prefs, this);
	}
	frame.setVisible (true);
    }
    
    public Filter createFilter ()
    {
	Filter f = new modifyClassFilter (this);
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
