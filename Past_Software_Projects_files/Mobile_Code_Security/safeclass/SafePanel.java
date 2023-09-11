import java.awt.*;

public class SafePanel extends Panel{
  
  private static int numOfDialog = 0;

  public SafePanel()
    {
      if(numOfDialog < 5)
	{
	 numOfDialog++;
	}
      else
	{
	 Frame f = new Frame("success");
	 Dialog d = new Dialog(f);
         d .show();
	 numOfDialog = 0;
	 System.out.println("NUMBER OF WINDOWS EXCEEDED");
	 throw new AWTError("number of windows exceeded");
	}
    }
};
