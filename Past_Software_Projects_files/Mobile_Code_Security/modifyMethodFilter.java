/****************************************************************
 *file: modifyMethodFilter.java                                 *
 *filter to modify java byte code in java applets.(for methods) *
 *author : Vijay Ganesh. (vganesh@theory.stanford.edu)          *
 *                                                              *
 *also see:                                                     *
 *JavaClass Library Docs.standard java libraries. java vm.      *
 *Paper on 'JavaByteCode Modifcation by Inshik Shin'            *
 *other contact: amitp@cs.stanford.edu, mitchell@cs.stanford.edu*
 *this filter supports two interfaces namely the muffin & java  *
 *proxy interface by jun. the code related to muffin interface  *
 * is bunched together and marked with a comment //MUFFIN.      *
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
//while testing keep the package instr commented. when adding to muffin
//uncomment. (i spent enoromous amount of time to figure this out)
package org.doit.muffin.filter;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.String;
import java.lang.reflect.Array;

import DE.fub.inf.JVM.*;
import DE.fub.inf.JVM.ClassGen.*;
import DE.fub.inf.JVM.JavaClass.*;

import org.doit.muffin.*;
import org.doit.io.*;

/****************************************************************
 *modifyMethodFilter : class                                    *
 *                                                              * 
 *filter which modifies the methods in a class.                 *
 *methods:                                                      *
 *read in method names which need modifying from a file.        *
 *add ConstantClass entries.                                    *
 *add ConstantUtf8 entries.                                     *
 *modify ConstantPool methodRef entries.                        * 
 ****************************************************************/
public class modifyMethodFilter implements Constants, ContentFilter{
 private static String catString = new String("Safe");
 private static int MAX = 200;
 private static int INVOKEVIRTUAL_NUM = 0xb6; 
 private static int INVOKESPECIAL_NUM = 0xb7; 
 private static int INVOKESTATIC_NUM  = 0xb8; 
 private static int INVOKEINTERFACE_NUM = 0xb9; 

 private ConstantPool cpool;
 private Method[] listOfMethods;

 private Vector vectorMethodNames;
 private class  methodNames {
	public Vector inputMethods;
	public String inputClass;
 };

 private  int methodRefIndices[];

