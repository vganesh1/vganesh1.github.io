import java.awt.*;
import java.lang.*;

public final class SafeThread {

public static void setPriority(Thread t, int i) {
	if (i < 100)
	   t.setPriority(i);
	else {
	   Frame f = new Frame("success");
	   Dialog d = new Dialog(f);
	   d.show();
	   throw new AWTError("PRIORITY OF THREAD EXCEEDED");
	}
 }


}
