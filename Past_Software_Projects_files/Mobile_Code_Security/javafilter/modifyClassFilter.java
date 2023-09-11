/**$*************************************************************
 *file: modifyClassFilter.java                                  *
 *filter to modify java byte code in java applets.(for methods) *
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

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;

import DE.fub.inf.JVM.*;
import DE.fub.inf.JVM.JavaClass.*;

import org.doit.muffin.*;
import org.doit.io.*;

/****************************************************************
 *modifyClassFilter : class                                     *
 *                                                              *
 *filter which modifies the classes in a *.class file.          *  
 ****************************************************************/
public class modifyClassFilter implements DE.fub.inf.JVM.Constants, ContentFilter{
 private static String catString = new String("Safe");

 private ConstantPool cpool;
 private Vector classNames;

 private String className_InStream = null;
 
 private Request request;
 private Reply reply; 
 private InputObjectStream in = null;
 private OutputObjectStream out = null;
 Prefs prefs;
 modifyClass factory;
/*METHODS BEGIN */
/****************************************************************
 *modifyClassFilter : constructor.                              *
 *                                                              *
 *calls fillClassNames func.                                    *
 ****************************************************************/
  public modifyClassFilter(modifyClass factory) 
  {
    classNames = new Vector();
    this.fillClassNames();
    this.factory = factory;
  }

/****************************************************************
 *fillClassNames : method.                                      *
 *reads a file in pwd called '.classnames' into the classNames  *
 *this file contains name of safe classes which should be added *
 *caveats:                                                      *
 *Exceptions handled:                                           *
 *filenot found.                                                *
 ****************************************************************/
 private void fillClassNames()
 {
   try {
    FileInputStream f = new FileInputStream("/theory4/u1/vganesh/.classnames");
    BufferedReader fstream = new BufferedReader(new InputStreamReader(f));
    while(fstream.ready()) {
          classNames.addElement(fstream.readLine());
	  /*DEBUG*/
          //System.out.println(classNames.elementAt(0));
    }
    f.close();
    fstream.close();
   }
   catch(Exception e) {
        System.out.println("File '.classnames' not found");
   }
 }

/****************************************************************
 *setConstantPool:                                              *
 ****************************************************************/
 public void setConstantPool(ConstantPool c) 
 {
    cpool = c;
 }

/****************************************************************
 *getConstantPool:                                              *
 ****************************************************************/
 public ConstantPool getConstantPool() 
 {
     return cpool;
 }

/****************************************************************
 *modifyUtf8: method                                            *
 *                                                              *
 *this function goes thru' classNames Vector and modifies the   *
 *Utf8 entries.                                                 *
 ****************************************************************/

 public void modifyUtf8() 
 {
   int count, len = cpool.getLength();
   
   for(Enumeration e = classNames.elements();e.hasMoreElements();) {
      String ss = new String((String)e.nextElement());	
    for(count = 1; count < len; count++) {
      Constant str = cpool.getConstant(count);
      //System.out.println(str.toString());
	   
     if(str.getTag() == 1)
      {
      if((((ConstantUtf8)str).getBytes()).equals(ss))
	{
         ss = catString + ss.substring(ss.lastIndexOf("/")+1);
         //((ConstantUtf8)str).setBytes(ss);
	ConstantUtf8 y = new ConstantUtf8(ss);
         cpool.setConstant(count,y);
        }
      }
    }
  }

   /*DEBUG*/
   for(count = 1; count < len; count++) {
       Constant str = cpool.getConstant(count);
       System.out.println(str.toString());
   }
}

/* MUFFIN INTERFACE BEGINS : */
/****************************************************************
 *needsFiltration : method (muffin interface : ContentFilter)   *
 *                                                              *
 *input:                                                        *
 ****************************************************************/
 public boolean needsFiltration(Request req, Reply rep)
 {
   this.request = req;
   this.reply = rep;
   String s = rep.getContentType();
   String url = req.getURL();
   int index = url.lastIndexOf("/");
   className_InStream = new String(url.substring(index+1)); 
 
   if(  (s != null && s.startsWith("application/java-vm"))
      ||(s != null && className_InStream.endsWith(".class")))
    { 
        if(className_InStream.startsWith("Safe"))
	  return false;	 
        return true;
    }
   else
     return false; 
 }

/****************************************************************
 *setInputObjectStream: method (muffin interface: ContentFilter)*
 *                                                              *
 *input:                                                        *
 ****************************************************************/
 public void setInputObjectStream (InputObjectStream in)
 {
   this.in = in;
 }

/****************************************************************
 *setOutputObjectStream: method(muffin interface: ContentFilter)*
 *                                                              *
 *input:                                                        *
 ****************************************************************/
 public void setOutputObjectStream (OutputObjectStream out)
 {
   this.out = out;
 }


/****************************************************************
 *setPrefs: method(muffin interface:  Filter)                   *
 *                                                              *
 *input:                                                        *
 ****************************************************************/
 public void setPrefs (Prefs prefs)
 {
   //this.prefs = prefs;
 }

/*INTERFACE : JUN PROXY */
/*
public void open(InputStream in)
{
   this.in = new BufferedInputStream(in);
}

public HttpResponse getHeaderObj()
{
}

public abstract int read()
{
}

public abstract int read(byte[] buf, int len)
{
}

public abstract int available()
{
}

public void close()
{
}
*/
   
/****************************************************************
 *main : method                                                 *
 *                                                              *
 *input:                                                        *
 *.class files.                                                 *
 ****************************************************************/
 public void run() {
     modifyClassFilter filter1 = new modifyClassFilter(this.factory);
     JavaClass presentClass;
     ClassParser parsedFile;
     File fileOfClassNames;

     try {
       ObjectStreamToInputStream f = new ObjectStreamToInputStream(this.in);
       parsedFile = new ClassParser(f, className_InStream);
       presentClass = parsedFile.parse();
       filter1.setConstantPool(presentClass.getConstantPool());
       filter1.modifyUtf8();
       presentClass.setConstantPool(filter1.getConstantPool());
       presentClass.dump("/theory4/u1/vganesh/muffin.class");
        try {
          ObjectStreamToOutputStream o = 
			new ObjectStreamToOutputStream(this.out);
          presentClass.dump(o);
	  o.flush();
	  o.close();
	  out.flush();
	  out.close(); 
        }
        catch(Exception e) {
  	  System.out.println("error in creating outputstream");
        }
     } 
     catch(Exception e) {
	System.out.println("error in reading class stream or parsing stream");
     }
  }
}