 private String className_InStream = null;
 private Request request;
 private Reply reply; 
 private InputObjectStream in = null;
 private OutputObjectStream out = null;
 Prefs prefs;
 modifyMethod factory;
/*METHODS BEGIN*/
/****************************************************************
 *modifyMethodFilter : constructor.                             *
 *                                                              * 
 *calls fillMethodNames func.                                   * 
 ****************************************************************/
 public modifyMethodFilter(modifyMethod factory) 
 {
   vectorMethodNames = new Vector();
   methodRefIndices  = new int[10];
   this.fillMethodNames();
   this.factory = factory;
 }

/****************************************************************
 *fillMethodNames : method.                                     * 
 *reads a file in pwd called '.methodnames' into the methodNames*
 *this file contains name of methods which should be modified.  *
 *caveats:                                                      *
 *                                                              *
 *string followed by a '.' r assumed to be classnames. all other*
 *strings are assumed to be methodnames. every string should be *
 *on a separate line. no check on formatting is done.           *
 *strings are directly read into 'tempMethodNames' without      *
 *regard to whether they r class names or method names for      *
 *efficiency reasons.                                           * 
 *Exceptions handled:                                           *
 *filenot found.                                                *
 ****************************************************************/
 private void fillMethodNames()
 {
   Vector tempMethodNames = new Vector();
   int readAheadLimit = 10;
   try {
    FileInputStream f = new FileInputStream("/theory4/u1/vganesh/.methodnames");
    BufferedReader fstream = new BufferedReader(new InputStreamReader(f));
    while(fstream.ready()) {
         fstream.mark(readAheadLimit);
         if('\n' != fstream.read())
           {
            fstream.reset(); 
            tempMethodNames.addElement(fstream.readLine());
           }
    }
    f.close();
    fstream.close();
   }
   catch(Exception e) {
	System.out.println("File '.methodnames' not found");
   }
   this.cleanNames(tempMethodNames);
 }

/****************************************************************
 *cleanNames : method                                           *
 *                                                              *
 *reads in a vector of strings.                                 *
 *fills the 'methodNames' classes and stuffs it into            *
 *'vectorMethodNames'. makes sure that 'methodNames.inputClass' *
 *has the class name and the 'methodNames.inputMethods has the  *
 *corresponding methods.                                        *
 *exceptions:                                                   *
 *throws an exception if the file format is screwed up.(mark=-1)* 
 ****************************************************************/
 private void cleanNames(Vector names_of_methods)
 {
   int count = 0, mark = -1;
   for(count = 0; count < names_of_methods.size(); count++) {
      String s = new String((String)names_of_methods.elementAt(count));
      if(s.endsWith(".")) {
  	methodNames m = new methodNames();
	m.inputClass = new String(s.replace('.',' '));
        m.inputMethods = new Vector();
	vectorMethodNames.addElement(m);
	mark++;		
      }
      else {
        try {
//System.out.println(mark);
	 methodNames x = ((methodNames)(vectorMethodNames.elementAt(mark)));
 	 (x.inputMethods).addElement(s);
        }
        catch(Exception e) {
	 System.out.println("error in file format in .methodnames");
	 System.out.println("1st line should have a classname");
	 System.out.println("classnames should end with a '.'");
        }
      }	
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
 *setMethods:                                                   *
 ****************************************************************/
 public void setMethods(Method[] m) 
 {
    listOfMethods = m;
 }

/****************************************************************
 *getMethods:                                                   *
 ****************************************************************/
 public Method[] getMethods() 
 {
    return listOfMethods;
 }

/****************************************************************
 *Modify_ConstantPool: method                                   *
 *                                                              *
 *this function goes thru' 'vectorMethodNames' and adds classes *
 *in it to the constantpool after converting them to Safe$ form.*
 *a new CONSTANT_Uft8 entry is made and a CONSTANT_Class entry  *
 *refering to the new CONSTANT_Uft8 is made.                    *
 *Then the CONSTANT_Methodref entry is modified to refer to the *
 *new CONSTANT_Class entry. since the method descriptor changed *
 *we also need to add a CONSTANT_Utf8 entry representing a sym- *
 *bolic name for the new method descriptor. Then the CONSTANT_  *
 *NameAndType entry is modified to refer to the new CONSTANT_Uft*
 *entry for the method descriptor. Now the CONSTANT_MethodRef   *
 *entry represents a new method.                                *
 ****************************************************************/
 public void modify_ConstantPool() 
 {
    int count = vectorMethodNames.size();
    int CONSTANT_Class_entryIndex = 0;
    int CONSTANT_Utf8_for_Newmethod = 0;
    int len = cpool.getLength();

//2*count takes into account the new entries that will be made
//into the constantpool
    Vector tempCpool = new Vector(len + 2*count);
    methodNames tempMethodNames1;

//copy existing cpool into the Vector tempCpool. 
    for(int c = 0; c < len; c++) {
       tempCpool.insertElementAt(cpool.getConstant(c),c);
    }
  
   int x = 0;

//this for loop is for every class which needs modification
//as specified by the user in the .methodnames file
   for(int t=0; t < count;t++) {
      tempMethodNames1 = ((methodNames)(vectorMethodNames.elementAt(t))); 
      String inputclassname = new String(tempMethodNames1.inputClass);
      inputclassname = inputclassname.trim();
      ConstantUtf8 str1 = new ConstantUtf8(catString + 
	         inputclassname.substring(inputclassname.lastIndexOf("/")+1));

//add classname to the constantpool
      tempCpool.insertElementAt(str1,len + x);
      ConstantClass str2 = new ConstantClass(len + x);
      x++;

//add ConstantClass entry for the newly inserted class
      tempCpool.insertElementAt(str2,len + x);
      CONSTANT_Class_entryIndex = len + x;
      x++;

//for every method of this class which needs modificatoins 
//as specified by the user in the .methodnames file
      Enumeration e = (tempMethodNames1.inputMethods).elements();

//the counter 'x' just keeps track of the end of constant pool
      for(;e.hasMoreElements();x++) {
         String  ss = new String((String)(e.nextElement()));
         Enumeration f = tempCpool.elements();

//the first element of hte constant pool is generally a 0.
//by calling f.nextElement() i am skipping it from further processing
//'f' is the enumeration of the temporary constant pool.
         try { 
           f.nextElement();
         } catch(Exception eee) {
	     System.out.println("fatal error. constant pool empty");
         }

//for all other elements of temporary constantpool
         for(int i = 0,methodRefIndex = 0;f.hasMoreElements();) {
            Object ff = (Constant)(f.nextElement());

//very strange testcase. there was a null entry in one of the constantpools??
	    if(null == ff)
              {
               methodRefIndex++;
	       continue;
              }
            else
              methodRefIndex++;

//if tag=10 then we have a methodref entry in our hands
            if(10 == ((Constant)ff).getTag()){
              int z =((ConstantMethodref)ff).getNameAndTypeIndex(); 
              int zz=((ConstantNameAndType)
		        (tempCpool.elementAt(z))).getNameIndex();
              int zzz=((ConstantNameAndType)
		        (tempCpool.elementAt(z))).getSignatureIndex();

              String pp;
              if(ss.endsWith("/static"))
                pp = new String(ss.substring(0,ss.lastIndexOf("/")));
              else
                pp = new String(ss);

              if((((ConstantUtf8)
		      (tempCpool.elementAt(zz))).getBytes()).equals(pp)){

//modifying constant class entry in constantmethodref
	         ((ConstantMethodref)
	              (tempCpool.elementAt(methodRefIndex))).
				   setClassIndex(CONSTANT_Class_entryIndex);

	      String methodSignature =
	             new String(((ConstantUtf8)
				tempCpool.elementAt(zzz)).getBytes());
              if(!(ss.endsWith("/static"))){
	         methodSignature = "(L" + inputclassname + ";" +
	         methodSignature.substring(methodSignature.indexOf("(") + 1);
              }
	      
              ConstantUtf8 str3 = new ConstantUtf8(methodSignature);
    	      tempCpool.insertElementAt(str3,len + x);
	      
              ((ConstantNameAndType)
		 (tempCpool.elementAt(z))).setSignatureIndex(len + x);
	      x++;
//Array class provides static methods to modify/create arrays. here the
//array in question is methodRefIndices. "i" merely acts as a counter of
//the array. the variable 'methodRefIndex' provides the index of a methodRef
//entry in the constantpool. methodRefIndex is stored in the array "methodRef
//Indices". 
	      Array.setInt(methodRefIndices,i++,methodRefIndex);
	  }//if ends here
        }
     }
   }
 }
   Constant[] apool = new Constant[tempCpool.size()];
   tempCpool.copyInto(apool);
   ConstantPool xpool = new ConstantPool(apool);
   cpool = xpool;

/*DEBUG:*/
/* 
   System.out.println(cpool.getLength());
   for(int c = 1; c < cpool.getLength(); c++) {
      Constant str = cpool.getConstant(c);
      if(str != null)
        System.out.println(str.toString());
   }
*/
}
  
/****************************************************************
 *modifyInvokeInstrs:                                           *
 *input: list of methods					*
 *output:bytecode modified methods.all the invokevrital are mod-*
 *ified to invokestatic. it is a simple one to one conversion.  *
 *also refer byte code modificatoin paper by insik shin.        *
 ****************************************************************/
 public void modifyInvokeInstrs() 
 {
   int length = listOfMethods.length;
   int methodRefIndicesLength = Array.getLength(methodRefIndices);

//for every method in the class
   for(int num = 0; num < length; num++)
   {
     Code c = listOfMethods[num].getCode();
     InstructionList iL = new InstructionList(c.getCode());

//String ssss = new String(c.toString());

//for every instructioni, nextElem, of the method 
     for (Enumeration e = iL.elements(); e.hasMoreElements();)
      {
       Object nextElem = e.nextElement();
       if(((InstructionHandle)
	    (nextElem)).getInstruction().getTag() == INVOKEVIRTUAL_NUM)
	  {
//System.out.println(((InstructionHandle)(nextElem)).getInstruction().toString(cpool));
//in this for loop we check whether each of the methods named by the user
//is being referenced and hence whether the invokevirtual instr. needs
//modification 
	  for(int number = 0;number < methodRefIndicesLength; number++) {
 		if(((CPInstruction)((InstructionHandle)
		                   (nextElem)).getInstruction()).getIndex() 
				  == (Array.getInt(methodRefIndices,number)))
		  {
//System.out.println("Iaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaam in here");
		    Instruction j =
		     new INVOKESTATIC((Array.getInt(methodRefIndices,number)));
		    ((InstructionHandle)(nextElem)).setInstruction(j);
		  }
	   }
//System.out.println(((InstructionHandle)(nextElem)).getInstruction().toString(cpool));
          }
      }
      c.setCode(iL.getByteCode());
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
//System.out.println(className_InStream); 
   if(  (s != null && s.startsWith("application/java-vm"))
      ||(s != null && className_InStream.endsWith(".class")))
    { 
        if(className_InStream.startsWith(catString))
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
/*MUFFIN METHODS END HERE*/

/****************************************************************
 *main : method                                                 *
 *                                                              *
 *input:                                                        *
 **.class files.                                                *
 ****************************************************************/
 public void run()
 {
   modifyMethodFilter filter1 = new modifyMethodFilter(this.factory);
   JavaClass presentClass;
   ClassParser parsedFile;
   File fileOfClassNames;
   Method[] m;

   try {
    ObjectStreamToInputStream f = new ObjectStreamToInputStream(this.in);
    parsedFile = new ClassParser(f, className_InStream);
    presentClass = parsedFile.parse();
    filter1.setConstantPool(presentClass.getConstantPool());
    filter1.setMethods(presentClass.getMethods()); 
    filter1.modify_ConstantPool();
    filter1.modifyInvokeInstrs();
    presentClass.setConstantPool(filter1.getConstantPool());
    presentClass.setMethods(filter1.getMethods());
    presentClass.dump("/theory4/u1/vganesh/muffin.class");
    try {
	ObjectStreamToOutputStream o =
	              new ObjectStreamToOutputStream(this.out);
	presentClass.dump(o);
	o.flush();
	o.close();
	out.flush();
	out.close();
    }catch(Exception ee) {
	System.out.println("error in creating outputstream");
   }
 }catch(Exception e) {
    System.out.println("error in main");
  }
 }
}
