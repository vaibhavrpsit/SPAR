/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *   
 * Rev 1.0  	Dec 08, 2014  	Deepshikha Singh     Resolution for LSIPL-FES:-Multiple Tender using Innoviti  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
/**
 * This site is created to enter Loyalty amount for Points Redemption transaction by using Credit card.  
 */
public class MAXPineLabLoyaltyAmountRedeemSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5349281263746320018L;

	public void arrive(BusIfc bus)
	{	
			POSUIManagerIfc ui =(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			//PromptAndResponseModel prompt = null;
			POSBaseBeanModel  model = new POSBaseBeanModel();
			//model.setPromptAndResponseModel(prompt);
			ui.showScreen(MAXPOSUIManagerIfc.LOYALTY_EMI_AMOUNT_SCREEN,model);
	}
}
