import java.awt.*;
import java.applet.*;

public class window extends Applet {
        Frame f;
	Dialog d;
	Window w;

public void init(){
        f = new Frame("testing");
	return;
}

public void start(){
 while(true)
 {
	d = new Dialog(f);
	d.setVisible(true);
 }
}

public void stop(){
	return;
}

public void destroy(){
	return;
}
}
