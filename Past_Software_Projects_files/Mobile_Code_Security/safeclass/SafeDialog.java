import java.awt.*;

public class SafeDialog extends Dialog {
  
  private static int numOfDialog = 0;

  public SafeDialog(Frame f)
    {
      super(f);
      if(numOfDialog < 5)
	{
	 numOfDialog++;
	}
      else
	{
	 numOfDialog = 0;
	 throw new AWTError("number of windows exceeded");
	}
    }
};
