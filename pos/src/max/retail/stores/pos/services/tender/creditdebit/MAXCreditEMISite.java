/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
*
*	Rev 1.0   27 Oct 2017	 Anoop Seth	  Changes for Innoviti Integration CR
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
/**
 * This site is created to enter EMI amount for EMI transaction by using Credit card.  
 */
public class MAXCreditEMISite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5214618785095558590L;

	public void arrive(BusIfc bus)
   {	
		POSUIManagerIfc ui =(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		PromptAndResponseModel prompt = null;
		POSBaseBeanModel  model = new POSBaseBeanModel();
		model.setPromptAndResponseModel(prompt);
		ui.showScreen(MAXPOSUIManagerIfc.LOYALTY_EMI_AMOUNT_SCREEN,model);
		return;				
    }	
}
