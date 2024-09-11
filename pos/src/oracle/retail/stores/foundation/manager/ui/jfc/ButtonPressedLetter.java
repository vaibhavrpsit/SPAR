package oracle.retail.stores.foundation.manager.ui.jfc;

import oracle.retail.stores.foundation.tour.application.Letter;

public class ButtonPressedLetter extends Letter {
  
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final String NAME = "ButtonPressedLetter";
  
  protected String btnName;
  
  protected int btnNumber;
  
  public ButtonPressedLetter(String btnName) {
    this(btnName, 0);
  }
  
  public ButtonPressedLetter(String btnName, int btnNumber) {
    super(btnName);
    this.btnNumber = btnNumber;
    //System.out.println("btnNumber :"+btnNumber);
  }
  
  public int getNumber() {
    return this.btnNumber;
  }
}
