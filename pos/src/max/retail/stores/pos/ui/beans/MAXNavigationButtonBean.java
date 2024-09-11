/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBean;
import oracle.retail.stores.pos.ui.beans.UIAction;

public class MAXNavigationButtonBean extends NavigationButtonBean{
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5803225403133451389L;

	public static int MAX_HORIZONTAL        = 6  ; 
    
    /****** End HC Update ***********/
    
    public static int MAX_VERTICAL          = 8;
    
    public void configureButtons(ButtonSpec[] buttonSpecs)
    {
        int barCount    = 1;
        int buttonCount = MAX_HORIZONTAL;
        if(orientation == VERTICAL)
        {
            buttonCount = MAX_VERTICAL;
            if (buttonSpecs.length > MAX_VERTICAL)
            {
                // Calculate the number of button bars needed.
                // If there are more than MAX_VERTICAL buttons, then one button
                // per bar will be decicated to the "More" key;  Therefore, the
                // number of buttons than can be used for other functions is
                // MAX_VERTICAL - 1.
                int funcButtons = MAX_VERTICAL - 1;
                barCount = buttonSpecs.length / funcButtons;

                // The integer division above may have remainder; if so
                // then we need another button bar for remainder number of
                // buttons.
                int rem  = buttonSpecs.length % funcButtons;
                if (rem > 0)
                {
                    barCount++;
                }
            }
        }
        UIAction[][] actions = createActions(buttonSpecs, barCount, buttonCount);
        initialize(actions);
    }
}
