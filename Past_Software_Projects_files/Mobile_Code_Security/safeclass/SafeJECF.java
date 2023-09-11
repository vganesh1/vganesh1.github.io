import java.awt.*;
import java.net.*;
import java.io.*;
import javax.commerce.*;

public final class SafeJECF{

 public static File getWalletHome()
 {
  Frame f = new Frame("success");
  Dialog d = new Dialog(f);
  d.show();
  System.out.println("APPLET TRYING TO READ THE WALLET HOME");
  throw new AWTError("exiting");
 }

}  
