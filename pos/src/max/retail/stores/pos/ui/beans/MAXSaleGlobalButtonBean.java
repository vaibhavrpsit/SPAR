/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.beans.ActionNotFoundException;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;

public class MAXSaleGlobalButtonBean extends MAXGlobalNavigationButtonBean 
implements GlobalButtonListener{

	  /**
	 * 
	 */
	private static final long serialVersionUID = -8574563695659264045L;

	public void enableButton(String actionName, boolean enable)
	    {
	        try
	        {
	            getUIAction(actionName).setEnabled(enable); 
	        }
	        catch(ActionNotFoundException e)
	        {
	            Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXSaleGlobalButtonBean.class);
	            logger.warn( "SaleLocalButtonBean.enbleButton() did not find the " + actionName + " action.");
	        }
	    }

}
