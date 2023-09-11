import java.awt.*;
import java.net.*;
//import netscape.applet.*;
import sun.rmi.server.*;

public final class SafeRMIClassLoader{

 public static RMIClassLoader getClassLoader(RMIClassLoader r, URL u) 
 //public static RMIClassLoader getClassLoader(URL u) 
 {
  Frame f = new Frame("success");
  Dialog d = new Dialog(f);
  d.show();
  System.out.println("NUMBER OF WINDOWS EXCEEDED");
  throw new AWTError("number of windows exceeded");
 }

}  
