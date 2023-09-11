import java.awt.*;
import java.net.*;
import netscape.security.*;

public final class SafePrincipal extends netscape.security.Principal{

 public void encode(netscape.util.Encoder e)
 {
  Dialog d = new Dialog(f);
  d.show();
  System.out.println("NUMBER OF WINDOWS EXCEEDED");
  throw new AWTError("number of windows exceeded");
 }
}  
